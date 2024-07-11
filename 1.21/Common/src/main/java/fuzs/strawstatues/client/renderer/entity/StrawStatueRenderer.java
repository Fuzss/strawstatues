package fuzs.strawstatues.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StrawStatueRenderer extends LivingEntityRenderer<StrawStatue, StrawStatueModel> {
    public static final ResourceLocation STRAW_STATUE_LOCATION = StrawStatues.id("textures/entity/straw_statue.png");

    public StrawStatueRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawStatueModel(context.bakeLayer(ModClientRegistry.STRAW_STATUE), false), 0.0F);
        this.addLayer(new HumanoidArmorLayer<>(this, new StrawStatueArmorModel<>(context.bakeLayer(ModClientRegistry.STRAW_STATUE_INNER_ARMOR)), new StrawStatueArmorModel<>(context.bakeLayer(ModClientRegistry.STRAW_STATUE_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new StrawStatueDeadmau5EarsLayer(this));
        this.addLayer(new StrawStatueCapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    public static Optional<PlayerSkin> getPlayerProfileTexture(StrawStatue strawStatue) {
        return strawStatue.getOwner().map(ResolvableProfile::gameProfile).map((GameProfile gameProfile) -> {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.getSkinManager().getInsecureSkin(gameProfile);
        });
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
        return getPlayerProfileTexture(entity).map(PlayerSkin::texture).orElse(STRAW_STATUE_LOCATION);
    }

    @Override
    protected void scale(StrawStatue livingEntity, PoseStack matrixStack, float partialTickTime) {
        float modelScale = Mth.lerp(partialTickTime, livingEntity.entityScaleO, livingEntity.getEntityScale());
        modelScale /= StrawStatue.DEFAULT_ENTITY_SCALE;
        modelScale *= 0.9375F;
        matrixStack.scale(modelScale, modelScale, modelScale);
    }

    @Override
    protected void setupRotations(StrawStatue entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        float entityZRotation = Mth.lerp(partialTicks, entityLiving.entityRotationsO.getZ(), entityLiving.getEntityZRotation());
        float entityXRotation = Mth.lerp(partialTicks, entityLiving.entityRotationsO.getX(), entityLiving.getEntityXRotation());
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F - entityZRotation));
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
        matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F - entityXRotation));
        float hurtAmount = (float) (entityLiving.level().getGameTime() - entityLiving.lastHit) + partialTicks;
        if (hurtAmount < 5.0F) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hurtAmount / 1.5F * 3.1415927F) * 3.0F));
        }
        if (isEntityUpsideDown(entityLiving)) {
            matrixStack.translate(0.0, entityLiving.getBbHeight() - 0.0625F, 0.0);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
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
