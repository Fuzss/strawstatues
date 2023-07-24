package fuzs.strawstatues.world.entity.decoration;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.authlib.GameProfile;
import fuzs.strawstatues.api.helper.ArmorStandInteractHelper;
import fuzs.strawstatues.api.world.entity.decoration.ArmorStandDataProvider;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import fuzs.strawstatues.api.world.inventory.data.PosePartMutator;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.mixin.accessor.ArmorStandAccessor;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.NavigableMap;
import java.util.Optional;

public class StrawStatue extends ArmorStand implements ArmorStandDataProvider {
    public static final Rotations DEFAULT_ENTITY_ROTATIONS = new Rotations(180.0F, 0.0F, 180.0F);
    public static final float DEFAULT_ENTITY_SCALE = 3.0F;
    public static final float MIN_MODEL_SCALE = 1.0F;
    public static final float MAX_MODEL_SCALE = 8.0F;
    public static final String OWNER_KEY = "Owner";
    public static final String SLIM_ARMS_KEY = "SlimArms";
    public static final String CROUCHING_KEY = "Crouching";
    public static final String MODEL_PARTS_KEY = "ModelParts";
    public static final String ENTITY_SCALE_KEY = "EntityScale";
    public static final String ENTITY_ROTATIONS_KEY = "EntityRotations";
    public static final EntityDataAccessor<Optional<GameProfile>> DATA_OWNER = SynchedEntityData.defineId(StrawStatue.class, ModRegistry.GAME_PROFILE_ENTITY_DATA_SERIALIZER);
    public static final EntityDataAccessor<Boolean> DATA_SLIM_ARMS = SynchedEntityData.defineId(StrawStatue.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_CROUCHING = SynchedEntityData.defineId(StrawStatue.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(StrawStatue.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Float> DATA_ENTITY_SCALE = SynchedEntityData.defineId(StrawStatue.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Rotations> DATA_ENTITY_ROTATIONS = SynchedEntityData.defineId(StrawStatue.class, EntityDataSerializers.ROTATIONS);

    private final NavigableMap<Float, EntityDimensions> defaultDimensions;
    private final NavigableMap<Float, EntityDimensions> babyDimensions;
    public float entityScaleO = DEFAULT_ENTITY_SCALE;
    public Rotations entityRotationsO = DEFAULT_ENTITY_ROTATIONS;

    public StrawStatue(EntityType<? extends StrawStatue> entityType, Level level) {
        super(entityType, level);
        // important to enable arms beyond rendering in the model to allow for in world interactions (putting items into the hands by clicking on the statue)
        ArmorStandStyleOption.setArmorStandData(this, true, ArmorStand.CLIENT_FLAG_SHOW_ARMS);
        ArmorStandStyleOption.setArmorStandData(this, true, ArmorStand.CLIENT_FLAG_NO_BASEPLATE);
        this.defaultDimensions = buildStatueDimensions(entityType, false);
        this.babyDimensions = buildStatueDimensions(entityType, true);
    }

    public StrawStatue(Level level, double x, double y, double z) {
        this(ModRegistry.STRAW_STATUE_ENTITY_TYPE.get(), level);
        this.setPos(x, y, z);
    }

    private static NavigableMap<Float, EntityDimensions> buildStatueDimensions(EntityType<?> entityType, boolean forBaby) {
        final float defaultScale = DEFAULT_ENTITY_SCALE * (forBaby ? 2.0F : 1.0F);
        ImmutableSortedMap.Builder<Float, EntityDimensions> builder = ImmutableSortedMap.naturalOrder();
        for (float scale = MIN_MODEL_SCALE; scale <= MAX_MODEL_SCALE; scale += 0.5F) {
            builder.put(scale - 0.25F, entityType.getDimensions().scale(scale / defaultScale));
        }
        return builder.build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER, Optional.empty());
        this.entityData.define(DATA_SLIM_ARMS, false);
        this.entityData.define(DATA_CROUCHING, false);
        this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, getAllModelParts());
        this.entityData.define(DATA_ENTITY_SCALE, DEFAULT_ENTITY_SCALE);
        this.entityData.define(DATA_ENTITY_ROTATIONS, DEFAULT_ENTITY_ROTATIONS);
    }

    private static byte getAllModelParts() {
        byte value = 0;
        for (PlayerModelPart modelPart : PlayerModelPart.values()) {
            value |= modelPart.getMask();
        }
        return value;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(SLIM_ARMS_KEY, this.slimArms());
        tag.putBoolean(CROUCHING_KEY, this.isCrouching());
        tag.putByte(MODEL_PARTS_KEY, this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION));
        this.entityData.get(DATA_OWNER).ifPresent(owner -> {
            CompoundTag gameProfileTag = new CompoundTag();
            NbtUtils.writeGameProfile(gameProfileTag, owner);
            tag.put(OWNER_KEY, gameProfileTag);
        });
        tag.putFloat(ENTITY_SCALE_KEY, this.getEntityScale());
        Rotations entityRotations = this.getEntityRotations();
        if (!DEFAULT_ENTITY_ROTATIONS.equals(entityRotations)) {
            tag.put(ENTITY_ROTATIONS_KEY, entityRotations.save());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(SLIM_ARMS_KEY, Tag.TAG_BYTE)) {
            this.setSlimArms(tag.getBoolean(SLIM_ARMS_KEY));
        }
        if (tag.contains(CROUCHING_KEY, Tag.TAG_BYTE)) {
            this.setCrouching(tag.getBoolean(CROUCHING_KEY));
        }
        if (tag.contains(MODEL_PARTS_KEY, Tag.TAG_BYTE)) {
            this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, tag.getByte(MODEL_PARTS_KEY));
        }
        if (tag.contains(OWNER_KEY, Tag.TAG_COMPOUND)) {
            this.verifyAndSetOwner(NbtUtils.readGameProfile(tag.getCompound(OWNER_KEY)));
        }
        if (tag.contains(ENTITY_SCALE_KEY, Tag.TAG_FLOAT)) {
            this.setEntityScale(tag.getFloat(ENTITY_SCALE_KEY));
            this.entityScaleO = this.getEntityScale();
        }
        if (tag.contains(ENTITY_ROTATIONS_KEY, Tag.TAG_LIST)) {
            Rotations entityRotations = new Rotations(tag.getList(ENTITY_ROTATIONS_KEY, Tag.TAG_FLOAT));
            this.setEntityRotations(entityRotations.getX(), entityRotations.getZ());
            this.entityRotationsO = this.getEntityRotations();
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.85F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.isMarker() || this.babyDimensions == null || this.defaultDimensions == null) return super.getDimensions(pose);
        NavigableMap<Float, EntityDimensions> dimensions = this.isBaby() ? this.babyDimensions : this.defaultDimensions;
        return dimensions.floorEntry(this.getEntityScale()).getValue();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_ENTITY_SCALE.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        InteractionResult result = super.interactAt(player, vec, hand);
        if (!player.level.isClientSide && !player.isSpectator() && result == InteractionResult.SUCCESS && itemInHand.is(Items.PLAYER_HEAD) && itemInHand.hasTag()) {
            GameProfile gameProfile = null;
            CompoundTag compoundTag = itemInHand.getTag();
            if (compoundTag.contains("SkullOwner", Tag.TAG_COMPOUND)) {
                gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
            } else if (compoundTag.contains("SkullOwner", Tag.TAG_STRING) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
            }
            if (gameProfile != null) this.verifyAndSetOwner(gameProfile);
        }
        return result;
    }

