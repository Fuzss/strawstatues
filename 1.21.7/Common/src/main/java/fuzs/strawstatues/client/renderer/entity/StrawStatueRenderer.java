package fuzs.strawstatues.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueArmorModel;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.layers.StrawStatueCapeLayer;
import fuzs.strawstatues.client.renderer.entity.state.StrawStatueRenderState;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StrawStatueRenderer extends LivingEntityRenderer<StrawStatue, StrawStatueRenderState, StrawStatueModel> {
    public static final ResourceLocation STRAW_STATUE_LOCATION = StrawStatues.id("textures/entity/straw_statue.png");

    private final StrawStatueModel bigModel;
    private final StrawStatueModel smallModel;
    private final StrawStatueModel slimBigModel;
    private final StrawStatueModel slimSmallModel;

    public StrawStatueRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE), false), 0.5F);
        this.bigModel = this.getModel();
        this.smallModel = new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY), false);
        this.slimBigModel = new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_SLIM), true);
        this.slimSmallModel = new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY_SLIM), true);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_INNER_ARMOR)),
                new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR)),
                new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY_INNER_ARMOR)),
                new StrawStatueArmorModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE_BABY_OUTER_ARMOR)),
                context.getEquipmentRenderer()));
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        this.addLayer(new StrawStatueCapeLayer(this, context.getModelSet(), context.getEquipmentAssets()));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(StrawStatueRenderState renderState) {
        if (renderState.skin == DefaultPlayerSkin.getDefaultSkin()) {
            return STRAW_STATUE_LOCATION;
        } else {
            return renderState.skin.texture();
        }
    }

    @Override
    protected void scale(StrawStatueRenderState renderState, PoseStack poseStack) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public void render(StrawStatueRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (renderState.isBaby) {
            this.model = renderState.slimArms ? this.slimSmallModel : this.smallModel;
        } else {
            this.model = renderState.slimArms ? this.slimBigModel : this.bigModel;
        }
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public Vec3 getRenderOffset(StrawStatueRenderState renderState) {
        Vec3 vec3 = super.getRenderOffset(renderState);
        return renderState.isCrouching ? vec3.add(0.0, (double) (renderState.scale * -2.0F) / 16.0, 0.0) : vec3;
    }

    @Override
    public StrawStatueRenderState createRenderState() {
        return new StrawStatueRenderState();
    }

    @Override
    public void extractRenderState(StrawStatue strawStatue, StrawStatueRenderState reusedState, float partialTick) {
        super.extractRenderState(strawStatue, reusedState, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(strawStatue, reusedState, partialTick, this.itemModelResolver);
        reusedState.isMarker = strawStatue.isMarker();
        reusedState.bodyPose = strawStatue.getBodyPose();
        reusedState.headPose = strawStatue.getHeadPose();
        reusedState.leftArmPose = strawStatue.getLeftArmPose();
        reusedState.rightArmPose = strawStatue.getRightArmPose();
        reusedState.leftLegPose = strawStatue.getLeftLegPose();
        reusedState.rightLegPose = strawStatue.getRightLegPose();
        reusedState.wiggle = (float) (strawStatue.level().getGameTime() - strawStatue.lastHit) + partialTick;
        reusedState.rotationZ = Mth.lerp(partialTick,
                strawStatue.entityRotationsO.z(),
                strawStatue.getEntityZRotation());
        reusedState.rotationX = Mth.lerp(partialTick,
                strawStatue.entityRotationsO.x(),
                strawStatue.getEntityXRotation());
        reusedState.skin = getPlayerProfileTexture(strawStatue).orElseGet(DefaultPlayerSkin::getDefaultSkin);
        reusedState.showHat = strawStatue.isModelPartShown(PlayerModelPart.HAT);
        reusedState.showJacket = strawStatue.isModelPartShown(PlayerModelPart.JACKET);
        reusedState.showLeftPants = strawStatue.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        reusedState.showRightPants = strawStatue.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        reusedState.showLeftSleeve = strawStatue.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        reusedState.showRightSleeve = strawStatue.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        reusedState.showCape = strawStatue.isModelPartShown(PlayerModelPart.CAPE);
        reusedState.name = strawStatue.getProfile()
                .map(ResolvableProfile::gameProfile)
                .map(GameProfile::getName)
                .orElse("Steve");
        reusedState.slimArms = strawStatue.slimArms();
        // override vanilla scale property, so we can lerp the value
        reusedState.scale = Mth.lerp(partialTick, strawStatue.scaleO, strawStatue.getScale());
    }

    public static Optional<PlayerSkin> getPlayerProfileTexture(StrawStatue strawStatue) {
        return strawStatue.getProfile().map(ResolvableProfile::gameProfile).map((GameProfile gameProfile) -> {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.getSkinManager().getInsecureSkin(gameProfile);
        });
    }

    @Override
    protected void setupRotations(StrawStatueRenderState renderState, PoseStack poseStack, float bodyRot, float scale) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F - renderState.rotationZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F - renderState.rotationX));

        if (renderState.wiggle < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(renderState.wiggle / 1.5F * (float) Math.PI) * 3.0F));
        }

        if (renderState.isUpsideDown) {
            poseStack.translate(0.0F, (renderState.boundingBoxHeight + 0.1F) / scale, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    protected boolean shouldShowName(StrawStatue entity, double distanceToCameraSq) {
        float visibilityDistance = entity.isCrouching() ? 32.0F : 64.0F;
        return !(distanceToCameraSq >= visibilityDistance * visibilityDistance) && entity.isCustomNameVisible();
    }

    @Override
    protected @Nullable RenderType getRenderType(StrawStatueRenderState renderState, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
        if (!renderState.isMarker) {
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
