package com.nindybun.burnergun.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static KeyBinding burnergun_gui_key = new KeyBinding("key.burnergun_gui_key", GLFW.GLFW_KEY_C, "key.categories.burnergun");
    public static KeyBinding burnergun_light_key = new KeyBinding("key.burnergun_light_key", GLFW.GLFW_KEY_R, "key.categories.burnergun");
    public static KeyBinding burnergun_lightPlayer_key = new KeyBinding("key.burnergun_lightplayer_key", GLFW.GLFW_KEY_G, "key.categories.burnergun");
    public static KeyBinding burnergun_veinMiner_key = new KeyBinding("key.burnergun_veinminer_key", GLFW.GLFW_KEY_LEFT_ALT, "key.categories.burnergun");

    public static void register()
    {
        ClientRegistry.registerKeyBinding(burnergun_gui_key);
        ClientRegistry.registerKeyBinding(burnergun_light_key);
        ClientRegistry.registerKeyBinding(burnergun_lightPlayer_key);
        ClientRegistry.registerKeyBinding(burnergun_veinMiner_key);
    }


}
