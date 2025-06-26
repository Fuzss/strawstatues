package fuzs.strawstatues.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/**
 * @see PlayerModel
 */
public class StrawStatueModel extends HumanoidModel<StrawStatueRenderState> {
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final boolean slim;

    public StrawStatueModel(ModelPart modelPart, boolean slim) {
        super(modelPart, RenderType::entityTranslucent);
        this.slim = slim;
        this.leftSleeve = this.leftArm.getChild("left_sleeve");
        this.rightSleeve = this.rightArm.getChild("right_sleeve");
        this.leftPants = this.leftLeg.getChild("left_pants");
        this.rightPants = this.rightLeg.getChild("right_pants");
        this.jacket = this.body.getChild("jacket");
    }

    @Override
    public void setupAnim(StrawStatueRenderState renderState) {
        this.resetPose();
        this.hat.visible = renderState.showHat;
        this.jacket.visible = renderState.showJacket;
        this.leftPants.visible = renderState.showLeftPants;
        this.rightPants.visible = renderState.showRightPants;
        this.leftSleeve.visible = renderState.showLeftSleeve;
        this.rightSleeve.visible = renderState.showRightSleeve;
        setupAnim(this, renderState);
    }

    public static void setupAnim(HumanoidModel<?> model, StrawStatueRenderState renderState) {
        model.head.xRot = Mth.DEG_TO_RAD * renderState.headPose.x();
        model.head.yRot = Mth.DEG_TO_RAD * renderState.headPose.y();
        model.head.zRot = Mth.DEG_TO_RAD * renderState.headPose.z();
        model.leftArm.xRot = Mth.DEG_TO_RAD * renderState.leftArmPose.x();
        model.leftArm.yRot = Mth.DEG_TO_RAD * renderState.leftArmPose.y();
        model.leftArm.zRot = Mth.DEG_TO_RAD * renderState.leftArmPose.z();
        model.rightArm.xRot = Mth.DEG_TO_RAD * renderState.rightArmPose.x();
        model.rightArm.yRot = Mth.DEG_TO_RAD * renderState.rightArmPose.y();
        model.rightArm.zRot = Mth.DEG_TO_RAD * renderState.rightArmPose.z();
        model.leftLeg.xRot = Mth.DEG_TO_RAD * renderState.leftLegPose.x();
        model.leftLeg.yRot = Mth.DEG_TO_RAD * renderState.leftLegPose.y();
        model.leftLeg.zRot = Mth.DEG_TO_RAD * renderState.leftLegPose.z();
        model.rightLeg.xRot = Mth.DEG_TO_RAD * renderState.rightLegPose.x();
        model.rightLeg.yRot = Mth.DEG_TO_RAD * renderState.rightLegPose.y();
        model.rightLeg.zRot = Mth.DEG_TO_RAD * renderState.rightLegPose.z();
        if (renderState.isCrouching) {
            model.body.xRot = 0.5F;
            model.rightArm.xRot += 0.4F;
            model.leftArm.xRot += 0.4F;
            float babyScale = renderState.isBaby ? 0.5F : 1.0F;
            model.rightLeg.z += 4.0F * babyScale;
            model.leftLeg.z += 4.0F * babyScale;
            model.head.y += 4.2F * babyScale;
            model.body.y += 3.2F * babyScale;
            model.leftArm.y += 3.2F * babyScale;
            model.rightArm.y += 3.2F * babyScale;
        }
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        this.root().translateAndRotate(poseStack);
        ModelPart modelPart = this.getArm(side);
        if (this.slim) {
            float f = 0.5F * (side == HumanoidArm.RIGHT ? 1 : -1);
            modelPart.x += f;
            modelPart.translateAndRotate(poseStack);
            modelPart.x -= f;
        } else {
            modelPart.translateAndRotate(poseStack);
        }
    }
}
