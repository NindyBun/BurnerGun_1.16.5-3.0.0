package com.nindybun.burnergun.client.particles.megaBlazeFireballParticle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MegaBlazeFireballParticle extends SpriteTexturedParticle {
    private boolean hasHitGround;
    private final IAnimatedSprite sprites;

    public MegaBlazeFireballParticle(ClientWorld p_i232359_1_, double p_i232359_2_, double p_i232359_4_, double p_i232359_6_, double p_i232359_8_, double p_i232359_10_, double p_i232359_12_, IAnimatedSprite p_i232359_14_) {
        super(p_i232359_1_, p_i232359_2_, p_i232359_4_, p_i232359_6_);
        this.xd = p_i232359_8_;
        this.yd = p_i232359_10_;
        this.zd = p_i232359_12_;
        this.rCol = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
        this.gCol = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
        this.bCol = MathHelper.nextFloat(this.random, 0.0F, 0.0F);
        this.quadSize *= 0.75F;
        this.lifetime = (int)(20.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D));
        this.hasHitGround = false;
        this.hasPhysics = false;
        this.sprites = p_i232359_14_;
        this.setSpriteFromAge(p_i232359_14_);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
            if (this.onGround) {
                this.yd = 0.0D;
                this.hasHitGround = true;
            }

            if (this.hasHitGround) {
                this.yd += 0.002D;
            }

            this.move(this.xd, this.yd, this.zd);
            if (this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }

            this.xd *= (double)0.96F;
            this.zd *= (double)0.96F;
            if (this.hasHitGround) {
                this.yd *= (double)0.96F;
            }

        }
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getQuadSize(float p_217561_1_) {
        return this.quadSize * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }
}
