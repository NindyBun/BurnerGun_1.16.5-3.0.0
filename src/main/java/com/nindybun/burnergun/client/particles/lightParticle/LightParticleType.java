package com.nindybun.burnergun.client.particles.lightParticle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;


public class LightParticleType extends BasicParticleType {
    public LightParticleType() {
        super(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static class LightParticleFactory implements IParticleFactory<BasicParticleType> {
        IAnimatedSprite sprite;
        public LightParticleFactory(IAnimatedSprite sprite){
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType basicParticleType, ClientWorld world, double x, double y, double z, double xS, double yS, double zS) {
            LightParticle particle = new LightParticle(world, x, y, z, xS, yS, zS);
            particle.setSpriteFromAge(sprite);
            return particle;
        }
    }
}
