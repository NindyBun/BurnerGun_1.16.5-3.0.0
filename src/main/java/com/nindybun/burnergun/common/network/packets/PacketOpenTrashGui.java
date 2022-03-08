package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.containers.TrashContainer;
import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmelt;
import com.nindybun.burnergun.common.items.upgrades.Trash.Trash;
import com.nindybun.burnergun.common.items.upgrades.Trash.TrashHandler;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.util.UpgradeUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class PacketOpenTrashGui {
    public PacketOpenTrashGui(){

    }

    public static void encode(PacketOpenTrashGui msg, PacketBuffer buffer) {
    }

    public static PacketOpenTrashGui decode(PacketBuffer buffer) {
        return new PacketOpenTrashGui();
    }

    public static class Handler {
        public static void handle(PacketOpenTrashGui msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();

                if (player == null)
                    return;

                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                ItemStack trash = player.getMainHandItem();
                if (!gun.isEmpty()){
                    List<Upgrade> upgradeList = BurnerGunNBT.getUpgrades(gun);
                    if (upgradeList.contains(Upgrade.TRASH))
                        trash = UpgradeUtil.getStackByUpgrade(gun, Upgrade.TRASH);
                }

                if (!(trash.getItem() instanceof Trash))
                    return;

                IItemHandler handler = trash.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

                player.openMenu(new SimpleNamedContainerProvider(
                        (windowId, playerInv, playerEntity) -> new TrashContainer(windowId, playerInv, (TrashHandler) handler),
                        new StringTextComponent("")
                ));

            });

            ctx.get().setPacketHandled(true);
        }
    }




}
