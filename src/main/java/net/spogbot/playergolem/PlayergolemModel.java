package net.spogbot.playergolem;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CopperGolemEntityModel;
import net.minecraft.client.render.entity.state.CopperGolemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class PlayergolemModel extends CopperGolemEntityModel {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public PlayergolemModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.leftArm = body.getChild("left_arm");
        this.rightArm = body.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public ModelPart getRoot() {
        return this.root;
    }

    public ModelPart getBody()    { return body; }
    public ModelPart getHead()    { return head; }
    public ModelPart getLeftArm() { return leftArm; }
    public ModelPart getRightArm(){ return rightArm; }
    public ModelPart getLeftLeg() { return leftLeg; }
    public ModelPart getRightLeg(){ return rightLeg; }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-4.0F, -6.0F, -2.0F, 8.0F, 6.0F, 4.0F, new Dilation(0.0F))
                        .uv(16, 32).cuboid(-4.0F, -6.0F, -2.0F, 8.0F, 6.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.origin(0.0F, 19.0F, 0.0F));

        body.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                        .uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F))
                        .uv(24, 0).cuboid(-1.0F, -5.0F, -3.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(11, 0).cuboid(-1.0F, -7.9F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(-0.01F))
                        .uv(8, 0).cuboid(-2.0F, -7.9F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(-0.01F)),
                ModelTransform.origin(0.0F, -6.0F, 0.0F));

        body.addChild("left_arm", ModelPartBuilder.create()
                        .uv(32, 48).cuboid(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F))
                        .uv(48, 48).cuboid(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.origin(4.0F, -6.0F, 0.0F));

        body.addChild("right_arm", ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F))
                        .uv(40, 32).cuboid(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.origin(-4.0F, -6.0F, 0.0F));

        modelPartData.addChild("left_leg", ModelPartBuilder.create()
                        .uv(16, 48).cuboid(-0.1F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 48).cuboid(-0.1F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.origin(0.0F, 19.0F, 0.0F));

        modelPartData.addChild("right_leg", ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-3.9F, 0.0F, -1.99F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 32).cuboid(-3.9F, 0.0F, -1.99F, 4.0F, 5.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.origin(0.0F, 19.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(CopperGolemEntityRenderState state) {
        super.setAngles(state);
    }

    @Override
    public void transformMatricesForBlock(MatrixStack matrices) {
        super.transformMatricesForBlock(matrices);
        matrices.translate(0.0D, 4.0D / 16.0D, 0.0D);
    }
}