package com.nindybun.burnergun.common.containers;

import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrashGhostSlot extends SlotItemHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public TrashGhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(PlayerEntity player) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(getSlotIndex(), ItemStack.EMPTY);
        return false;
    }
    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BurnerGunMK1 || stack.getItem() instanceof BurnerGunMK2 || stack.getItem() instanceof UpgradeCard)
            return false;
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(getSlotIndex(), stack.copy().getItem().getDefaultInstance());
        return false;
    }

}
