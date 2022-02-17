package com.nindybun.burnergun.common.items.burnergunmk2;

import com.nindybun.burnergun.common.containers.BurnerGunMK1Container;
import com.nindybun.burnergun.common.containers.BurnerGunMK2Container;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.common.items.upgrades.Upgrade_Bag.UpgradeBag;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketUpdateGun;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BurnerGunMK2Handler extends ItemStackHandler {
    public static final int MAX_SLOTS = BurnerGunMK2Container.MAX_EXPECTED_GUN_SLOT_COUNT;

    public BurnerGunMK2Handler(int numberOfSlots){
        super(numberOfSlots);
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("Invalid slot number: " + slot);
        }

        return true;
    }

    public boolean canInsert(ItemStack item){
        return true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.validateSlotIndex(slot);
    }

    private final Logger LOGGER = LogManager.getLogger();



}
