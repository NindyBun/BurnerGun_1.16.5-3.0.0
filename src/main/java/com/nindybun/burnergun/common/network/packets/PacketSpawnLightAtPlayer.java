package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.blocks.ModBlocks;
import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.util.UpgradeUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

public class PacketSpawnLightAtPlayer {
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketSpawnLightAtPlayer() {
    }

    public static void encode(PacketSpawnLightAtPlayer msg, PacketBuffer buffer) {
    }

    public static PacketSpawnLightAtPlayer decode(PacketBuffer buffer) {
        return new PacketSpawnLightAtPlayer();
    }

    public static class Handler {
        public static void handle(PacketSpawnLightAtPlayer msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                List<Upgrade> upgrades = BurnerGunNBT.getUpgrades(gun);
                BlockState state = player.level.getBlockState(new BlockPos(player.position().add(new Vector3d(0, 1, 0))));
                if (UpgradeUtil.containsUpgradeFromList(upgrades, Upgrade.LIGHT)){
                    if (state == Blocks.AIR.defaultBlockState() || state == Blocks.CAVE_AIR.defaultBlockState()){
                        if (gun.getItem() instanceof BurnerGunMK1){
                            if (BurnerGunNBT.getFuelValue(gun) >= Upgrade.LIGHT.getCost())
                                BurnerGunNBT.setFuelValue(gun, BurnerGunNBT.getFuelValue(gun)-Upgrade.LIGHT.getCost());
                            else
                                return;
                        }
                        PacketHandler.sendTo(new PacketClientPlayLightSound(), player);
                        player.level.setBlockAndUpdate(new BlockPos(player.position().add(new Vector3d(0, 1, 0))), ModBlocks.LIGHT.get().defaultBlockState());
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}