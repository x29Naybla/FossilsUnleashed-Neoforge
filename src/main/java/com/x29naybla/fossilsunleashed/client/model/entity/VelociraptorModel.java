package com.x29naybla.fossilsunleashed.client.model.entity;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class VelociraptorModel extends GeoModel {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "geo/entity/velociraptor.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "textures/entity/velociraptor.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "animations/entity/velociraptor.animation.json");

    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return this.animations;
    }
}
