package net.spogbot.playergolem.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.spogbot.playergolem.CustomEntityModelLayers;
import net.spogbot.playergolem.PlayergolemModel;
import net.spogbot.playergolem.PlayergolemRenderer;

public class PlayergolemClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CustomEntityModelLayers.PLAYERGOLEM, PlayergolemModel::getTexturedModelData);
       PlayergolemCommand.register();
        EntityRendererRegistry.register(EntityType.COPPER_GOLEM, PlayergolemRenderer::new);
    }
}