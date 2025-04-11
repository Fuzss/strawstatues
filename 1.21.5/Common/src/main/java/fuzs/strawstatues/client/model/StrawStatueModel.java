package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

public class StrawStatueModel extends PlayerModel {

    public StrawStatueModel(ModelPart modelPart, boolean slim) {
        super(modelPart, slim);
    }

    @Override
    public void setupAnim(PlayerRenderState renderState) {
        // we need the super call for model visibilities,
        // all other model part modifications are immediately reset afterward
        super.setupAnim(renderState);
        this.resetPose();
        setupAnim(this, (StrawStatueRenderState) renderState);
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
}
