package com.example.shirjeel_plays.armorstatus.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;

    public ConfigScreen(Screen parent) {
        super(Text.of("ArmorStatus Config"));
        this.parent = parent;
        this.config = ModConfig.get();
    }

    @Override
    protected void init() {
        int y = 40;
        int center = width / 2;

        // Enabled Toggle
        this.addDrawableChild(ButtonWidget.builder(Text.of("Enabled: " + config.enabled), button -> {
            config.enabled = !config.enabled;
            button.setMessage(Text.of("Enabled: " + config.enabled));
        }).dimensions(center - 100, y, 200, 20).build());
        y += 24;

        // Position Cycle
        this.addDrawableChild(ButtonWidget.builder(Text.of("Position: " + config.position), button -> {
            config.position = config.position.next();
            button.setMessage(Text.of("Position: " + config.position));
        }).dimensions(center - 100, y, 200, 20).build());
        y += 24;

        // Scale Slider
        this.addDrawableChild(new ScaleSlider(center - 100, y, 200, 20, Text.of("Scale: " + String.format("%.1f", config.scale)), config.scale));
        y += 24;

        // Armor Toggles
        this.addDrawableChild(ButtonWidget.builder(Text.of("Helmet: " + config.showHelmet), button -> {
            config.showHelmet = !config.showHelmet;
            button.setMessage(Text.of("Helmet: " + config.showHelmet));
        }).dimensions(center - 100, y, 98, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Chestplate: " + config.showChestplate), button -> {
            config.showChestplate = !config.showChestplate;
            button.setMessage(Text.of("Chestplate: " + config.showChestplate));
        }).dimensions(center + 2, y, 98, 20).build());
        y += 24;

        this.addDrawableChild(ButtonWidget.builder(Text.of("Leggings: " + config.showLeggings), button -> {
            config.showLeggings = !config.showLeggings;
            button.setMessage(Text.of("Leggings: " + config.showLeggings));
        }).dimensions(center - 100, y, 98, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Boots: " + config.showBoots), button -> {
            config.showBoots = !config.showBoots;
            button.setMessage(Text.of("Boots: " + config.showBoots));
        }).dimensions(center + 2, y, 98, 20).build());
        y += 24;

        // Done Button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Done"), button -> {
            ModConfig.save();
            this.client.setScreen(this.parent);
        }).dimensions(center - 100, height - 40, 200, 20).build());
    }

    @Override
    public void close() {
        ModConfig.save();
        super.close();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    private class ScaleSlider extends SliderWidget {
        public ScaleSlider(int x, int y, int width, int height, Text text, double value) {
            super(x, y, width, height, text, (Math.max(0.5, Math.min(2.0, value)) - 0.5) / 1.5);
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            // value is 0.0 to 1.0, mapped to 0.5 to 2.0
            double scaleVal = 0.5 + (this.value * 1.5);
            this.setMessage(Text.of("Scale: " + String.format("%.1f", scaleVal)));
        }

        @Override
        protected void applyValue() {
            config.scale = (float) (0.5 + (this.value * 1.5));
        }
    }
}
