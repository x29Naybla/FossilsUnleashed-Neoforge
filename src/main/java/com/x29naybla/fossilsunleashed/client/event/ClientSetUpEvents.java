package com.x29naybla.fossilsunleashed.client.event;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import com.x29naybla.fossilsunleashed.client.renderer.entity.DodoRenderer;
import com.x29naybla.fossilsunleashed.client.renderer.entity.VelociraptorRenderer;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = FossilsUnleashed.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetUpEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.DODO.get(), DodoRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DODO_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityRegistry.VELOCIRAPTOR.get(), VelociraptorRenderer::new);
    }
}
