package com.x29naybla.fossilsunleashed.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.x29naybla.fossilsunleashed.client.model.entity.VelociraptorModel;
import com.x29naybla.fossilsunleashed.entity.VelociraptorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VelociraptorRenderer extends GeoEntityRenderer<VelociraptorEntity> {
    public VelociraptorRenderer(EntityRendererProvider.Context context) {
        super(context, new VelociraptorModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(VelociraptorEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.isBaby()) {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        } else {
            poseStack.scale(1F, 1F, 1F);
        }
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
