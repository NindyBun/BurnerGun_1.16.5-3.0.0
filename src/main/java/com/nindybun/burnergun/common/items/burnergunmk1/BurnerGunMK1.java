package com.nindybun.burnergun.common.items.burnergunmk1;

import com.google.common.collect.Sets;
import com.nindybun.burnergun.client.Keybinds;
import com.nindybun.burnergun.common.blocks.Light;
import com.nindybun.burnergun.common.blocks.ModBlocks;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2InfoProvider;
import com.nindybun.burnergun.common.items.upgrades.Auto_Fuel.AutoFuel;
import com.nindybun.burnergun.common.items.upgrades.Trash.Trash;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.util.UpgradeUtil;
import com.nindybun.burnergun.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Dimension;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class BurnerGunMK1 extends Item{
    private static final double base_use = 100;
    public static final double base_use_buffer = 10_000;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final IRecipeType<? extends AbstractCookingRecipe> RECIPE_TYPE = IRecipeType.SMELTING;

    public BurnerGunMK1() {
        super(new Properties().stacksTo(1).setNoRepair().tab(com.nindybun.burnergun.common.BurnerGun.itemGroup));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IItemHandler handler = getHandler(stack);
        if (!handler.getStackInSlot(0).getItem().equals(Upgrade.AMBIENCE.getCard().getItem())){
            tooltip.add(new StringTextComponent("Feed me fuel!").withStyle(TextFormatting.YELLOW));
        }else if (handler.getStackInSlot(0).getItem().equals(Upgrade.AMBIENCE.getCard().getItem())){
            tooltip.add(new StringTextComponent("Collecting heat from nearby sources!").withStyle(TextFormatting.YELLOW));
        }
        tooltip.add(new StringTextComponent("Press " + GLFW.glfwGetKeyName(Keybinds.burnergun_gui_key.getKey().getValue(), GLFW.glfwGetKeyScancode(Keybinds.burnergun_gui_key.getKey().getValue())).toUpperCase() + " to open GUI").withStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent("Press " + GLFW.glfwGetKeyName(Keybinds.burnergun_light_key.getKey().getValue(), GLFW.glfwGetKeyScancode(Keybinds.burnergun_light_key.getKey().getValue())).toUpperCase() + " to shoot light!").withStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent("Press " + GLFW.glfwGetKeyName(Keybinds.burnergun_lightPlayer_key.getKey().getValue(), GLFW.glfwGetKeyScancode(Keybinds.burnergun_lightPlayer_key.getKey().getValue())).toUpperCase() + " to place light at your head!").withStyle(TextFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
        return new BurnerGunMK1Provider();
    }

    public static BurnerGunMK1Info getInfo(ItemStack gun) {
        return gun.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).orElse(null);
    }

    public static IItemHandler getHandler(ItemStack itemStack) {
        return itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
    }
    public static ItemStack getGun(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof BurnerGunMK1)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof BurnerGunMK1)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

    private final String INFO_NBT_TAG = "burnergunMK1InfoNBT";
    private final String HANDLER_NBT_TAG = "burnergunMK1HandlerNBT";

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT infoTag = new CompoundNBT();
        stack.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).ifPresent((cap) -> {
            infoTag.put(INFO_NBT_TAG, BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability.writeNBT(cap, null));
        });
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((cap)->{
            infoTag.put(HANDLER_NBT_TAG, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(cap, null));
        });
        return infoTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (nbt != null){
            stack.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).ifPresent((cap) -> {
                BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability.readNBT(cap, null, nbt.get(INFO_NBT_TAG));
            });
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((cap)->{
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(cap, null, nbt.get(HANDLER_NBT_TAG));
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void refuel(BurnerGunMK1Info info, ItemStack stack, PlayerEntity player){
        IItemHandler handler = getHandler(stack);
        if (!handler.getStackInSlot(0).getItem().equals(Upgrade.AMBIENCE.getCard().getItem())) {
            while (handler.getStackInSlot(0).getCount() > 0){
                if (info.getFuelValue() + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)) > base_use_buffer)
                    break;
                info.setFuelValue(info.getFuelValue() + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)));
                ItemStack containerItem = handler.getStackInSlot(0).getContainerItem();
                handler.getStackInSlot(0).shrink(1);
                if (player.inventory.add(containerItem))
                    player.drop(containerItem, true);
            }
        }
    }

    public void useFuel(BurnerGunMK1Info info, ItemStack stack, PlayerEntity player, List<Upgrade> upgrades){
        if (!getHandler(stack).getStackInSlot(0).getItem().equals(Upgrade.AMBIENCE.getCard().getItem()))
            refuel(info, stack, player);
        info.setFuelValue(info.getFuelValue() - getUseValue(upgrades));
    }

    public double getUseValue(List<Upgrade> upgrades){
        int extraUse = 0;
        if (!upgrades.isEmpty()){
            extraUse = upgrades.stream().mapToInt(upgrade -> upgrade.lazyIs(Upgrade.LIGHT) ? 0 : upgrade.getCost()).sum();
        }
        return (base_use + extraUse) * (1.0 - ((UpgradeUtil.containsUpgradeFromList(upgrades, Upgrade.FUEL_EFFICIENCY_1)) ? UpgradeUtil.getUpgradeFromListByUpgrade(upgrades, Upgrade.FUEL_EFFICIENCY_1).getExtraValue() : 0));
    }

    public boolean canMine(World world, BlockPos pos, BlockState state, PlayerEntity player, BurnerGunMK1Info info, List<Upgrade> upgrades){
        if (state.getDestroySpeed(world, pos) == -1 || state.getBlock() instanceof Light
                || !world.mayInteract(player, pos) || !player.mayBuild()
                || MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, player))
                || info.getFuelValue() < getUseValue(upgrades)
                ||  state.getBlock().equals(Blocks.AIR))
            return false;
        return true;
    }

    public ItemStack trashItem(List<Item> trashList, ItemStack drop, Boolean trashWhitelist){
        if (trashList.contains(drop.getItem()) && !trashWhitelist)
            return drop;
        else if (!trashList.contains(drop.getItem()) && trashWhitelist)
            return drop;
        return ItemStack.EMPTY;
    }

    public ItemStack smeltItem(World world, List<Item> smeltList, ItemStack drop, Boolean smeltWhitelist){
        IInventory inv = new Inventory(1);
        inv.setItem(0, drop);
        Optional<? extends AbstractCookingRecipe> recipe = world.getRecipeManager().getRecipeFor(RECIPE_TYPE, inv, world);
        if (recipe.isPresent()){
            ItemStack smelted = recipe.get().getResultItem().copy();
            if (smeltList.contains(smelted.getItem()) && smeltWhitelist)
                return smelted;
            else if (!smeltList.contains(smelted.getItem()) && !smeltWhitelist)
                return smelted;
        }
        return drop;
    }

    public void spawnLight(World world, BlockRayTraceResult ray, BurnerGunMK1Info info){
        if (world.getBrightness(LightType.BLOCK, ray.getBlockPos().relative(ray.getDirection())) < 8 && ray.getType() == RayTraceResult.Type.BLOCK && info.getFuelValue() >= Upgrade.LIGHT.getCost()){
            info.setFuelValue(info.getFuelValue()-Upgrade.LIGHT.getCost());
            world.setBlockAndUpdate(ray.getBlockPos(), ModBlocks.LIGHT.get().defaultBlockState());
        }
    }

    public void mineBlock(World world, BlockRayTraceResult ray, ItemStack gun, BurnerGunMK1Info info, List<Upgrade> activeUpgrades, List<Item> smeltFilter, List<Item> trashFilter, BlockPos blockPos, BlockState blockState, PlayerEntity player){
        if (canMine(world, blockPos, blockState, player, info, activeUpgrades)){
            useFuel(info, gun, player, activeUpgrades);
            List<ItemStack> blockDrops = blockState.getDrops(new LootContext.Builder((ServerWorld) world)
                    .withParameter(LootParameters.TOOL, gun)
                    .withParameter(LootParameters.ORIGIN, new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                    .withParameter(LootParameters.BLOCK_STATE, blockState)
            );
            world.destroyBlock(blockPos, false);
            int blockXP = blockState.getExpDrop(world, blockPos, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.FORTUNE_1) ? UpgradeUtil.getUpgradeFromListByUpgrade(activeUpgrades, Upgrade.FORTUNE_1).getTier() : 0, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.SILK_TOUCH) ? 1 : 0);
            if (!blockDrops.isEmpty()){
                blockDrops.forEach(drop -> {
                    if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.AUTO_SMELT))
                        drop = smeltItem(world, smeltFilter, drop.copy(), info.getSmeltIsWhitelist());
                    if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.TRASH))
                        drop = trashItem(trashFilter, drop.copy(), info.getTrashIsWhitelist());
                    if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.MAGNET)){
                        if (!player.inventory.add(drop.copy()))
                            player.drop(drop.copy(), true);
                    }else{
                        world.addFreshEntity(new ItemEntity(world, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, drop.copy()));
                    }
                });
            }
            if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.MAGNET))
                player.giveExperiencePoints(blockXP);
            else
                blockState.getBlock().popExperience((ServerWorld) world, blockPos, blockXP);
            if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.LIGHT))
                spawnLight(world, ray, info);
        }
    }

    public void mineArea(World world, BlockRayTraceResult ray, ItemStack gun, BurnerGunMK1Info info, List<Upgrade> activeUpgrades, List<Item> smeltFilter, List<Item> trashFilter, BlockPos blockPos, BlockState blockState, PlayerEntity player){
        int xRad = info.getHorizontal();
        int yRad = info.getVertical();
        Vector3d size = WorldUtil.getDim(ray, xRad, yRad, player);
        for (int xPos = blockPos.getX() - (int)size.x(); xPos <= blockPos.getX() + (int)size.x(); ++xPos){
            for (int yPos = blockPos.getY() - (int)size.y(); yPos <= blockPos.getY() + (int)size.y(); ++yPos){
                for (int zPos = blockPos.getZ() - (int)size.z(); zPos <= blockPos.getZ() + (int)size.z(); ++zPos){
                    BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                    if (thePos.equals(blockPos))
                        continue;
                    BlockState theState = world.getBlockState(thePos);
                    mineBlock(world, ray, gun, info, activeUpgrades, smeltFilter, trashFilter, thePos, theState, player);
                }
            }
        }
        mineBlock(world, ray, gun, info, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        super.inventoryTick(stack, world, entity, slot, held);
        boolean heldgun = ((PlayerEntity)entity).getMainHandItem().getItem() instanceof BurnerGunMK1 || ((PlayerEntity)entity).getOffhandItem().getItem() instanceof BurnerGunMK1 ? true : false;
        if (heldgun && entity instanceof PlayerEntity && stack.getItem() instanceof BurnerGunMK1){
            IItemHandler handler = getHandler(stack);
            if (handler.getStackInSlot(0).getItem().equals(Upgrade.AMBIENCE.getCard().getItem())){
                BurnerGunMK1Info info = getInfo(stack);
                if (info.getFuelValue() < base_use_buffer && world.getMaxLocalRawBrightness((entity.blockPosition())) >= 8)
                    info.setFuelValue(info.getFuelValue()+1.0);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack gun = player.getItemInHand(hand);
        BurnerGunMK1Info info = getInfo(gun);
        List<Upgrade> activeUpgrades = UpgradeUtil.getActiveUpgrades(gun);
        BlockRayTraceResult blockRayTraceResult = WorldUtil.getLookingAt(world, player, RayTraceContext.FluidMode.NONE, info.getRaycastRange());
        BlockPos blockPos = blockRayTraceResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        List<Item> smeltFilter = UpgradeUtil.getItemsFromList(info.getSmeltNBTFilter());
        List<Item> trashFilter = UpgradeUtil.getItemsFromList(info.getTrashNBTFilter());
        if (world.isClientSide)
            player.playSound(SoundEvents.FIRECHARGE_USE, info.getVolume()*0.5f, 1.0f);
        if (!world.isClientSide){
            refuel(info, gun, player);
            if (canMine(world, blockPos, blockState, player, info, activeUpgrades)){
                gun.enchant(Enchantments.BLOCK_FORTUNE, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.FORTUNE_1) ? UpgradeUtil.getUpgradeFromListByUpgrade(activeUpgrades, Upgrade.FORTUNE_1).getTier() : 0);
                gun.enchant(Enchantments.SILK_TOUCH, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.SILK_TOUCH) ? 1 : 0);
                if (player.isCrouching())
                    mineBlock(world, blockRayTraceResult, gun, info, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player);
                else
                    mineArea(world, blockRayTraceResult, gun, info, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player);
            }
        }
        UpgradeUtil.removeEnchantment(gun, Enchantments.BLOCK_FORTUNE);
        UpgradeUtil.removeEnchantment(gun, Enchantments.SILK_TOUCH);
        return ActionResult.consume(gun);
    }


}
