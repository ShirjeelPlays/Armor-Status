package com.example.shirjeel_plays.armorstatus.client;

import com.example.shirjeel_plays.armorstatus.config.ConfigScreen;
import com.example.shirjeel_plays.armorstatus.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class ArmorStatusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load();
        HudRenderCallback.EVENT.register(new ArmorStatusHud());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("armorstatus")
                .then(ClientCommandManager.literal("config")
                    .executes(context -> {
                        // setScreen must be run on the main thread
                        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ConfigScreen(null)));
                        return 1;
                    }))
            );
        });
    }
}
