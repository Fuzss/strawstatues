package fuzs.strawstatues.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.client.init.ModClientRegistry;
import fuzs.strawstatues.client.model.StrawStatueArmorModel;
import fuzs.strawstatues.client.model.StrawStatueModel;
import fuzs.strawstatues.client.renderer.entity.layers.StrawStatueCapeLayer;
import fuzs.strawstatues.client.renderer.entity.layers.StrawStatueDeadmau5EarsLayer;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class StrawStatueRenderer extends LivingEntityRenderer<StrawStatue, StrawStatueModel> {
    public static final ResourceLocation STRAW_STATUE_LOCATION = StrawStatues.id("textures/entity/straw_statue.png");

    public StrawStatueRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE), false), 0.0F);
        this.addLayer(new HumanoidArmorLayer<>(this, new StrawStatueArmorModel<>(context.bakeLayer(ModClientRegistry.STRAW_STATUE_INNER_ARMOR)), new StrawStatueArmorModel<>(context.bakeLayer(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR))));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new StrawStatueDeadmau5EarsLayer(this));
        this.addLayer(new StrawStatueCapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Override
    public void render(StrawStatue entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        this.setModelProperties(entity);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public Vec3 getRenderOffset(StrawStatue entity, float partialTicks) {
        return entity.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entity, partialTicks);
    }

    private void setModelProperties(StrawStatue entity) {
        StrawStatueModel model = this.getModel();
        model.setAllVisible(true);
        model.hat.visible = entity.isModelPartShown(PlayerModelPart.HAT);
        model.jacket.visible = entity.isModelPartShown(PlayerModelPart.JACKET);
        model.leftPants.visible = entity.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        model.rightPants.visible = entity.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        model.leftSleeve.visible = model.slimLeftSleeve.visible = entity.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        model.rightSleeve.visible = model.slimRightSleeve.visible = entity.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        model.crouching = entity.isCrouching();
    }

    @Override
    public ResourceLocation getTextureLocation(StrawStatue entity) {
        return getPlayerProfileTexture(entity, MinecraftProfileTexture.Type.SKIN).orElse(STRAW_STATUE_LOCATION);
    }

    public static Optional<ResourceLocation> getPlayerProfileTexture(StrawStatue entity, MinecraftProfileTexture.Type type) {
        GameProfile gameProfile = entity.getOwner().orElse(null);
        if (gameProfile != null) {
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfile);
            if (map.containsKey(type)) {
                return Optional.of(minecraft.getSkinManager().registerTexture(map.get(type), type));
            }
        }
        return Optional.empty();
    }

    @Override
    protected void scale(StrawStatue livingEntity, PoseStack matrixStack, float partialTickTime) {
        float modelScale = Mth.lerp(partialTickTime, livingEntity.modelScaleO, livingEntity.getModelScale());
        modelScale /= StrawStatue.DEFAULT_MODEL_SCALE;
        modelScale *= 0.9375F;
        matrixStack.scale(modelScale, modelScale, modelScale);
    }

    @Override
    protected void setupRotations(StrawStatue entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
//        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(entityLiving.getBodyPose().getZ()));
//        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityLiving.getBodyPose().getY()));
//        matrixStack.mulPose(Vector3f.XP.rotationDegrees(entityLiving.getBodyPose().getX()));
        float f = (float)(entityLiving.level.getGameTime() - entityLiving.lastHit) + partialTicks;
        if (f < 5.0F) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f / 1.5F * 3.1415927F) * 3.0F));
        }
        if (isEntityUpsideDown(entityLiving)) {
            matrixStack.translate(0.0, entityLiving.getBbHeight() - 0.0625F, 0.0);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    protected boolean shouldShowName(StrawStatue entity) {
        double d = this.entityRenderDispatcher.distanceToSqr(entity);
        float f = entity.isCrouching() ? 32.0F : 64.0F;
        return !(d >= f * f) && entity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderType getRenderType(StrawStatue livingEntity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (!livingEntity.isMarker()) {
            return super.getRenderType(livingEntity, bodyVisible, translucent, glowing);
        } else {
            ResourceLocation resourceLocation = this.getTextureLocation(livingEntity);
            if (translucent) {
                return RenderType.entityTranslucent(resourceLocation, false);
            } else {
                return bodyVisible ? RenderType.entityCutoutNoCull(resourceLocation, false) : null;
            }
        }
    }
}