    public static Optional<InteractionResult> onUseEntityAt(Player player, Level level, InteractionHand interactionHand, Entity target, Vec3 hitVector) {
        if (!player.isSpectator() && target.getType() == ModRegistry.STRAW_STATUE_ENTITY_TYPE.get()) {
            return ArmorStandInteractHelper.tryOpenArmorStatueMenu(player, level, interactionHand, (ArmorStand) target, ModRegistry.STRAW_STATUE_MENU_TYPE.get());
        }
        return Optional.empty();
    }

    @Override
    public boolean isShowArms() {
        return true;
    }

    @Override
    public boolean isNoBasePlate() {
        return true;
    }

    public Optional<GameProfile> getOwner() {
        return this.entityData.get(DATA_OWNER);
    }

    public void verifyAndSetOwner(@Nullable GameProfile gameProfile) {
        // check for max name length here as client will crash when value is exceeded
        if (gameProfile != null && (!gameProfile.isComplete() || gameProfile.getName().length() > 16)) {
            if (gameProfile.getName().length() > 16) {
                if (gameProfile.getId() != null) {
                    // will throw exception if both uuid and name are empty
                    gameProfile = new GameProfile(gameProfile.getId(), "");
                } else {
                    this.setOwner(null);
                    return;
                }
            }
            SkullBlockEntity.updateGameprofile(gameProfile, this::setOwner);
        } else {
            this.setOwner(gameProfile);
        }
    }

    private void setOwner(@Nullable GameProfile gameProfile) {
        this.entityData.set(DATA_OWNER, Optional.ofNullable(gameProfile));
    }

    public boolean slimArms() {
        return this.entityData.get(DATA_SLIM_ARMS);
    }

    public void setSlimArms(boolean slimArms) {
        this.entityData.set(DATA_SLIM_ARMS, slimArms);
    }

