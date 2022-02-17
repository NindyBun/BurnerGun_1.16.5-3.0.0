package com.nindybun.burnergun.client.particles.megaBlazeFireballParticle;

import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


public class MegaBlazeFireballParticleType extends BasicParticleType {
    public MegaBlazeFireballParticleType() {
        super(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static class MegaBlazeFireballParticleFactory implements IParticleFactory<BasicParticleType> {
        IAnimatedSprite sprite;
        public MegaBlazeFireballParticleFactory(IAnimatedSprite sprite){
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            return new MegaBlazeFireballParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
        }
    }
}
