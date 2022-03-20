package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.containers.BurnerGunMK2Container;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.containers.BurnerGunMK1Container;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1Handler;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2Handler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Supplier;

public class PacketOpenBurnerGunGui {
    public PacketOpenBurnerGunGui(){

    }

    public static void encode(PacketOpenBurnerGunGui msg, PacketBuffer buffer) {
    }

    public static PacketOpenBurnerGunGui decode(PacketBuffer buffer) {
        return new PacketOpenBurnerGunGui();
    }

    public static class Handler {
        public static void handle(PacketOpenBurnerGunGui msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();

                if (player == null)
                    return;

                ItemStack stack = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (stack.isEmpty())
                    return;

                IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                if (stack.getItem() instanceof BurnerGunMK1)
                    player.openMenu(new SimpleNamedContainerProvider(
                        (windowId, playerInv, playerEntity) ->
                            new BurnerGunMK1Container(windowId, playerInv, (BurnerGunMK1Handler) handler),
                        new StringTextComponent("")
                    ));
                else if (stack.getItem() instanceof BurnerGunMK2)
                    player.openMenu(new SimpleNamedContainerProvider(
                            (windowId, playerInv, playerEntity) -> new BurnerGunMK2Container(windowId, playerInv, (BurnerGunMK2Handler) handler),
                            new StringTextComponent("")
                    ));

            });

            ctx.get().setPacketHandled(true);
        }
    }




}
