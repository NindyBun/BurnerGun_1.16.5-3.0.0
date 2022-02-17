package com.nindybun.burnergun.common.items;

import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlazeCage extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    public BlazeCage() {
        super(new Properties().stacksTo(16).tab(BurnerGun.itemGroup));
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.level.isClientSide){
            if(entity.getEntity().isAlive() && entity.getType().equals(EntityType.PIG)){
                player.getItemInHand(hand).shrink(1);
                if (player.getItemInHand(hand).isEmpty()){
                    IItemHandler playerHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
                    playerHandler.insertItem(hand.equals(Hand.MAIN_HAND) ? EquipmentSlotType.MAINHAND.getIndex() : EquipmentSlotType.OFFHAND.getIndex(), new ItemStack(ModItems.CAGED_BLAZE.get()), false);
                }
                else if(!player.inventory.add(new ItemStack(ModItems.CAGED_BLAZE.get())))
                    player.drop(new ItemStack(ModItems.CAGED_BLAZE.get()), false);
            }
            entity.remove();
        }
        return ActionResultType.PASS;
    }

    public ItemStack setItemInHand(PlayerEntity player, Hand hand, ItemStack stack){
        player.setItemInHand(hand, stack);
        return stack;
    }
}
