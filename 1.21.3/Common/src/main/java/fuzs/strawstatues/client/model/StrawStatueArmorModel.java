package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class StrawStatueArmorModel extends HumanoidModel<PlayerRenderState> {

    public StrawStatueArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(PlayerRenderState renderState) {
        StrawStatueModel.setupPoseAnim(this, (StrawStatueRenderState) renderState);
    }
}
