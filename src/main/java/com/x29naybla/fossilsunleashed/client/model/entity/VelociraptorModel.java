package com.x29naybla.fossilsunleashed.client.model.entity;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import com.x29naybla.fossilsunleashed.entity.VelociraptorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class VelociraptorModel extends GeoModel {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "geo/entity/velociraptor.geo.json");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "animations/entity/velociraptor.animation.json");

    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        if (((VelociraptorEntity) animatable).isSleeping()){
            return ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "textures/entity/velociraptor/velociraptor_sleep.png");
        }
        return ResourceLocation.fromNamespaceAndPath(FossilsUnleashed.MOD_ID, "textures/entity/velociraptor/velociraptor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return this.animations;
    }

    @Override
    public void setCustomAnimations(GeoAnimatable animatable, long instanceId, AnimationState animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if (animationState == null) return;

        EntityModelData extraDataOfType = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        GeoBone head = this.getAnimationProcessor().getBone("head");

        if (((VelociraptorEntity) animatable).isBaby()) {
            head.setScaleX(1.6F);
            head.setScaleY(1.6F);
            head.setScaleZ(1.6F);
        } else {
            head.setScaleX(1.0F);
            head.setScaleY(1.0F);
            head.setScaleZ(1.0F);
        }
    }
}
