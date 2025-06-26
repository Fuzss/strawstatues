package fuzs.strawstatues.client.model;

import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

/**
 * @see net.minecraft.client.model.PlayerCapeModel
 */
public class StrawStatueCapeModel extends HumanoidModel<StrawStatueRenderState> {
    public final ModelPart cape = this.body.getChild("cape");

    public StrawStatueCapeModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(StrawStatueRenderState renderState) {
        super.setupAnim(renderState);
        this.cape.rotateBy(new Quaternionf().rotateY((float) -Math.PI)
                .rotateX((6.0F + renderState.capeLean / 2.0F + renderState.capeFlap) * (float) (Math.PI / 180.0))
                .rotateZ(renderState.capeLean2 / 2.0F * (float) (Math.PI / 180.0))
                .rotateY((180.0F - renderState.capeLean2 / 2.0F) * (float) (Math.PI / 180.0)));
        this.cape.xRot += -Mth.DEG_TO_RAD * renderState.bodyPose.x();
        this.cape.yRot += Mth.DEG_TO_RAD * renderState.bodyPose.y();
        this.cape.zRot += -Mth.DEG_TO_RAD * renderState.bodyPose.z();
    }
}
