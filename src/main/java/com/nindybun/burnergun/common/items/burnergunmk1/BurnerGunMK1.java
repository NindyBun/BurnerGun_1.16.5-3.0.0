package com.nindybun.burnergun.common.items.burnergunmk1;

import com.nindybun.burnergun.client.Keybinds;
import com.nindybun.burnergun.common.blocks.Light;
import com.nindybun.burnergun.common.blocks.ModBlocks;
import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketRefuel;
import com.nindybun.burnergun.util.UpgradeUtil;
import com.nindybun.burnergun.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BurnerGunMK1 extends Item{
    private static final double base_use = 100;
    public static final double base_use_buffer = 20_000;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final IRecipeType<? extends AbstractCookingRecipe> RECIPE_TYPE = IRecipeType.SMELTING;

    public BurnerGunMK1() {
        super(new Properties().stacksTo(1).setNoRepair().tab(com.nindybun.burnergun.common.BurnerGun.itemGroup));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IItemHandler handler = getHandler(stack);
        if (!(handler.getStackInSlot(0).getItem() instanceof UpgradeCard)){
            tooltip.add(new StringTextComponent("Feed me fuel!").withStyle(TextFormatting.YELLOW));
        }else if (handler.getStackInSlot(0).getItem() instanceof UpgradeCard){
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void refuel(ItemStack gun){
        IItemHandler handler = getHandler(gun);
        if (!(handler.getStackInSlot(0).getItem() instanceof UpgradeCard)) {
            while (handler.getStackInSlot(0).getCount() > 0){
                if (BurnerGunNBT.getFuelValue(gun) + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)) > base_use_buffer)
                    break;
                BurnerGunNBT.setFuelValue(gun, BurnerGunNBT.getFuelValue(gun) + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)));
                ItemStack containerItem = handler.getStackInSlot(0).getContainerItem();
                handler.getStackInSlot(0).shrink(1);
                if (!containerItem.isEmpty())
                    handler.insertItem(0, containerItem, false);
            }
        }
    }

    public void useFuel(ItemStack gun, List<Upgrade> upgrades){;
        BurnerGunNBT.setFuelValue(gun, BurnerGunNBT.getFuelValue(gun) - getUseValue(upgrades));
        if (!(getHandler(gun).getStackInSlot(0).getItem() instanceof UpgradeCard))
            refuel(gun);
    }

    public double getUseValue(List<Upgrade> upgrades){
        int extraUse = 0;
        if (!upgrades.isEmpty()){
            extraUse = upgrades.stream().mapToInt(upgrade -> upgrade.lazyIs(Upgrade.LIGHT) ? 0 : upgrade.getCost()).sum();
        }
        return (base_use + extraUse) * (1.0 - ((UpgradeUtil.containsUpgradeFromList(upgrades, Upgrade.FUEL_EFFICIENCY_1)) ? UpgradeUtil.getUpgradeFromListByUpgrade(upgrades, Upgrade.FUEL_EFFICIENCY_1).getExtraValue() : 0));
    }

    public boolean canMine(ItemStack gun, World world, BlockPos pos, BlockState state, PlayerEntity player, List<Upgrade> upgrades){
        if (    state.getDestroySpeed(world, pos) < 0
                || state.getBlock() instanceof Light
                || !world.mayInteract(player, pos)
                || !player.abilities.mayBuild
                || BurnerGunNBT.getFuelValue(gun) < getUseValue(upgrades)
                || state.getBlock().equals(Blocks.AIR.defaultBlockState())
                || state.getBlock().equals(Blocks.CAVE_AIR.defaultBlockState())
                || (state.getFluidState().isSource() && !state.hasProperty(BlockStateProperties.WATERLOGGED))
                || (state.getFluidState().getAmount() > 0 && !state.hasProperty(BlockStateProperties.WATERLOGGED))
                || world.isEmptyBlock(pos))
            return false;
        return true;
    }

    public void mineVein(World world, BlockRayTraceResult ray, List<BlockPos> blockPosList, List<BlockPos> minedBlockList, int count, ItemStack gun, List<Upgrade> activeUpgrades, List<Item> smeltFilter, List<Item> trashFilter, PlayerEntity player){
        if (blockPosList.isEmpty() || count <= 0)
            return;
        BlockState blockState = world.getBlockState(blockPosList.get(0));
        BlockPos blockPos = blockPosList.get(0);
        blockPosList.remove(0);
        minedBlockList.add(blockPos);
        count -= 1;

        if (canMine(gun, world, blockPos, blockState, player, activeUpgrades)){
            blockPosList = UpgradeUtil.collectBlocks(minedBlockList, blockPosList, blockPos, blockState.getBlock().defaultBlockState(), world);
            mineBlock(world, ray, gun, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player, true);
        }
        mineVein(world, ray, blockPosList, minedBlockList, count, gun, activeUpgrades, smeltFilter, trashFilter, player);
    }

    public void mineBlock(World world, BlockRayTraceResult ray, ItemStack gun, List<Upgrade> activeUpgrades, List<Item> smeltFilter, List<Item> trashFilter, BlockPos blockPos, BlockState blockState, PlayerEntity player, Boolean isVein){
        if (canMine(gun, world, blockPos, blockState, player, activeUpgrades)){
            useFuel(gun, activeUpgrades);
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
                        drop = UpgradeUtil.smeltItem(world, smeltFilter, drop.copy(), BurnerGunNBT.getSmeltWhitelist(gun));
                    if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.TRASH))
                        drop = UpgradeUtil.trashItem(trashFilter, drop.copy(), BurnerGunNBT.getTrashWhitelist(gun));
                    if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.MAGNET)){
                        if (!player.inventory.add(drop.copy()))
                            player.drop(drop.copy(), true);
                    }else{
                        world.addFreshEntity(new ItemEntity(world, (isVein ? ray.getBlockPos() : blockPos).getX()+0.5, (isVein ? ray.getBlockPos() : blockPos).getY()+0.5, (isVein ? ray.getBlockPos() : blockPos).getZ()+0.5, drop.copy()));
                    }
                });
            }
            if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.MAGNET))
                player.giveExperiencePoints(blockXP);
            else
                blockState.getBlock().popExperience((ServerWorld) world, blockPos, blockXP);
            if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.LIGHT)){
                UpgradeUtil.spawnLight(world, ray, gun);
            }
        }
    }

    public void mineArea(World world, BlockRayTraceResult ray, ItemStack gun, List<Upgrade> activeUpgrades, List<Item> smeltFilter, List<Item> trashFilter, BlockPos blockPos, BlockState blockState, PlayerEntity player){
        int xRad = BurnerGunNBT.getHorizontal(gun);
        int yRad = BurnerGunNBT.getVertical(gun);
        Vector3d size = WorldUtil.getDim(ray, xRad, yRad, player);
        for (int xPos = blockPos.getX() - (int)size.x(); xPos <= blockPos.getX() + (int)size.x(); ++xPos){
            for (int yPos = blockPos.getY() - (int)size.y(); yPos <= blockPos.getY() + (int)size.y(); ++yPos){
                for (int zPos = blockPos.getZ() - (int)size.z(); zPos <= blockPos.getZ() + (int)size.z(); ++zPos){
                    BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                    if (thePos.equals(blockPos))
                        continue;
                    BlockState theState = world.getBlockState(thePos);
                    mineBlock(world, ray, gun, activeUpgrades, smeltFilter, trashFilter, thePos, theState, player, false);
                }
            }
        }
        mineBlock(world, ray, gun, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player, false);
    }

    @Override
    public void inventoryTick(ItemStack gun, World world, Entity entity, int slot, boolean held) {
        super.inventoryTick(gun, world, entity, slot, held);
        boolean heldgun = ((PlayerEntity)entity).getMainHandItem().getItem() instanceof BurnerGunMK1 || ((PlayerEntity)entity).getOffhandItem().getItem() instanceof BurnerGunMK1 ? true : false;
        if (heldgun && entity instanceof PlayerEntity && gun.getItem() instanceof BurnerGunMK1){
            IItemHandler handler = getHandler(gun);
            if (handler.getStackInSlot(0).getItem() instanceof UpgradeCard){
                double fuel = BurnerGunNBT.getFuelValue(gun)+((UpgradeCard)handler.getStackInSlot(0).getItem()).getUpgrade().getExtraValue();
                if (world.getMaxLocalRawBrightness((entity.blockPosition())) >= 8)
                    BurnerGunNBT.setFuelValue(gun, fuel >= base_use_buffer ? base_use_buffer : fuel);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack gun = player.getItemInHand(hand);
        List<Upgrade> activeUpgrades = UpgradeUtil.getActiveUpgrades(gun);
        BlockRayTraceResult blockRayTraceResult = WorldUtil.getLookingAt(world, player, RayTraceContext.FluidMode.NONE, BurnerGunNBT.getRaycast(gun));
        BlockPos blockPos = blockRayTraceResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        List<Item> smeltFilter = BurnerGunNBT.getSmeltFilter(gun);
        List<Item> trashFilter = BurnerGunNBT.getTrashFilter(gun);
        if (world.isClientSide)
            player.playSound(SoundEvents.FIRECHARGE_USE, BurnerGunNBT.getVolume(gun)*0.5f, 1.0f);
        if (!world.isClientSide){
            refuel(gun);
            if (canMine(gun, world, blockPos, blockState, player, activeUpgrades)){
                gun.enchant(Enchantments.BLOCK_FORTUNE, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.FORTUNE_1) ? UpgradeUtil.getUpgradeFromListByUpgrade(activeUpgrades, Upgrade.FORTUNE_1).getTier() : 0);
                gun.enchant(Enchantments.SILK_TOUCH, UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.SILK_TOUCH) ? 1 : 0);
                if (UpgradeUtil.containsUpgradeFromList(activeUpgrades, Upgrade.VEIN_MINER_1) && Keybinds.burnergun_veinMiner_key.isDown()){
                    List<BlockPos> blocks = new ArrayList<>();
                    blocks.add(blockPos);
                    mineVein(world, blockRayTraceResult, blocks, new ArrayList<>(), BurnerGunNBT.getCollectedBlocks(gun), gun, activeUpgrades, BurnerGunNBT.getSmeltFilter(gun), BurnerGunNBT.getTrashFilter(gun), player);
                }if (player.isCrouching())
                    mineBlock(world, blockRayTraceResult, gun, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player, false);
                else
                    mineArea(world, blockRayTraceResult, gun, activeUpgrades, smeltFilter, trashFilter, blockPos, blockState, player);
            }
        }
        UpgradeUtil.removeEnchantment(gun, Enchantments.BLOCK_FORTUNE);
        UpgradeUtil.removeEnchantment(gun, Enchantments.SILK_TOUCH);
        return ActionResult.consume(gun);
    }


}
