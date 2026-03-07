package net.spogbot.playergolem;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.CopperGolemEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CopperGolemHeadBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.CopperGolemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayergolemRenderer extends MobEntityRenderer<CopperGolemEntity, PlayergolemRenderer.PlayergolemRenderState, PlayergolemModel> {

    private final CopperGolemEntityRenderer vanillaRenderer;

    public PlayergolemRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayergolemModel(context.getPart(CustomEntityModelLayers.PLAYERGOLEM)), 0.5f);
        this.vanillaRenderer = new CopperGolemEntityRenderer(context);

        this.addFeature(new CopperGolemHeadBlockFeatureRenderer(
                this,
                state -> ((CopperGolemEntityRenderState) state).headBlockItemStack,
                (Object obj) -> ((PlayergolemModel) this.model).transformMatricesForBlock((MatrixStack) obj)
        ));

        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    public PlayergolemRenderState createRenderState() {
        return new PlayergolemRenderState();
    }

    @Override
    public void updateRenderState(CopperGolemEntity entity, PlayergolemRenderState state, float tickDelta) {
        this.vanillaRenderer.updateRenderState(entity, state, tickDelta);

        if (entity.hasCustomName()) {
            String playerName = entity.getCustomName().getString().trim();
            if (state.lastPlayerName == null || !state.lastPlayerName.equals(playerName)) {
                state.lastPlayerName = playerName;
                state.skinFuture = PlayerSkinCache.getOrLoad(playerName);
                state.resolvedSkin = Optional.empty();
            }
            if (state.resolvedSkin.isEmpty() && state.skinFuture != null && state.skinFuture.isDone()) {
                state.resolvedSkin = state.skinFuture.join();
            }
        } else {
            state.lastPlayerName = null;
            state.skinFuture = null;
            state.resolvedSkin = Optional.empty();
        }

        state.useCustomModel = state.resolvedSkin.isPresent();
    }

    @Override
    protected RenderLayer getRenderLayer(PlayergolemRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        if (state.useCustomModel) {
            return RenderLayers.entityTranslucent(getTexture(state));
        }
        return super.getRenderLayer(state, showBody, translucent, showOutline);
    }

    @Override
    public void render(PlayergolemRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (!state.useCustomModel) {
            this.vanillaRenderer.render(state, matrices, queue, cameraRenderState);
            return;
        }
        super.render(state, matrices, queue, cameraRenderState);
    }

    @Override
    public Identifier getTexture(PlayergolemRenderState state) {
        return state.resolvedSkin.orElse(Identifier.of("minecraft", "textures/entity/copper_golem/copper_golem.png"));
    }

    public static class PlayergolemRenderState extends CopperGolemEntityRenderState {
        public String lastPlayerName = null;
        public CompletableFuture<Optional<Identifier>> skinFuture = null;
        public Optional<Identifier> resolvedSkin = Optional.empty();
        public boolean useCustomModel = false;
    }
}