    public boolean isModelPartShown(PlayerModelPart part) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & part.getMask()) == part.getMask();
    }

    public void setModelPart(PlayerModelPart modelPart, boolean enable) {
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ArmorStandStyleOption.setBit(this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION), modelPart.getMask(), enable));
    }

    public float getEntityScale() {
        return this.entityData.get(DATA_ENTITY_SCALE);
    }

    public float getEntityXRotation() {
        return this.getEntityRotations().getX();
    }

    public Rotations getEntityRotations() {
        return this.entityData.get(DATA_ENTITY_ROTATIONS);
    }

    public float getEntityZRotation() {
        return this.getEntityRotations().getZ();
    }

    public void setEntityXRotation(float rotationX) {
        this.setEntityRotations(rotationX, this.getEntityZRotation());
    }

    public void setEntityZRotation(float rotationZ) {
        this.setEntityRotations(this.getEntityXRotation(), rotationZ);
    }

    public void setEntityRotations(float rotationX, float rotationZ) {
        rotationX = Mth.clamp(rotationX, 0.0F, 360.0F);
        rotationZ = Mth.clamp(rotationZ, 0.0F, 360.0F);
        this.entityData.set(DATA_ENTITY_ROTATIONS, new Rotations(rotationX, 0.0F, rotationZ));
    }

    public void setEntityScale(float modelScale) {
        modelScale = clampModelScale(modelScale);
        this.entityData.set(DATA_ENTITY_SCALE, modelScale);
    }

    public static float clampModelScale(double modelScale) {
        modelScale = (int) (modelScale * 10.0) / 10.0;
        return Mth.clamp((float) modelScale, MIN_MODEL_SCALE, MAX_MODEL_SCALE);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.entityScaleO = this.getEntityScale();
        this.entityRotationsO = this.getEntityRotations();
    }

    public void setCrouching(boolean crouching) {
        this.entityData.set(DATA_CROUCHING, crouching);
    }

    @Override
    public boolean isCrouching() {
        return this.entityData.get(DATA_CROUCHING);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (DamageSource.OUT_OF_WORLD.equals(source)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(source) && !this.isInvisible() && !this.isMarker()) {
                if (source.isExplosion()) {
                    ((ArmorStandAccessor) this).callBrokenByAnything(source);
                    this.kill();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(source)) {
                    if (this.isOnFire()) {
                        ((ArmorStandAccessor) this).callCauseDamage(source, 0.15F);
                    } else {
                        this.setSecondsOnFire(5);
                    }
                    return false;
                } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
                    ((ArmorStandAccessor) this).callCauseDamage(source, 4.0F);
                    return false;
                } else {
                    boolean bl = source.getDirectEntity() instanceof AbstractArrow;
                    boolean bl2 = bl && ((AbstractArrow) source.getDirectEntity()).getPierceLevel() > 0;
                    boolean bl3 = "player".equals(source.getMsgId());
                    if (!bl3 && !bl) {
                        return false;
                    } else if (source.getEntity() instanceof Player && !((Player)source.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (source.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.kill();
                        return bl2;
                    } else {
                        long gameTime = this.level.getGameTime();
                        if (gameTime - this.lastHit > 5L && !bl) {
                            this.level.broadcastEntityEvent(this, EntityEvent.HURT);
                            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                            this.lastHit = gameTime;
                            this.invulnerableTime = 20;
                            this.hurtDuration = 10;
                            this.hurtTime = this.hurtDuration;
                        } else {
                            this.brokenByPlayer(source);
                            this.showBreakingParticles();
                            this.kill();
                        }
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void brokenByPlayer(DamageSource source) {
        Block.popResource(this.level, this.blockPosition(), new ItemStack(ModRegistry.STRAW_STATUE_ITEM.get()));
        ((ArmorStandAccessor) this).callBrokenByAnything(source);
    }

    private void playBrokenSound() {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GRASS_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, this.getBbWidth() / 4.0F, this.getBbHeight() / 4.0F, this.getBbWidth() / 4.0F, 0.05);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityEvent.HURT) {
            this.lastHit = this.level.getGameTime();
        }
        super.handleEntityEvent(id);
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.GRASS_FALL, SoundEvents.GRASS_FALL);
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GRASS_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.GRASS_BREAK;
    }

    @Override
    @Nullable
    public ItemStack getPickResult() {
        return new ItemStack(ModRegistry.STRAW_STATUE_ITEM.get());
    }

    @Override
    public ArmorStandScreenType[] getScreenTypes() {
        return new ArmorStandScreenType[]{ArmorStandScreenType.ROTATIONS, ArmorStandScreenType.POSES, StrawStatueData.STRAW_STATUE_STYLE_SCREEN_TYPE, StrawStatueData.MODEL_PARTS_SCREEN_TYPE, StrawStatueData.STRAW_STATUE_POSITION_SCREEN_TYPE, StrawStatueData.STRAW_STATUE_SCALE_SCREEN_TYPE, ArmorStandScreenType.EQUIPMENT};
    }

    @Override
    public PosePartMutator[] getPosePartMutators() {
        return new PosePartMutator[]{PosePartMutator.HEAD, StrawStatueData.CAPE_POSE_PART_MUTATOR, PosePartMutator.LEFT_ARM, PosePartMutator.RIGHT_ARM, PosePartMutator.LEFT_LEG, PosePartMutator.RIGHT_LEG};
    }

    @Override
    public ArmorStandPose getRandomPose(boolean clampRotations) {
        return ArmorStandPose.random(this.getPosePartMutators(), clampRotations);
    }
}
