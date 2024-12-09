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
        super.setupAnim(renderState);
        setupAnim(this, (StrawStatueRenderState) renderState);
    }

    public static void setupAnim(HumanoidModel<?> model, StrawStatueRenderState renderState) {
        model.head.xRot = Mth.DEG_TO_RAD * renderState.headPose.getX();
        model.head.yRot = Mth.DEG_TO_RAD * renderState.headPose.getY();
        model.head.zRot = Mth.DEG_TO_RAD * renderState.headPose.getZ();
        model.leftArm.xRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getX();
        model.leftArm.yRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getY();
        model.leftArm.zRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getZ();
        model.rightArm.xRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getX();
        model.rightArm.yRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getY();
        model.rightArm.zRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getZ();
        model.leftLeg.xRot = Mth.DEG_TO_RAD * renderState.leftLegPose.getX();
        model.leftLeg.yRot = Mth.DEG_TO_RAD * renderState.leftLegPose.getY();
        model.leftLeg.zRot = Mth.DEG_TO_RAD * renderState.leftLegPose.getZ();
        model.rightLeg.xRot = Mth.DEG_TO_RAD * renderState.rightLegPose.getX();
        model.rightLeg.yRot = Mth.DEG_TO_RAD * renderState.rightLegPose.getY();
        model.rightLeg.zRot = Mth.DEG_TO_RAD * renderState.rightLegPose.getZ();
        if (renderState.isCrouching) {
            model.body.xRot = 0.5F;
            model.rightArm.xRot += 0.4F;
            model.leftArm.xRot += 0.4F;
            model.rightLeg.z = 4.0F;
            model.leftLeg.z = 4.0F;
            model.rightLeg.y = 12.2F;
            model.leftLeg.y = 12.2F;
            model.head.y = 4.2F;
            model.body.y = 3.2F;
            model.leftArm.y = 5.2F;
            model.rightArm.y = 5.2F;
        }
    }
}
