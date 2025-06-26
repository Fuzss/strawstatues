package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class StrawStatueArmorModel extends HumanoidModel<StrawStatueRenderState> {

    public StrawStatueArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(StrawStatueRenderState renderState) {
        StrawStatueModel.setupAnim(this, renderState);
    }
}
