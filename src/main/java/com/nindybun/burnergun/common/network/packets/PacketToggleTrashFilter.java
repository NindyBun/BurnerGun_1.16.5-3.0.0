package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleTrashFilter {
    public PacketToggleTrashFilter() {
    }

    public static void encode(PacketToggleTrashFilter msg, PacketBuffer buffer) {

    }

    public static PacketToggleTrashFilter decode(PacketBuffer buffer) {
        return new PacketToggleTrashFilter();
    }

    public static class Handler {
        public static void handle(PacketToggleTrashFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunNBT.setTrashWhitelist(gun, !BurnerGunNBT.getTrashWhitelist(gun));
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
