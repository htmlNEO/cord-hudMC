package com.chaoscubed.cordhud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class CordHudClient implements ClientModInitializer {
    public static final String MOD_ID = "cordhud";

    private static KeyBinding openTrackerKey;

    @Override
    public void onInitializeClient() {
        openTrackerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cordhud.open_tracker",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_6,
                "category.cordhud.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openTrackerKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new PlayerTrackerScreen());
                }
            }
        });
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
