package com.nindybun.burnergun.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.util.WorldUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.CollisionBoxDebugRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ICollisionReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber(modid = BurnerGun.MOD_ID, value = Dist.CLIENT)
public class BlockRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void drawBoundingBoxAtBlockPos(MatrixStack matrixStackIn, AxisAlignedBB aabbIn, float red, float green, float blue, float alpha, BlockPos pos, BlockPos aimed) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        double camX = cam.x, camY = cam.y, camZ = cam.z;

        matrixStackIn.pushPose();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawShapeOutline(matrixStackIn, VoxelShapes.create(aabbIn), pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha, pos, aimed);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrixStackIn.popPose();
    }

    private static void drawShapeOutline(MatrixStack matrixStack, VoxelShape voxelShape, double originX, double originY, double originZ, float red, float green, float blue, float alpha, BlockPos pos, BlockPos aimed) {
        Matrix4f matrix4f = matrixStack.last().pose();

        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder bufferIn = renderTypeBuffer.getBuffer(RenderType.lines());

        voxelShape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            if (!pos.equals(aimed)){
                bufferIn.vertex(matrix4f, (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ)).color(red, green, blue, alpha).endVertex();
                bufferIn.vertex(matrix4f, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ)).color(red, green, blue, alpha).endVertex();
            }

        });

        renderTypeBuffer.endBatch(RenderType.lines());
    }

    public static void drawArea(ItemStack gun, PlayerEntity player, MatrixStack matrixStack){
        BlockRayTraceResult ray = WorldUtil.getLookingAt(player.level, player, RayTraceContext.FluidMode.NONE, BurnerGunNBT.getRaycast(gun));
        if (player.level.getBlockState(ray.getBlockPos()) == Blocks.AIR.defaultBlockState())
            return;
        int xRad = BurnerGunNBT.getHorizontal(gun);
        int yRad = BurnerGunNBT.getVertical(gun);
        BlockPos aimedPos = ray.getBlockPos();
        if (ray.getType() != RayTraceResult.Type.BLOCK)
            return;
        Vector3d size = WorldUtil.getDim(ray, xRad, yRad, player);
        float[] color = BurnerGunNBT.getColor(gun);
        drawBoundingBoxAtBlockPos(matrixStack, player.level.getBlockState(aimedPos).getShape(player.level, aimedPos, ISelectionContext.of(player)).bounds(), color[0], color[1], color[2], 1.0F, aimedPos.relative(ray.getDirection()), aimedPos.relative(ray.getDirection()));
        drawBoundingBoxAtBlockPos(matrixStack, player.level.getBlockState(aimedPos).getShape(player.level, aimedPos, ISelectionContext.of(player)).bounds(), color[0], color[1], color[2], 1.0F, aimedPos, aimedPos.relative(ray.getDirection()));
        if (player.isCrouching())
            return;
        for (int xPos = aimedPos.getX() - (int)size.x(); xPos <= aimedPos.getX() + (int)size.x(); ++xPos){
            for (int yPos = aimedPos.getY() - (int)size.y(); yPos <= aimedPos.getY() + (int)size.y(); ++yPos){
                for (int zPos = aimedPos.getZ() - (int)size.z(); zPos <= aimedPos.getZ() + (int)size.z(); ++zPos){
                    BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                    if (thePos != aimedPos && player.level.getBlockState(thePos) != Blocks.AIR.defaultBlockState() && player.level.getBlockState(thePos) != Blocks.CAVE_AIR.defaultBlockState())
                        drawBoundingBoxAtBlockPos(matrixStack, player.level.getBlockState(aimedPos).getShape(player.level, aimedPos, ISelectionContext.of(player)).bounds(), color[0], color[1], color[2], 1.0F, thePos, aimedPos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorldEvent(RenderWorldLastEvent e) {
        final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
        if (gun.isEmpty())
            return;
        gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());

        drawArea(gun, player, e.getMatrixStack());

        //drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, 0), new BlockPos(0, 65, 0));
        //drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(1, 65, 0), new BlockPos(0, 65, 0));
        /*drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 65, -1));

        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, 0));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 64, -1));

        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, 0));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, 1));
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), test, 1.0F, 0.0F, 0.0F, 1.0F, new BlockPos(0, 66, -1));*/
    }

}
