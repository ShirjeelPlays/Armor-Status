package com.example.shirjeel_plays.armorstatus.client;

import com.example.shirjeel_plays.armorstatus.config.HudPosition;
import com.example.shirjeel_plays.armorstatus.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ArmorStatusHud implements HudRenderCallback {

    private record HudEntry(ItemStack stack, String text, int color) {}

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ModConfig config = ModConfig.get();
        if (!config.enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) return;

        List<HudEntry> entries = new ArrayList<>();

        Iterable<ItemStack> armorItems = client.player.getArmorItems();
        List<ItemStack> items = new ArrayList<>();
        armorItems.forEach(items::add);
        
        // Head (index 3), Chest (2), Legs (1), Feet (0)
        if (config.showHelmet && items.size() > 3 && !items.get(3).isEmpty()) addArmorEntry(items.get(3), entries);
        if (config.showChestplate && items.size() > 2 && !items.get(2).isEmpty()) addArmorEntry(items.get(2), entries);
        if (config.showLeggings && items.size() > 1 && !items.get(1).isEmpty()) addArmorEntry(items.get(1), entries);
        if (config.showBoots && items.size() > 0 && !items.get(0).isEmpty()) addArmorEntry(items.get(0), entries);

        if (entries.isEmpty()) return;

        drawHud(context, client, entries, config);
    }

    private void addArmorEntry(ItemStack stack, List<HudEntry> entries) {
        if (!stack.isDamageable()) {
            return;
        }

        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamage();
        int durability = maxDamage - currentDamage;
        float percentage = (float) durability / maxDamage;
        
        int percentInt = Math.round(percentage * 100);
        String text = String.valueOf(durability);
        int color = 0xFFFFFF; // White text to match the image
        
        entries.add(new HudEntry(stack, text, color));
    }

    private int getDurabilityColor(float percentage) {
        if (percentage >= 0.8f) {
             return 0x55FF55; // Green
        } else if (percentage >= 0.6f) {
            return interpolateColor(0xFFFF55, 0x55FF55, (percentage - 0.6f) / 0.2f); // Yellow to Green
        } else if (percentage >= 0.4f) {
            return interpolateColor(0xFF5555, 0xFFFF55, (percentage - 0.4f) / 0.2f); // Red to Yellow
        } else if (percentage >= 0.2f) {
            return interpolateColor(0xAA0000, 0xFF5555, (percentage - 0.2f) / 0.2f); // Dark Red to Red
        } else {
            return interpolateColor(0xAA00AA, 0xAA0000, percentage / 0.2f); // Purple to Dark Red
        }
    }

    private int interpolateColor(int color1, int color2, float factor) {
        factor = MathHelper.clamp(factor, 0f, 1f);
        
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        
        return (r << 16) | (g << 8) | b;
    }

    private void drawHud(DrawContext context, MinecraftClient client, List<HudEntry> entries, ModConfig config) {
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        
        float scale = config.scale;
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        // Ensure items render above the background
        context.getMatrices().translate(0.0F, 0.0F, 100.0F);
        
        int scaledWidth = (int) (screenWidth / scale);
        int scaledHeight = (int) (screenHeight / scale);
        
        // Item size is 16x16
        int itemSize = 16;
        int padding = 2;
        int lineHeight = Math.max(itemSize, client.textRenderer.fontHeight) + padding;
        int totalHeight = entries.size() * lineHeight;
        
        int startX = 0;
        int startY = 0;
        
        HudPosition pos = config.position;
        boolean isRightSide = (pos == HudPosition.TOP_RIGHT || pos == HudPosition.MIDDLE_RIGHT || pos == HudPosition.BOTTOM_RIGHT);
        
        switch (pos) {
            case TOP_LEFT:
            case TOP_RIGHT:
                startY = 4;
                break;
            case MIDDLE_LEFT:
            case MIDDLE_RIGHT:
                startY = (scaledHeight - totalHeight) / 2;
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                startY = scaledHeight - totalHeight - 4;
                break;
        }
        
        // Horizontal position setup
        // Left: 4px margin
        // Right: 4px margin from edge
        
        for (int i = 0; i < entries.size(); i++) {
            HudEntry entry = entries.get(i);
            String line = entry.text;
            int color = entry.color;
            int textWidth = client.textRenderer.getWidth(line);
            
            int y = startY + (i * lineHeight);
            
            // Calculate X based on alignment
            // Left: [Item] [Text]
            // Right: [Text] [Item]
            
            int itemX;
            int textX;
            
            if (isRightSide) {
                // Right aligned
                // Item is at scaledWidth - 4 - 16
                // Text is to the left of Item
                itemX = scaledWidth - 4 - 16;
                textX = itemX - 2 - textWidth; // 2px padding between text and item
            } else {
                // Left aligned
                // Item is at 4
                // Text is to the right of Item
                itemX = 4;
                textX = itemX + 16 + 2; // 16px item + 2px padding
            }
            
            // Draw Item
            context.drawItem(entry.stack, itemX, y);
            
            // Draw Text (centered vertically relative to item)
            int textY = y + (itemSize - client.textRenderer.fontHeight) / 2 + 1; // +1 for better visual alignment
            context.drawTextWithShadow(client.textRenderer, line, textX, textY, color);
        }
        
        context.getMatrices().pop();
    }
}
