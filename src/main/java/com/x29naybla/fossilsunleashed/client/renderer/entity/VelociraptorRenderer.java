package com.x29naybla.fossilsunleashed.client.renderer.entity;

import com.x29naybla.fossilsunleashed.client.model.entity.VelociraptorModel;
import com.x29naybla.fossilsunleashed.entity.VelociraptorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VelociraptorRenderer extends GeoEntityRenderer<VelociraptorEntity> {
    public VelociraptorRenderer(EntityRendererProvider.Context context) {
        super(context, new VelociraptorModel());
    }
}
