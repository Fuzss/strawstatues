package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;

public class StrawStatueModel extends PlayerModel {

    public StrawStatueModel(ModelPart modelPart, PlayerModelType modelType) {
        super(modelPart, modelType == PlayerModelType.SLIM);
    }

    @Override
    public void setupAnim(AvatarRenderState renderState) {
        super.setupAnim(renderState);
        this.setupAnim((StrawStatueRenderState) renderState);
    }

    private void setupAnim(StrawStatueRenderState renderState) {
        this.head.xRot = Mth.DEG_TO_RAD * renderState.headPose.x();
        this.head.yRot = Mth.DEG_TO_RAD * renderState.headPose.y();
        this.head.zRot = Mth.DEG_TO_RAD * renderState.headPose.z();
        this.leftArm.xRot = Mth.DEG_TO_RAD * renderState.leftArmPose.x();
        this.leftArm.yRot = Mth.DEG_TO_RAD * renderState.leftArmPose.y();
        this.leftArm.zRot = Mth.DEG_TO_RAD * renderState.leftArmPose.z();
        this.rightArm.xRot = Mth.DEG_TO_RAD * renderState.rightArmPose.x();
        this.rightArm.yRot = Mth.DEG_TO_RAD * renderState.rightArmPose.y();
        this.rightArm.zRot = Mth.DEG_TO_RAD * renderState.rightArmPose.z();
        this.leftLeg.xRot = Mth.DEG_TO_RAD * renderState.leftLegPose.x();
        this.leftLeg.yRot = Mth.DEG_TO_RAD * renderState.leftLegPose.y();
        this.leftLeg.zRot = Mth.DEG_TO_RAD * renderState.leftLegPose.z();
        this.rightLeg.xRot = Mth.DEG_TO_RAD * renderState.rightLegPose.x();
        this.rightLeg.yRot = Mth.DEG_TO_RAD * renderState.rightLegPose.y();
        this.rightLeg.zRot = Mth.DEG_TO_RAD * renderState.rightLegPose.z();
    }
}
