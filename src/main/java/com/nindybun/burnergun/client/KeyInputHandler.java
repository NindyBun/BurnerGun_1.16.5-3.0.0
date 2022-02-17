package com.nindybun.burnergun.client;

import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketOpenBurnerGunGui;
import com.nindybun.burnergun.common.network.packets.PacketSpawnLightAtPlayer;
import com.nindybun.burnergun.common.network.packets.PacketSpawnLightAtRaycast;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyInputHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (Keybinds.burnergun_light_key.isDown() && event.getAction() == 1 && Minecraft.getInstance().screen == null)
            PacketHandler.sendToServer(new PacketSpawnLightAtRaycast());
        if (Keybinds.burnergun_lightPlayer_key.isDown() && event.getAction() == 1 && Minecraft.getInstance().screen == null)
            PacketHandler.sendToServer(new PacketSpawnLightAtPlayer());
        if (Keybinds.burnergun_gui_key.isDown() && event.getAction() == 1 && Minecraft.getInstance().screen == null
                && (player.getMainHandItem().getItem() instanceof BurnerGunMK1 || player.getOffhandItem().getItem() instanceof BurnerGunMK1
                || player.getMainHandItem().getItem() instanceof BurnerGunMK2 || player.getOffhandItem().getItem() instanceof BurnerGunMK2)){
            PacketHandler.sendToServer(new PacketOpenBurnerGunGui());
        }
    }
}
