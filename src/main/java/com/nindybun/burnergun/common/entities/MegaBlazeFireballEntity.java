package com.nindybun.burnergun.common.entities;

import com.nindybun.burnergun.client.particles.ModParticles;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class MegaBlazeFireballEntity extends DamagingProjectileEntity {
    public MegaBlazeFireballEntity(EntityType<? extends MegaBlazeFireballEntity> p_i50171_1_, World p_i50171_2_) {
        super(p_i50171_1_, p_i50171_2_);
    }

    public MegaBlazeFireballEntity(World world, LivingEntity livingEntity, double x, double y, double z) {
        super(ModEntities.MEGA_BLAZE_FIREBALL.get(), livingEntity, x, y, z, world);
    }

    protected void onHit(RayTraceResult p_70227_1_) {
        super.onHit(p_70227_1_);
        Entity entity = this.getOwner();
        if (p_70227_1_.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult)p_70227_1_).getEntity().is(entity)) {
            if (!this.level.isClientSide) {
                List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
                if (entity instanceof LivingEntity) {
                    areaeffectcloudentity.setOwner((LivingEntity)entity);
                }

                areaeffectcloudentity.setParticle(ModParticles.MEGA_BLAZE_FIREBALL_PARTICLE.get());
                areaeffectcloudentity.setRadius(3.0F);
                areaeffectcloudentity.setDuration(200);
                areaeffectcloudentity.addEffect(new EffectInstance(Effects.WITHER, 100, 1));
                if (!list.isEmpty()) {
                    for(LivingEntity livingentity : list) {
                        double d0 = this.distanceToSqr(livingentity);
                        if (d0 < 16.0D) {
                            areaeffectcloudentity.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                            break;
                        }
                    }
                }

                this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level.addFreshEntity(areaeffectcloudentity);
                this.remove();
            }

        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        return false;
    }

    protected IParticleData getTrailParticle() {
        return ModParticles.MEGA_BLAZE_FIREBALL_PARTICLE.get();
    }

    protected boolean shouldBurn() {
        return false;
    }
}
