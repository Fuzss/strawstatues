package fuzs.strawstatues.client.entity;

import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.client.gui.screens.StrawStatueModelPartsScreen;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @see net.minecraft.client.entity.ClientMannequin
 */
public class ClientStrawStatue extends StrawStatue implements ClientAvatarEntity {
    private final ClientAvatarState avatarState = new ClientAvatarState();
    @Nullable
    private CompletableFuture<Optional<PlayerSkin>> skinLookup;
    private PlayerSkin skin = DEFAULT_SKIN;
    private boolean showExtraEars;
    private final PlayerSkinRenderCache skinRenderCache;
    private float oldScale = 1.0F;
    private Rotations oldEntityPose = DEFAULT_ENTITY_POSE;
    private Rotations oldHeadPose = ArmorStand.DEFAULT_HEAD_POSE;
    private Rotations oldBodyPose = ArmorStand.DEFAULT_BODY_POSE;
    private Rotations oldLeftArmPose = ArmorStand.DEFAULT_LEFT_ARM_POSE;
    private Rotations oldRightArmPose = ArmorStand.DEFAULT_RIGHT_ARM_POSE;
    private Rotations oldLeftLegPose = ArmorStand.DEFAULT_LEFT_LEG_POSE;
    private Rotations oldRightLegPose = ArmorStand.DEFAULT_RIGHT_LEG_POSE;

    public ClientStrawStatue(EntityType<? extends StrawStatue> entityType, Level level, PlayerSkinRenderCache playerSkinRenderCache) {
        super(entityType, level);
        this.skinRenderCache = playerSkinRenderCache;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.oldScale = this.getScale();
            this.oldEntityPose = this.getEntityPose();
            this.oldHeadPose = this.getHeadPose();
            this.oldBodyPose = this.getBodyPose();
            this.oldLeftArmPose = this.getLeftArmPose();
            this.oldRightArmPose = this.getRightArmPose();
            this.oldLeftLegPose = this.getLeftLegPose();
            this.oldRightLegPose = this.getRightLegPose();
            this.avatarState.tick(this.position(), this.getDeltaMovement());
            this.lookupSkin();
        }
    }

    private void lookupSkin() {
        if (this.skinLookup != null && this.skinLookup.isDone()) {
            try {
                this.skinLookup.get().ifPresent(this::setSkin);
                this.skinLookup = null;
            } catch (Exception exception) {
                StrawStatues.LOGGER.error("Error when trying to look up skin", exception);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (this.level().isClientSide()) {
            if (Objects.equals(dataAccessor, DATA_PROFILE) || Objects.equals(dataAccessor, DATA_DYNAMIC_PROFILE)) {
                this.updateProfile();
            }
        }
    }

    private void updateProfile() {
        this.updateSkin();
        if (Minecraft.getInstance().screen instanceof StrawStatueModelPartsScreen screen) {
            screen.applyNameValue();
        }
    }

    /**
     * @see ClientMannequin#updateSkin()
     */
    private void updateSkin() {
        if (this.skinLookup != null) {
            CompletableFuture<Optional<PlayerSkin>> completableFuture = this.skinLookup;
            this.skinLookup = null;
            completableFuture.cancel(false);
        }

        if (Objects.equals(this.getProfile(), DEFAULT_PROFILE)) {
            this.setSkin(DEFAULT_SKIN);
        } else {
            this.skinLookup = this.skinRenderCache.lookup(this.getSkinProfile())
                    .thenApply(optional -> optional.map(PlayerSkinRenderCache.RenderInfo::playerSkin));
            this.showExtraEars = Objects.equals("deadmau5", this.getProfileName());
        }
    }

    private ResolvableProfile getSkinProfile() {
        return this.isDynamicProfile() ? ResolvableProfile.createUnresolved(this.getProfileName()) : this.getProfile();
    }

    public float getScale(float partialTick) {
        return Mth.lerp(partialTick, this.oldScale, this.getScale());
    }

    public float getPoseX(float partialTick) {
        return Mth.rotLerp(partialTick, this.oldEntityPose.x(), this.getEntityPose().x());
    }

    public float getPoseZ(float partialTick) {
        return Mth.rotLerp(partialTick, this.oldEntityPose.z(), this.getEntityPose().z());
    }

    public Rotations getHeadPose(float partialTick) {
        return lerpRotations(partialTick, this.oldHeadPose, this.getHeadPose());
    }

    public Rotations getBodyPose(float partialTick) {
        return lerpRotations(partialTick, this.oldBodyPose, this.getBodyPose());
    }

    public Rotations getLeftArmPose(float partialTick) {
        return lerpRotations(partialTick, this.oldLeftArmPose, this.getLeftArmPose());
    }

    public Rotations getRightArmPose(float partialTick) {
        return lerpRotations(partialTick, this.oldRightArmPose, this.getRightArmPose());
    }

    public Rotations getLeftLegPose(float partialTick) {
        return lerpRotations(partialTick, this.oldLeftLegPose, this.getLeftLegPose());
    }

    public Rotations getRightLegPose(float partialTick) {
        return lerpRotations(partialTick, this.oldRightLegPose, this.getRightLegPose());
    }

    private static Rotations lerpRotations(float partialTick, Rotations oldRotations, Rotations newRotations) {
        float x = Mth.rotLerp(partialTick, oldRotations.x(), newRotations.x());
        float y = Mth.rotLerp(partialTick, oldRotations.y(), newRotations.y());
        float z = Mth.rotLerp(partialTick, oldRotations.z(), newRotations.z());
        return new Rotations(x, y, z);
    }

    @Override
    public ClientAvatarState avatarState() {
        return this.avatarState;
    }

    @Override
    public PlayerSkin getSkin() {
        return this.skin.with(this.getSkinPatch());
    }

    private void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }

    @Nullable
    @Override
    public Component belowNameDisplay() {
        return this.getDescription();
    }

    @Override
    public Parrot.@Nullable Variant getParrotVariantOnShoulder(boolean left) {
        return null;
    }

    @Override
    public boolean showExtraEars() {
        return this.showExtraEars;
    }
}
