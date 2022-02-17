package com.nindybun.burnergun.common.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.eventbus.api.Event;

import java.util.EnumSet;

public class MegaBlazeEntity extends BlazeEntity {
    private float allowedHeightOffset = 0.5F;
    private int nextHeightOffsetChangeTick;
    private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(MegaBlazeEntity.class, DataSerializers.BYTE);

    public MegaBlazeEntity(EntityType<? extends MegaBlazeEntity> entityType, World world) {
        super(entityType, world);

        this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
        this.setPathfindingMalus(PathNodeType.LAVA, 8.0F);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.xpReward = 100;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(4, new FireballAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.00)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.50F)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.BLAZE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    public float getBrightness() {
        return 1.0F;
    }

    public void aiStep() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.aiStep();
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    protected void customServerAiStep() {
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;
        }

        LivingEntity livingentity = this.getTarget();
        if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(livingentity)) {
            Vector3d vector3d = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double)0.3F - vector3d.y) * (double)0.3F, 0.0D));
            this.hasImpulse = true;
        }

        super.customServerAiStep();
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    public boolean isOnFire() {
        return this.isCharged();
    }

    private boolean isCharged() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    private void setCharged(boolean p_70844_1_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_70844_1_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public boolean doHurtTarget(Entity p_70652_1_) {
        if (!super.doHurtTarget(p_70652_1_)) {
            return false;
        } else {
            if (p_70652_1_ instanceof LivingEntity) {
                ((LivingEntity)p_70652_1_).addEffect(new EffectInstance(Effects.WITHER, 200));
            }
            return true;
        }
    }
    public boolean canBeAffected(EffectInstance p_70687_1_) {
        return p_70687_1_.getEffect() == Effects.WITHER ? false : super.canBeAffected(p_70687_1_);
    }
    @Override
    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        if (p_70097_1_ != DamageSource.DROWN && !(p_70097_1_.getEntity() instanceof WitherEntity)) {
            Entity entity1 = p_70097_1_.getDirectEntity();
            if (entity1 instanceof AbstractArrowEntity && this.getHealth() <= this.getMaxHealth()/2)
                return false;
            entity1 = p_70097_1_.getEntity();
            if (entity1 != null && !(entity1 instanceof PlayerEntity) && entity1 instanceof LivingEntity && ((LivingEntity)entity1).getMobType() == this.getMobType()) {
                return false;
            } else {
                return super.hurt(p_70097_1_, p_70097_2_);
            }
        } else {
            return false;
        }
    }

    static class FireballAttackGoal extends Goal {
        private final MegaBlazeEntity megaBlaze;
        private int attackStep;
        private int attackTime;
        private int specialTime;
        private int lastSeen;
        private boolean lowHealth;

        public FireballAttackGoal(MegaBlazeEntity p_i45846_1_) {
            this.megaBlaze = p_i45846_1_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.megaBlaze.getTarget();
            return livingentity != null && livingentity.isAlive() && this.megaBlaze.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.megaBlaze.setCharged(false);
            this.lastSeen = 0;
        }

        public void tick() {
            --this.attackTime;
            --this.specialTime;
            if (this.megaBlaze.getHealth() <= this.megaBlaze.getMaxHealth()/2 && !lowHealth)
                lowHealth = true;
            LivingEntity livingentity = this.megaBlaze.getTarget();
            if (livingentity != null) {
                boolean flag = this.megaBlaze.getSensing().canSee(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.megaBlaze.distanceToSqr(livingentity);
                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0 || this.specialTime <= 0) {
                        this.attackTime = 20;
                        this.specialTime = 50 + (int)(Math.random() * ((100 - 50) + 1));
                        this.megaBlaze.doHurtTarget(livingentity);
                    }

                    this.megaBlaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.megaBlaze.getX();
                    double d2 = livingentity.getY(0.5D) - this.megaBlaze.getY(0.5D);
                    double d3 = livingentity.getZ() - this.megaBlaze.getZ();

                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = lowHealth ? 1 : 20; //startup
                            this.megaBlaze.setCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = lowHealth ? 1 : 6;//winding
                        } else {
                            this.attackTime = lowHealth ? 1 : 40; //cooldown
                            this.attackStep = 0;
                            this.megaBlaze.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
                            if (!this.megaBlaze.isSilent()) {
                                this.megaBlaze.level.levelEvent((PlayerEntity)null, 1018, this.megaBlaze.blockPosition(), 0);
                            }

                            /*if (this.megaBlaze.getHealth() < this.megaBlaze.getMaxHealth()/2 && specialTime <= 0){
                                this.specialTime = 20*10 + (int)(Math.random() * ((20 - 10)*10 + 1));
                                MegaBlazeFireballEntity megaBlazeFireballEntity = new MegaBlazeFireballEntity(this.megaBlaze.level, this.megaBlaze, d1 + this.megaBlaze.getRandom().nextGaussian() * (double)f, d2, d3 + this.megaBlaze.getRandom().nextGaussian() * (double)f);
                                megaBlazeFireballEntity.setPos(megaBlazeFireballEntity.getX(), this.megaBlaze.getY(0.5D) + 0.5D, megaBlazeFireballEntity.getZ());
                                this.megaBlaze.level.addFreshEntity(megaBlazeFireballEntity);
                            }*/
                            //SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.megaBlaze.level, this.megaBlaze, d1 + this.megaBlaze.getRandom().nextGaussian() * (double)f, d2, d3 + this.megaBlaze.getRandom().nextGaussian() * (double)f);
                            MegaBlazeProjectileEntity megaBlazeProjectileEntity = new MegaBlazeProjectileEntity(this.megaBlaze.level, this.megaBlaze, d1+this.megaBlaze.getRandom().nextGaussian() , d2, d3+this.megaBlaze.getRandom().nextGaussian());
                            megaBlazeProjectileEntity.setPos(megaBlazeProjectileEntity.getX(), this.megaBlaze.getY(0.5D), megaBlazeProjectileEntity.getZ());
                            this.megaBlaze.level.addFreshEntity(megaBlazeProjectileEntity);
                            //SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.megaBlaze.level, this.megaBlaze, d1+this.megaBlaze.getRandom().nextGaussian() , d2, d3+this.megaBlaze.getRandom().nextGaussian());
                            //smallfireballentity.setPos(smallfireballentity.getX(), this.megaBlaze.getY(0.5D), smallfireballentity.getZ());
                            //this.megaBlaze.level.addFreshEntity(smallfireballentity);
                        }
                    }
                    this.megaBlaze.getLookControl().setLookAt(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 10F, 10F);
                } else if (lastSeen > 5){
                    this.megaBlaze.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.8f, 1.0f);
                    this.megaBlaze.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                    //this.megaBlaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.megaBlaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }

}
