package com.nindybun.burnergun.common.containers;

import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2Handler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class BurnerGunMK2Container extends Container{
    BurnerGunMK2Container(int windowId, PlayerInventory playerInv,
                          PacketBuffer buf){
        this(windowId, playerInv, new BurnerGunMK2Handler(MAX_EXPECTED_GUN_SLOT_COUNT));
    }

    public BurnerGunMK2Container(int windowId, PlayerInventory playerInventory, BurnerGunMK2Handler handler){
        super(ModContainers.BURNERGUNMK2_CONTAINER.get(), windowId);
        this.handler = handler;
        this.setup(playerInventory);
    }

    private final BurnerGunMK2Handler handler;

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int GUN_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private final int SLOT_X_SPACING = 18;
    private final int SLOT_Y_SPACING = 18;

    public static int MAX_EXPECTED_GUN_SLOT_COUNT = 9;

    private void setup(PlayerInventory playerInv){
        final int GUN_INVENTORY_YPOS = 8;
        final int GUN_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 48;
        final int PLAYER_INVENTORY_XPOS = 8;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 106;
        final int GUN_SLOTS_PER_ROW = 9;

        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int slotNumber = 0; slotNumber < HOTBAR_SLOT_COUNT; slotNumber++) {
            addSlot(new Slot(playerInv, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * slotNumber, HOTBAR_YPOS));
        }

        // Add the rest of the player's inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(playerInv, slotNumber, xpos, ypos));
            }
        }

        int gunSlotCount = handler.getSlots();
        if (gunSlotCount < 1 || gunSlotCount > MAX_EXPECTED_GUN_SLOT_COUNT) {
            LOGGER.warn("Unexpected invalid slot count in BurnerGunMK2(" + gunSlotCount + ")");
            gunSlotCount = MathHelper.clamp(gunSlotCount, 1, MAX_EXPECTED_GUN_SLOT_COUNT);
        }

        // Add the tile inventory container to the gui
        for (int gunSlot = 0; gunSlot < gunSlotCount; gunSlot++) {
            int xpos = GUN_INVENTORY_XPOS + SLOT_X_SPACING * gunSlot;
            addSlot(new SlotItemHandler(handler, gunSlot, xpos, GUN_INVENTORY_YPOS));
        }

    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        ItemStack main = playerIn.getMainHandItem();
        ItemStack off = playerIn.getOffhandItem();
        return (!main.isEmpty() && main.getItem() instanceof BurnerGunMK2) ||
                (!off.isEmpty() && off.getItem() instanceof BurnerGunMK2);
    }

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the Bag Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the Bag inventory)
    // At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot
    // returns ItemStack.EMPTY if the source slot is empty, or if none of the the source slot item could be moved
    //   otherwise, returns a copy of the source stack
    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int sourceSlotIndex) {
        Slot sourceSlot = slots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        final int BAG_SLOT_COUNT = handler.getSlots();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the bag inventory
            if (!moveItemStackTo(sourceStack, GUN_INVENTORY_FIRST_SLOT_INDEX, GUN_INVENTORY_FIRST_SLOT_INDEX + BAG_SLOT_COUNT, false)){
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= GUN_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < GUN_INVENTORY_FIRST_SLOT_INDEX + BAG_SLOT_COUNT) {
            // This is a bag slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            LOGGER.warn("Invalid slotIndex:" + sourceSlotIndex);
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private static final Logger LOGGER = LogManager.getLogger();

}
