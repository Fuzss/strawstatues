package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;

public class StrawStatueCapeModel extends PlayerCapeModel {
    private final ModelPart cape;

    public StrawStatueCapeModel(ModelPart modelPart) {
        super(modelPart);
        this.cape = this.body.getChild("cape");
    }

    @Override
    public void setupAnim(AvatarRenderState renderState) {
        super.setupAnim(renderState);
        this.cape.xRot += -Mth.DEG_TO_RAD * ((StrawStatueRenderState) renderState).bodyPose.x();
        this.cape.yRot += Mth.DEG_TO_RAD * ((StrawStatueRenderState) renderState).bodyPose.y();
        this.cape.zRot += -Mth.DEG_TO_RAD * ((StrawStatueRenderState) renderState).bodyPose.z();
    }
}
