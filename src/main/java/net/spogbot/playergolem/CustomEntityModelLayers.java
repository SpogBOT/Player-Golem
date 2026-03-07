package net.spogbot.playergolem;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class CustomEntityModelLayers {
    public static final EntityModelLayer PLAYERGOLEM = new EntityModelLayer(
            Identifier.of("playergolem", "playergolem"), "main"
    );

}