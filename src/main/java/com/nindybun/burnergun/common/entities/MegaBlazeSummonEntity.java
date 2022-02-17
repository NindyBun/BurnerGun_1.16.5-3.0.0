package com.nindybun.burnergun.common.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MegaBlazeSummonEntity extends ItemEntity {

    public MegaBlazeSummonEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
        super(p_i50217_1_, p_i50217_2_);
    }

    public MegaBlazeSummonEntity(World world, double x, double y, double z, ItemStack itemStack) {
        super(world, x, y, z, itemStack);
    }

    @Override
    public boolean isInLava() {
        return super.isInLava();
    }

    @Override
    public void tick() {
        if (this.isInLava() && this.level.dimension() == World.NETHER){
            BlockState state = this.getBlockStateOn();
            if (state.getFluidState().isSource()){
                Block north = this.level.getBlockState(this.getOnPos().north()).getBlock();
                Block west = this.level.getBlockState(this.getOnPos().west()).getBlock();
                Block east = this.level.getBlockState(this.getOnPos().east()).getBlock();
                Block south = this.level.getBlockState(this.getOnPos().south()).getBlock();
                Block bottom = this.level.getBlockState(this.getOnPos().below()).getBlock();
                if (north == Blocks.NETHER_BRICKS && west == Blocks.NETHER_BRICKS && east == Blocks.NETHER_BRICKS
                        && south == Blocks.NETHER_BRICKS && bottom == Blocks.NETHER_BRICKS){
                    MegaBlazeEntity megaBlaze = new MegaBlazeEntity(ModEntities.MEGA_BLAZE.get(), this.level);
                    megaBlaze.setPos(this.getX(), this.getY()+1, this.getZ());
                    this.level.addFreshEntity(megaBlaze);
                }

            }
            this.remove();
        }
        super.tick();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    public static MegaBlazeSummonEntity copy(ItemEntity oldItemEntity){
        MegaBlazeSummonEntity newItemEntity =  new MegaBlazeSummonEntity(oldItemEntity.level, oldItemEntity.getX(), oldItemEntity.getY(), oldItemEntity.getZ(), oldItemEntity.getItem());
        newItemEntity.setDeltaMovement(oldItemEntity.getDeltaMovement());
        newItemEntity.lifespan = oldItemEntity.lifespan;
        return newItemEntity;
    }
}
