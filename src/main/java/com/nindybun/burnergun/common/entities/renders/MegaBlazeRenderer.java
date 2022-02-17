package com.nindybun.burnergun.common.entities.renders;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.entities.MegaBlazeEntity;
import com.nindybun.burnergun.common.entities.models.MegaBlazeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MegaBlazeRenderer extends MobRenderer<MegaBlazeEntity, MegaBlazeModel<MegaBlazeEntity>> {
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(BurnerGun.MOD_ID, "textures/entities/megablaze/mega_blaze.png");

    public MegaBlazeRenderer(EntityRendererManager p_i46191_1_) {
        super(p_i46191_1_, new MegaBlazeModel<>(), 0.5F);
    }

    protected int getBlockLightLevel(MegaBlazeEntity p_225624_1_, BlockPos p_225624_2_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(MegaBlazeEntity p_110775_1_) {
        return RESOURCE_LOCATION;
    }
}