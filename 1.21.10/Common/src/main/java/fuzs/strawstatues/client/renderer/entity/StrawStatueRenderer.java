package fuzs.strawstatues.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.strawstatues.client.entity.ClientStrawStatue;
import fuzs.strawstatues.client.model.StrawStatueCapeModel;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.model.geom.ModModelLayers;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;

import java.util.Map;

public class StrawStatueRenderer extends AvatarRenderer<ClientStrawStatue> {
    private final Map<PlayerModelType, AdultAndBabyModelPair<StrawStatueModel>> models;

    public StrawStatueRenderer(EntityRendererProvider.Context context) {
        super(context, false);
        this.models = bakeModels(context);
        this.layers.removeIf((RenderLayer<AvatarRenderState, PlayerModel> renderLayer) -> {
            return renderLayer instanceof HumanoidArmorLayer<?, ?, ?> || renderLayer instanceof CapeLayer
                    || renderLayer instanceof Deadmau5EarsLayer;
        });
        this.addHumanoidArmorLayer(context,
                ModModelLayers.STRAW_STATUE_ARMOR,
                ModModelLayers.STRAW_STATUE_BABY_ARMOR,
                PlayerModelType.WIDE);
        this.addHumanoidArmorLayer(context,
                ModModelLayers.STRAW_STATUE_SLIM_ARMOR,
                ModModelLayers.STRAW_STATUE_BABY_SLIM_ARMOR,
                PlayerModelType.SLIM);
        this.addLayer(new Deadmau5EarsLayer(this, context.getModelSet()) {
            private final AdultAndBabyModelPair<HumanoidModel<AvatarRenderState>> models = new AdultAndBabyModelPair<>(
                    new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_EARS), false),
                    new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_BABY_EARS), false));

            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
                this.model = this.models.getModel(renderState.isBaby);
                super.submit(poseStack, nodeCollector, packedLight, renderState, yRot, xRot);
            }
        });
        this.addLayer(new CapeLayer(this, context.getModelSet(), context.getEquipmentAssets()) {
            private final AdultAndBabyModelPair<HumanoidModel<AvatarRenderState>> models = new AdultAndBabyModelPair<>(
                    new StrawStatueCapeModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_CAPE)),
                    new StrawStatueCapeModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_BABY_CAPE)));

            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
                this.model = this.models.getModel(renderState.isBaby);
                super.submit(poseStack, nodeCollector, packedLight, renderState, yRot, xRot);
            }
        });
    }

    private static Map<PlayerModelType, AdultAndBabyModelPair<StrawStatueModel>> bakeModels(EntityRendererProvider.Context context) {
        return Maps.newEnumMap(Map.of(PlayerModelType.WIDE,
                new AdultAndBabyModelPair<>(new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE), false),
                        new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_BABY), false)),
                PlayerModelType.SLIM,
                new AdultAndBabyModelPair<>(new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_SLIM),
                        true), new StrawStatueModel(context.bakeLayer(ModModelLayers.STRAW_STATUE_BABY_SLIM), true))));
    }

    private void addHumanoidArmorLayer(EntityRendererProvider.Context context, ArmorModelSet<ModelLayerLocation> armorModelSet, ArmorModelSet<ModelLayerLocation> babyArmorModelSet, PlayerModelType modelType) {
        this.addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(armorModelSet, context.getModelSet(), (ModelPart modelPart) -> {
                    return new StrawStatueModel(modelPart, modelType == PlayerModelType.SLIM);
                }),
                ArmorModelSet.bake(babyArmorModelSet, context.getModelSet(), (ModelPart modelPart) -> {
                    return new StrawStatueModel(modelPart, modelType == PlayerModelType.SLIM);
                }),
                context.getEquipmentRenderer()) {
            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
                if (renderState.skin.model() == modelType) {
                    super.submit(poseStack, nodeCollector, packedLight, renderState, yRot, xRot);
                }
            }
        });
    }

    // TODO enable this again when fixed on NeoForge
//    @Override
//    public void submit(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
//        this.setupSubmit(renderState);
//        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
//    }

    public void setupSubmit(AvatarRenderState renderState) {
        this.model = this.models.get(renderState.skin.model()).getModel(renderState.isBaby);
    }

    @Override
    protected float getShadowRadius(AvatarRenderState renderState) {
        return super.getShadowRadius(renderState) * renderState.ageScale;
    }

    @Override
    public StrawStatueRenderState createRenderState() {
        return new StrawStatueRenderState();
    }

    @Override
    public void extractRenderState(ClientStrawStatue strawStatue, AvatarRenderState renderState, float partialTick) {
        super.extractRenderState(strawStatue, renderState, partialTick);
        ((StrawStatueRenderState) renderState).rotX = strawStatue.getPoseX(partialTick);
        ((StrawStatueRenderState) renderState).rotZ = strawStatue.getPoseZ(partialTick);
        ((StrawStatueRenderState) renderState).wiggle =
                (strawStatue.level().getGameTime() - strawStatue.lastHit) + partialTick;
        ((StrawStatueRenderState) renderState).headPose = strawStatue.getHeadPose(partialTick);
        ((StrawStatueRenderState) renderState).bodyPose = strawStatue.getBodyPose(partialTick);
        ((StrawStatueRenderState) renderState).leftArmPose = strawStatue.getLeftArmPose(partialTick);
        ((StrawStatueRenderState) renderState).rightArmPose = strawStatue.getRightArmPose(partialTick);
        ((StrawStatueRenderState) renderState).leftLegPose = strawStatue.getLeftLegPose(partialTick);
        ((StrawStatueRenderState) renderState).rightLegPose = strawStatue.getRightLegPose(partialTick);
        // override vanilla scale property, so we can lerp the value
        renderState.scale = strawStatue.getScale(partialTick);
    }

    @Override
    protected void setupRotations(AvatarRenderState renderState, PoseStack poseStack, float bodyRot, float scale) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F - ((StrawStatueRenderState) renderState).rotZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F - ((StrawStatueRenderState) renderState).rotX));
        float wiggle = ((StrawStatueRenderState) renderState).wiggle;
        if (wiggle < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(wiggle / 1.5F * Mth.PI) * 3.0F));
        }

        if (renderState.isUpsideDown) {
            poseStack.translate(0.0F, (renderState.boundingBoxHeight + 0.1F) / scale, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }
}
