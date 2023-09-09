package io.github.racoondog.msl.credits.impl;

import io.github.racoondog.msl.credits.api.Credit;
import io.github.racoondog.msl.credits.api.DrawFunction;
import io.github.racoondog.msl.credits.api.Section;
import io.github.racoondog.msl.credits.api.TitleScreenCredits;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.CommitsScreen;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class TitleScreenCreditsImpl implements TitleScreenCredits {
    private static final DrawFunction EMPTY = (context, credit, y) -> {};
    private static final DrawFunction DEFAULT = (context, credit, y) -> {
        int x = mc.currentScreen.width - 3 - credit.width;

        synchronized (credit.sections) {
            for (var section : credit.sections) {
                context.drawTextWithShadow(mc.textRenderer, section.text, x, y, -1);
                x += section.width;
            }
        }
    };

    private static final Map<MeteorAddon, CreditEntry> drawFunctions = new Reference2ObjectOpenHashMap<>();
    private static final List<CreditEntry> credits = new ObjectArrayList<>();
    private static List<Consumer<Credit>> creditModifications = new ObjectArrayList<>();
    private static boolean initialized = false;

    public static void registerCustomDrawFunction(MeteorAddon addon, DrawFunction function, boolean shouldUpdateY) {
        drawFunctions.put(addon, new CreditEntry(function, shouldUpdateY));
    }

    public static void registerConsumerDrawFunction(MeteorAddon addon, Consumer<DrawContext> consumer, boolean shouldUpdateY) {
        DrawFunction drawFunction = (context, credit, y) -> consumer.accept(context);
        drawFunctions.put(addon, new CreditEntry(drawFunction, shouldUpdateY));
    }

    public static void registerDefaultDrawFunction(MeteorAddon addon) {
        drawFunctions.put(addon, new CreditEntry(DEFAULT, false));
    }

    public static void registerEmptyDrawFunction(MeteorAddon addon) {
        drawFunctions.put(addon, new CreditEntry(EMPTY, false));
    }

    public static void registerCreditModification(MeteorAddon addon, Consumer<Credit> consumer) {
        if (initialized) {
            applyModification(drawFunctions.get(addon).credit, consumer);
            sortCredits();
        } else {
            creditModifications.add(credit -> {
                if (credit.addon == addon) consumer.accept(credit);
            });
        }
    }

    public static void registerGlobalCreditModification(Consumer<Credit> consumer) {
        if (initialized) throw new IllegalStateException("Could not register global credit modification, title screen already initialized!");
        creditModifications.add(consumer);
    }

    public static boolean hasOutdated(Credit credit) {
        for (var section : credit.sections) {
            if (section == OutdatedMarker.MARKER) return  true;
        }
        return false;
    }

    // Implementation

    private static void init() {
        initialized = true;

        add(MeteorClient.ADDON);
        AddonManager.ADDONS.forEach(TitleScreenCreditsImpl::add);

        // Credit modifications
        for (var creditModification : creditModifications) {
            for (var creditEntry : credits) {
                applyModification(creditEntry.credit, creditModification);
            }
        }

        // Make immutable, throws if used after this point.
        creditModifications = List.of();

        sortCredits();

        MeteorExecutor.execute(() -> {
            for (var entry : credits) {
                GithubRepo repo = entry.credit.addon.getRepo();
                String commit = entry.credit.addon.getCommit();
                if (repo == null || commit == null) continue;

                Response res = Http.get(String.format("https://api.github.com/repos/%s/branches/%s", repo.getOwnerName(), repo.branch())).sendJson(Response.class);

                if (res != null && !commit.equals(res.commit.sha)) {
                    entry.credit.outdated = true;
                    synchronized (entry.credit.sections) {
                        entry.credit.sections.add(1, OutdatedMarker.MARKER);
                        entry.credit.calculateWidth();
                    }
                }
            }
        });
    }

    private static void add(MeteorAddon addon) {
        CreditEntry entry = drawFunctions.computeIfAbsent(addon, meteorAddon -> new CreditEntry(DEFAULT, true));
        Credit credit = entry.credit = new Credit(addon);
        Color color = addon.color;

        String name = addon.name != null ? addon.name : addon.getPackage().substring(addon.getPackage().lastIndexOf("."));
        credit.sections.add(new Section(name, color == null ? RED : color.getPacked()));
        if (addon.authors != null && addon.authors.length >= 1) {
            credit.sections.add(new Section(" by ", GRAY));
            for (int i = 0; i < addon.authors.length; i++) {
                if (i > 0) credit.sections.add(new Section(i == addon.authors.length - 1 ? ", & " : ", ", GRAY));
                credit.sections.add(new Section(addon.authors[i], WHITE));
            }
        }

        credit.calculateWidth();
        credits.add(entry);
    }

    public static void render(DrawContext context) {
        if (!initialized) init();

        int y = 3;
        for (var entry : credits) {
            entry.drawFunction.accept(context, entry.credit, y);
            if (entry.updatesY) y += mc.textRenderer.fontHeight + 2;
        }
    }

    public static boolean onClicked(double mouseX, double mouseY) {
        int y = 3;
        int height = mc.textRenderer.fontHeight + 2;
        for (var entry : credits) {
            if (!entry.updatesY || entry.drawFunction == EMPTY) continue;

            int x = mc.currentScreen.width - 3 - entry.credit.width;
            if (mouseX >= x && mouseX <= x + entry.credit.width && mouseY >= y && mouseY <= y + height) {
                if (entry.credit.addon.getRepo() != null && entry.credit.addon.getCommit() != null) {
                    mc.setScreen(new CommitsScreen(GuiThemes.get(), entry.credit.addon));
                    return true;
                }
            }

            y += height;
        }
        return false;
    }

    private static void applyModification(Credit credit, Consumer<Credit> consumer) {
        synchronized (credit.sections) {
            consumer.accept(credit);
            if (credit.outdated && !hasOutdated(credit)) credit.sections.add(1, OutdatedMarker.MARKER);
            credit.calculateWidth();
        }
    }

    private static void sortCredits() {
        credits.sort(Comparator.comparingInt(value -> value.credit.addon == MeteorClient.ADDON ? Integer.MIN_VALUE : -value.credit.width));
    }

    private TitleScreenCreditsImpl() {}

    public static class OutdatedMarker extends Section {
        private static final Text TEXT = Text.literal("*").styled(s -> s.withColor(RED));
        public static final OutdatedMarker MARKER = new OutdatedMarker();

        private OutdatedMarker() {
            super(TEXT);
        }
    }

    public static class Response {
        public Commit commit;
    }

    public static class Commit {
        public String sha;
    }
}
