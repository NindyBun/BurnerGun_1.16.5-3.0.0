package com.nindybun.burnergun.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldUtil {

    private static final Logger LOGGER = LogManager.getLogger();

    public static BlockRayTraceResult getLookingAt(World world, PlayerEntity player, RayTraceContext.FluidMode rayTraceFluid, double range) {
        Vector3d look = player.getLookAngle();
        Vector3d start = player.position().add(new Vector3d(0, player.getEyeHeight(), 0));
        Vector3d end = new Vector3d(player.getX() + look.x * range, player.getY() + player.getEyeHeight() + look.y * range, player.getZ() + look.z * range);
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, rayTraceFluid, player);
        return world.clip(context);
    }

    public static Vector3d getDim(BlockRayTraceResult ray, int xRad, int yRad, PlayerEntity player){
        //Z Face mining by default
        int xRange = xRad;
        int yRange = yRad;
        int zRange = 0;
        //X Face Mining
        if (Math.abs(ray.getDirection().getNormal().getX()) == 1){
            zRange = xRad;
            xRange = 0;
        }
        //Vertical Mining needs to act like the Horizontal but based on yaw
        if (Math.abs(ray.getDirection().getNormal().getY()) == 1){
            yRange = 0;
            int yaw = (int)player.getYHeadRot();
            if (yaw <0)
                yaw += 360;
            int facing = yaw / 45;

            if (facing == 6 || facing == 5 || facing == 2 || facing == 1) { //X axis
                xRange = yRad;
                zRange = xRad;
            }
            if (facing == 7 || facing == 8 || facing == 0 || facing == 4 || facing == 3) { //Z axis
                xRange = xRad;
                zRange = yRad;
            }

        }
        return new Vector3d(xRange, yRange, zRange);
    }
}
