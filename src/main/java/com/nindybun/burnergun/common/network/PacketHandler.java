package com.nindybun.burnergun.common.network;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.network.packets.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "2";
    private static int index = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(BurnerGun.MOD_ID, "main"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register(){
        int id = 0;
        INSTANCE.registerMessage(id++, PacketOpenBurnerGunGui.class, PacketOpenBurnerGunGui::encode, PacketOpenBurnerGunGui::decode, PacketOpenBurnerGunGui.Handler::handle);
        INSTANCE.registerMessage(id++, PacketOpenAutoFuelGui.class, PacketOpenAutoFuelGui::encode, PacketOpenAutoFuelGui::decode, PacketOpenAutoFuelGui.Handler::handle);
        INSTANCE.registerMessage(id++, PacketOpenTrashGui.class, PacketOpenTrashGui::encode, PacketOpenTrashGui::decode, PacketOpenTrashGui.Handler::handle);
        INSTANCE.registerMessage(id++, PacketOpenAutoSmeltGui.class, PacketOpenAutoSmeltGui::encode, PacketOpenAutoSmeltGui::decode, PacketOpenAutoSmeltGui.Handler::handle);
        INSTANCE.registerMessage(id++, PacketOpenUpgradeBagGui.class, PacketOpenUpgradeBagGui::encode, PacketOpenUpgradeBagGui::decode, PacketOpenUpgradeBagGui.Handler::handle);
        INSTANCE.registerMessage(id++, PacketChangeVolume.class, PacketChangeVolume::encode, PacketChangeVolume::decode, PacketChangeVolume.Handler::handle);
        INSTANCE.registerMessage(id++, PacketToggleTrashFilter.class, PacketToggleTrashFilter::encode, PacketToggleTrashFilter::decode, PacketToggleTrashFilter.Handler::handle);
        INSTANCE.registerMessage(id++, PacketToggleSmeltFilter.class, PacketToggleSmeltFilter::encode, PacketToggleSmeltFilter::decode, PacketToggleSmeltFilter.Handler::handle);
        INSTANCE.registerMessage(id++, PacketUpdateUpgrade.class, PacketUpdateUpgrade::encode, PacketUpdateUpgrade::decode, PacketUpdateUpgrade.Handler::handle);
        INSTANCE.registerMessage(id++, PacketUpdateGun.class, PacketUpdateGun::encode, PacketUpdateGun::decode, PacketUpdateGun.Handler::handle);
        INSTANCE.registerMessage(id++, PacketChangeSettings.class, PacketChangeSettings::encode, PacketChangeSettings::decode, PacketChangeSettings.Handler::handle);
        INSTANCE.registerMessage(id++, PacketRefuel.class, PacketRefuel::encode, PacketRefuel::decode, PacketRefuel.Handler::handle);
        INSTANCE.registerMessage(id++, PacketSpawnLightAtPlayer.class, PacketSpawnLightAtPlayer::encode, PacketSpawnLightAtPlayer::decode, PacketSpawnLightAtPlayer.Handler::handle);
        INSTANCE.registerMessage(id++, PacketSpawnLightAtRaycast.class, PacketSpawnLightAtRaycast::encode, PacketSpawnLightAtRaycast::decode, PacketSpawnLightAtRaycast.Handler::handle);
        INSTANCE.registerMessage(id++, PacketChangeColor.class, PacketChangeColor::encode, PacketChangeColor::decode, PacketChangeColor.Handler::handle);

    }

    public static void send(Object msg, Supplier playerEntity){
        INSTANCE.send(PacketDistributor.PLAYER.with(playerEntity), msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer))
            INSTANCE.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object msg){
        INSTANCE.sendToServer(msg);
    }



}
