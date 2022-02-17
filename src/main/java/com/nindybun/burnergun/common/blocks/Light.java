package com.nindybun.burnergun.common.blocks;

//import com.nindybun.burnergun.client.particles.ModParticles;
import com.nindybun.burnergun.client.particles.ModParticles;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class Light extends Block {
    protected static final VoxelShape SHAPE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    public Light() {
        super(
                Properties
                        .of(Material.BARRIER)
                        .sound(SoundType.WOOL)
                        .noCollission()
                        .instabreak()
                        .lightLevel(value -> 15)
        );
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.5D;
        double d2 = (double) pos.getZ() + 0.5D;

        worldIn.addParticle(ModParticles.LIGHT_PARTICLE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}