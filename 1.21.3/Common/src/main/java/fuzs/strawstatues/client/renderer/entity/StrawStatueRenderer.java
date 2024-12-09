package fuzs.strawstatues.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueArmorModel;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.model.StrawStatueCapeModel;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class StrawStatueRenderer extends LivingEntityRenderer<StrawStatue, PlayerRenderState, StrawStatueModel> {
    public static final ResourceLocation STRAW_STATUE_LOCATION = StrawStatues.id("textures/entity/straw_statue.png");

    public StrawStatueRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE), false), 0.0F);
        this.addLayer(new HumanoidArmorLayer<>(this, new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_INNER_ARMOR)), new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR)), context.getEquipmentRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemRenderer()));
        this.addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        this.addPlayerLayer((RenderLayerParent<PlayerRenderState, PlayerModel> entityRenderer) -> new Deadmau5EarsLayer(entityRenderer, context.getModelSet()));
        this.addPlayerLayer((RenderLayerParent<PlayerRenderState, PlayerModel> entityRenderer) -> new CapeLayer(entityRenderer, context.getModelSet(), context.getEquipmentModels()) {

            {
                this.model = new StrawStatueCapeModel(context);
            }
        });
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemRenderer()));
    }

    private boolean addPlayerLayer(Function<RenderLayerParent<PlayerRenderState, PlayerModel>, RenderLayer<PlayerRenderState, PlayerModel>> factory) {
        RenderLayer<PlayerRenderState, PlayerModel> layer = factory.apply((RenderLayerParent<PlayerRenderState, PlayerModel>) (RenderLayerParent<?, ?>) this);
        return this.layers.add((RenderLayer<PlayerRenderState, StrawStatueModel>) (RenderLayer<?, ?>) layer);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerRenderState renderState) {
        if (renderState.skin == DefaultPlayerSkin.getDefaultSkin()) {
            return STRAW_STATUE_LOCATION;
        } else {
            return renderState.skin.texture();
        }
    }

    public static Optional<PlayerSkin> getPlayerProfileTexture(StrawStatue strawStatue) {
        return strawStatue.getProfile().map(ResolvableProfile::gameProfile).map((GameProfile gameProfile) -> {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.getSkinManager().getInsecureSkin(gameProfile);
        });
    }

    @Override
    public void render(PlayerRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public Vec3 getRenderOffset(PlayerRenderState renderState) {
        Vec3 vec3 = super.getRenderOffset(renderState);
        return renderState.isCrouching ? vec3.add(0.0, (double)(renderState.scale * -2.0F) / 16.0, 0.0) : vec3;
    }

    @Override
    public PlayerRenderState createRenderState() {
        return new StrawStatueRenderState();
    }

    @Override
    public void extractRenderState(StrawStatue strawStatue, PlayerRenderState reusedState, float partialTick) {
        super.extractRenderState(strawStatue, reusedState, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(strawStatue, reusedState, partialTick);
        StrawStatueRenderState strawStatueRenderState = (StrawStatueRenderState) reusedState;
        strawStatueRenderState.isMarker = strawStatue.isMarker();
        strawStatueRenderState.bodyPose = strawStatue.getBodyPose();
        strawStatueRenderState.headPose = strawStatue.getHeadPose();
        strawStatueRenderState.leftArmPose = strawStatue.getLeftArmPose();
        strawStatueRenderState.rightArmPose = strawStatue.getRightArmPose();
        strawStatueRenderState.leftLegPose = strawStatue.getLeftLegPose();
        strawStatueRenderState.rightLegPose = strawStatue.getRightLegPose();
        strawStatueRenderState.wiggle = (float)(strawStatue.level().getGameTime() - strawStatue.lastHit) + partialTick;
        strawStatueRenderState.rotationZ = Mth.lerp(partialTick, strawStatue.entityRotationsO.getZ(), strawStatue.getEntityZRotation());
        strawStatueRenderState.rotationX = Mth.lerp(partialTick, strawStatue.entityRotationsO.getX(), strawStatue.getEntityXRotation());
        reusedState.skin = getPlayerProfileTexture(strawStatue).orElseGet(DefaultPlayerSkin::getDefaultSkin);
        reusedState.showHat = strawStatue.isModelPartShown(PlayerModelPart.HAT);
        reusedState.showJacket = strawStatue.isModelPartShown(PlayerModelPart.JACKET);
        reusedState.showLeftPants = strawStatue.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        reusedState.showRightPants = strawStatue.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        reusedState.showLeftSleeve = strawStatue.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        reusedState.showRightSleeve = strawStatue.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        reusedState.showCape = strawStatue.isModelPartShown(PlayerModelPart.CAPE);
        strawStatueRenderState.slimArms = strawStatue.slimArms();
        strawStatueRenderState.entityScale = Mth.lerp(partialTick, strawStatue.entityScaleO, strawStatue.getEntityScale());
    }

    @Override
    protected void scale(PlayerRenderState renderState, PoseStack poseStack) {
        super.scale(renderState, poseStack);
        float entityScale = ((StrawStatueRenderState) renderState).entityScale;
        entityScale /= StrawStatue.DEFAULT_ENTITY_SCALE;
        entityScale *= 0.9375F;
        poseStack.scale(entityScale, entityScale, entityScale);
    }

    @Override
    protected void setupRotations(PlayerRenderState renderState, PoseStack poseStack, float bodyRot, float scale) {

        StrawStatueRenderState strawStatueRenderState = (StrawStatueRenderState) renderState;

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F - strawStatueRenderState.rotationZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F - strawStatueRenderState.rotationX));

        if (strawStatueRenderState.wiggle < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(strawStatueRenderState.wiggle / 1.5F * (float) Math.PI) * 3.0F));
        }

        if (renderState.isUpsideDown) {
            poseStack.translate(0.0F, (renderState.boundingBoxHeight + 0.1F) / scale, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    protected boolean shouldShowName(StrawStatue entity, double distanceToCameraSq) {
        float f = entity.isCrouching() ? 32.0F : 64.0F;
        return !(distanceToCameraSq >= f * f) && entity.isCustomNameVisible();
    }

    @Override
    protected @Nullable RenderType getRenderType(PlayerRenderState renderState, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
        if (!((StrawStatueRenderState) renderState).isMarker) {
            return super.getRenderType(renderState, isVisible, renderTranslucent, appearsGlowing);
        } else {
            ResourceLocation resourceLocation = this.getTextureLocation(renderState);
            if (renderTranslucent) {
                return RenderType.entityTranslucent(resourceLocation, false);
            } else {
                return isVisible ? RenderType.entityCutoutNoCull(resourceLocation, false) : null;
            }
        }
    }
}
