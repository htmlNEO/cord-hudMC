package com.chaoscubed.cordhud.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PlayerTrackerScreen extends Screen {
    private static final int ROW_HEIGHT = 14;
    private static final int LIST_TOP = 50;

    private TextFieldWidget searchBox;
    private SortMode sortMode = SortMode.NAME;
    private int scrollOffset = 0;

    private enum SortMode {
        NAME, DISTANCE
    }

    protected PlayerTrackerScreen() {
        super(Text.translatable("screen.cordhud.tracker.title"));
    }

    @Override
    protected void init() {
        searchBox = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100, 22, 200, 18,
                Text.translatable("screen.cordhud.tracker.search")
        );
        searchBox.setPlaceholder(Text.translatable("screen.cordhud.tracker.search"));
        addDrawableChild(searchBox);
        setInitialFocus(searchBox);

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("screen.cordhud.tracker.sort", sortMode.name()),
                btn -> {
                    sortMode = sortMode == SortMode.NAME ? SortMode.DISTANCE : SortMode.NAME;
                    btn.setMessage(Text.translatable("screen.cordhud.tracker.sort", sortMode.name()));
                }
        ).dimensions(this.width / 2 + 110, 20, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

        if (this.client == null || this.client.world == null || this.client.player == null) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("screen.cordhud.tracker.no_world"), this.width / 2, LIST_TOP, 0xFF5555);
            return;
        }

        String filter = searchBox.getText().toLowerCase();
        List<AbstractClientPlayerEntity> players = new ArrayList<>(this.client.world.getPlayers());
        players.removeIf(p -> p == this.client.player);
        players.removeIf(p -> !p.getGameProfile().getName().toLowerCase().contains(filter));

        if (sortMode == SortMode.NAME) {
            players.sort(Comparator.comparing(p -> p.getGameProfile().getName().toLowerCase()));
        } else {
            players.sort(Comparator.comparingDouble(p -> p.squaredDistanceTo(this.client.player)));
        }

        int visibleRows = Math.max(1, (this.height - LIST_TOP - 20) / ROW_HEIGHT);
        scrollOffset = MathHelper.clamp(scrollOffset, 0, Math.max(0, players.size() - visibleRows));

        if (players.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("screen.cordhud.tracker.none_found"), this.width / 2, LIST_TOP, 0xAAAAAA);
            return;
        }

        int y = LIST_TOP;
        for (int i = scrollOffset; i < Math.min(players.size(), scrollOffset + visibleRows); i++) {
            AbstractClientPlayerEntity p = players.get(i);
            double dist = Math.sqrt(p.squaredDistanceTo(this.client.player));
            String dim = p.getWorld().getRegistryKey().getValue().getPath();
            String line = String.format("%s  x=%.0f y=%.0f z=%.0f  [%s]  %.0fm away",
                    p.getGameProfile().getName(), p.getX(), p.getY(), p.getZ(), dim, dist);
            context.drawTextWithShadow(this.textRenderer, line, this.width / 2 - 150, y, 0xFFFFFF);
            y += ROW_HEIGHT;
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.cordhud.tracker.count", players.size()),
                this.width / 2, this.height - 14, 0xAAAAAA);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int) Math.signum(verticalAmount);
        if (scrollOffset < 0) scrollOffset = 0;
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
