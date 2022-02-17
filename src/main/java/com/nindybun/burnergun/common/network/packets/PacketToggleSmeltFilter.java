package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleSmeltFilter {
    public PacketToggleSmeltFilter() {
    }

    public static void encode(PacketToggleSmeltFilter msg, PacketBuffer buffer) {

    }

    public static PacketToggleSmeltFilter decode(PacketBuffer buffer) {
        return new PacketToggleSmeltFilter();
    }

    public static class Handler {
        public static void handle(PacketToggleSmeltFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
                BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);
                if (infoMK1 != null)
                    infoMK1.setSmeltIsWhitelist(!infoMK1.getSmeltIsWhitelist());
                else
                    infoMK2.setSmeltIsWhitelist(!infoMK2.getSmeltIsWhitelist());
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
