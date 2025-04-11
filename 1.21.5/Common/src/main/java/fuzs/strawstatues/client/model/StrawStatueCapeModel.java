package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

public class StrawStatueCapeModel extends PlayerCapeModel<PlayerRenderState> {

    public StrawStatueCapeModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(PlayerRenderState playerRenderState) {
        super.setupAnim(playerRenderState);
        // use cloak instead of body, changing body rotations looks just weird
        this.cape.xRot += -Mth.DEG_TO_RAD * ((StrawStatueRenderState) playerRenderState).bodyPose.x();
        this.cape.yRot += Mth.DEG_TO_RAD * ((StrawStatueRenderState) playerRenderState).bodyPose.y();
        this.cape.zRot += -Mth.DEG_TO_RAD * ((StrawStatueRenderState) playerRenderState).bodyPose.z();
    }
}
