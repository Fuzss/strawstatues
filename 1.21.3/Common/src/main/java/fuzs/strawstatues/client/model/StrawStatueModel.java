package fuzs.strawstatues.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Collections;

public class StrawStatueModel extends PlayerModel {
    public final ModelPart slimLeftArm;
    public final ModelPart slimRightArm;
    public final ModelPart slimLeftSleeve;
    public final ModelPart slimRightSleeve;

    private boolean slim;

    public StrawStatueModel(ModelPart modelPart, boolean slim) {
        super(modelPart, slim);
        this.slimLeftArm = modelPart.getChild("slim_left_arm");
        this.slimRightArm = modelPart.getChild("slim_right_arm");
        this.slimLeftSleeve = this.slimLeftArm.getChild("left_sleeve");
        this.slimRightSleeve = this.slimRightArm.getChild("right_sleeve");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
        MeshDefinition slimMeshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, true);
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition slimRoot = slimMeshDefinition.getRoot();
        root.addOrReplaceChild("slim_left_arm", slimRoot.getChild("left_arm"));
        root.addOrReplaceChild("slim_right_arm", slimRoot.getChild("right_arm"));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(PlayerRenderState renderState) {
        super.setupAnim(renderState);
        setupPoseAnim(this, (StrawStatueRenderState) renderState);
        this.setupSlimAnim((StrawStatueRenderState) renderState);
//        this.setupCloakAnim((StrawStatueRenderState) renderState);
//        this.hat.copyFrom(this.head);
//        this.leftPants.copyFrom(this.leftLeg);
//        this.rightPants.copyFrom(this.rightLeg);
//        this.leftSleeve.copyFrom(this.leftArm);
//        this.rightSleeve.copyFrom(this.rightArm);
//        this.slimLeftSleeve.copyFrom(this.slimLeftArm);
//        this.slimRightSleeve.copyFrom(this.slimRightArm);
//        this.jacket.copyFrom(this.body);
//        this.setupCrouchingAnimCape((StrawStatueRenderState) renderState);
    }

    private void setupSlimAnim(StrawStatueRenderState renderState) {
        this.leftArm.visible = this.slimLeftArm.visible = true;
        this.rightArm.visible = this.slimRightArm.visible = true;
        // very bad hack using slim like this, it only works as hand items are rendered after the main model,
        // so the field will have the correct value for the current entity when those render
        // vanilla does this using separate renderers/models, but multiple renderers for a single entity is hardcoded only for players
        // not worth a mixin though as this seems to work fine
        this.slim = renderState.slimArms;
        this.slimLeftArm.xRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getX();
        this.slimLeftArm.yRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getY();
        this.slimLeftArm.zRot = Mth.DEG_TO_RAD * renderState.leftArmPose.getZ();
        this.slimRightArm.xRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getX();
        this.slimRightArm.yRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getY();
        this.slimRightArm.zRot = Mth.DEG_TO_RAD * renderState.rightArmPose.getZ();
        if (this.slim) {
            this.leftArm.visible = this.leftSleeve.visible = false;
            this.rightArm.visible = this.rightSleeve.visible = false;
        } else {
            this.slimLeftArm.visible = this.slimLeftSleeve.visible = false;
            this.slimRightArm.visible = this.slimRightSleeve.visible = false;
        }
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        ModelPart modelPart = this.getArm(humanoidArm);
        if (this.slim) {
            float f = 0.5F * (float)(humanoidArm == HumanoidArm.RIGHT ? 1 : -1);
            modelPart.x += f;
            modelPart.translateAndRotate(poseStack);
            modelPart.x -= f;
        } else {
            modelPart.translateAndRotate(poseStack);
        }
    }

    public static void setupPoseAnim(HumanoidModel<?> model, StrawStatueRenderState renderState) {
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
        setupCrouchingAnim(model, renderState);
    }

    private static void setupCrouchingAnim(HumanoidModel<?> model, StrawStatueRenderState renderState) {
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
        } else {
//            model.body.xRot = 0.0F;
//            model.rightLeg.z = 0.1F;
//            model.leftLeg.z = 0.1F;
//            model.rightLeg.y = 12.0F;
//            model.leftLeg.y = 12.0F;
//            model.head.y = 0.0F;
//            model.body.y = 0.0F;
//            model.leftArm.y = 2.0F;
//            model.rightArm.y = 2.0F;
        }
    }
}
