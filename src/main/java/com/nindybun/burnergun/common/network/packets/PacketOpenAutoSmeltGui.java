package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.containers.AutoSmeltContainer;
import com.nindybun.burnergun.common.containers.TrashContainer;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmelt;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmeltHandler;
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

import java.util.List;
import java.util.function.Supplier;

public class PacketOpenAutoSmeltGui {
    public PacketOpenAutoSmeltGui(){

    }

    public static void encode(PacketOpenAutoSmeltGui msg, PacketBuffer buffer) {
    }

    public static PacketOpenAutoSmeltGui decode(PacketBuffer buffer) {
        return new PacketOpenAutoSmeltGui();
    }

    public static class Handler {
        public static void handle(PacketOpenAutoSmeltGui msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();

                if (player == null)
                    return;

                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                ItemStack smelt = player.getMainHandItem();
                if (!gun.isEmpty()){
                    BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
                    BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);
                    List<Upgrade> upgradeList = UpgradeUtil.getUpgradesFromNBT(infoMK1 != null ? infoMK1.getUpgradeNBTList() : infoMK2.getUpgradeNBTList());
                    if (upgradeList.contains(Upgrade.AUTO_SMELT))
                        smelt = UpgradeUtil.getStackByUpgrade(gun, Upgrade.AUTO_SMELT);
                }

                if (!(smelt.getItem() instanceof AutoSmelt))
                    return;

                IItemHandler handler = smelt.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                player.openMenu(new SimpleNamedContainerProvider(
                        (windowId, playerInv, playerEntity) -> new AutoSmeltContainer(windowId, playerInv, (AutoSmeltHandler) handler),
                        new StringTextComponent("")
                ));

            });

            ctx.get().setPacketHandled(true);
        }
    }




}
