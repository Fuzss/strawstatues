package fuzs.strawstatues.world.entity.decoration;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.statuemenus.api.v1.helper.ArmorStandInteractHelper;
import fuzs.statuemenus.api.v1.world.entity.decoration.ArmorStandDataProvider;
import fuzs.statuemenus.api.v1.world.inventory.data.*;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.core.Proxy;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StrawStatue extends ArmorStand implements ArmorStandDataProvider {
    protected static final EntityDimensions BABY_DIMENSIONS = Player.STANDING_DIMENSIONS.scale(0.5F);
    protected static final Map<Pose, EntityDimensions> BABY_POSES = Player.POSES.entrySet()
            .stream()
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey,
                    (Map.Entry<Pose, EntityDimensions> entry) -> entry.getValue().scale(0.5F)));
    public static final Rotations DEFAULT_ENTITY_ROTATIONS = new Rotations(180.0F, 0.0F, 180.0F);
    public static final String OWNER_KEY = "Owner";
    public static final String PROFILE_KEY = "profile";
    public static final String SLIM_ARMS_KEY = "SlimArms";
    public static final String CROUCHING_KEY = "Crouching";
    public static final String MODEL_PARTS_KEY = "ModelParts";
    public static final String ENTITY_ROTATIONS_KEY = "EntityRotations";
    public static final EntityDataAccessor<Optional<ResolvableProfile>> DATA_PROFILE = SynchedEntityData.defineId(
            StrawStatue.class,
            ModRegistry.RESOLVABLE_PROFILE_ENTITY_DATA_SERIALIZER.value());
    public static final EntityDataAccessor<Boolean> DATA_SLIM_ARMS = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> DATA_PLAYER_MODEL_CUSTOMISATION = SynchedEntityData.defineId(
            StrawStatue.class,
            EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Rotations> DATA_ENTITY_ROTATIONS = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);

    public float scaleO = 1.0F;
    public Rotations entityRotationsO = DEFAULT_ENTITY_ROTATIONS;

    public StrawStatue(EntityType<? extends StrawStatue> entityType, Level level) {
        super(entityType, level);
        // important to enable arms beyond rendering in the model to allow for in world interactions (putting items into the hands by clicking on the statue)
        ArmorStandStyleOption.setArmorStandData(this, true, ArmorStand.CLIENT_FLAG_SHOW_ARMS);
        ArmorStandStyleOption.setArmorStandData(this, true, ArmorStand.CLIENT_FLAG_NO_BASEPLATE);
    }

    public StrawStatue(Level level, double x, double y, double z) {
        this(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PROFILE, Optional.empty());
        builder.define(DATA_SLIM_ARMS, false);
        builder.define(DATA_PLAYER_MODEL_CUSTOMISATION, getAllModelParts());
        builder.define(DATA_ENTITY_ROTATIONS, DEFAULT_ENTITY_ROTATIONS);
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
        tag.putByte(MODEL_PARTS_KEY, this.entityData.get(DATA_PLAYER_MODEL_CUSTOMISATION));
        this.entityData.get(DATA_PROFILE).ifPresent((ResolvableProfile resolvableProfile) -> {
            tag.put(PROFILE_KEY, ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, resolvableProfile).getOrThrow());
        });
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
            this.entityData.set(DATA_PLAYER_MODEL_CUSTOMISATION, tag.getByte(MODEL_PARTS_KEY));
        }
        Optional<Dynamic<?>> optional;
        if (tag.contains(PROFILE_KEY, Tag.TAG_COMPOUND)) {
            optional = Optional.of(new Dynamic<>(NbtOps.INSTANCE, tag.get(PROFILE_KEY)));
        } else if (tag.contains(OWNER_KEY, Tag.TAG_COMPOUND)) {
            // backwards compatibility with the old game profile format
            optional = Optional.of(ItemStackComponentizationFix.fixProfile(new Dynamic<>(NbtOps.INSTANCE,
                    tag.getCompound(OWNER_KEY))));
        } else {
            optional = Optional.empty();
        }
        optional.map(ResolvableProfile.CODEC::parse)
                .flatMap((DataResult<ResolvableProfile> dataResult) -> dataResult.resultOrPartial((string) -> {
                    StrawStatues.LOGGER.error("Failed to load profile from straw statue: {}", string);
                }))
                .ifPresent(this::setProfile);
        this.scaleO = this.getScale();
        if (tag.contains(ENTITY_ROTATIONS_KEY, Tag.TAG_LIST)) {
            Rotations entityRotations = new Rotations(tag.getList(ENTITY_ROTATIONS_KEY, Tag.TAG_FLOAT));
            this.setEntityRotations(entityRotations.getX(), entityRotations.getZ());
            this.entityRotationsO = this.getEntityRotations();
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return (this.isBaby() ? BABY_POSES : Player.POSES).getOrDefault(pose,
                this.isBaby() ? BABY_DIMENSIONS : Player.STANDING_DIMENSIONS);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_PROFILE.equals(key)) {
            Proxy.INSTANCE.setModelPartsScreenGameProfile(this.getProfile());
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand interactionHand) {
        // apply profile from a player head when clicked on the statue
        if (!player.level().isClientSide && !player.isSpectator() && !this.isMarker()) {
            ItemStack itemInHand = player.getItemInHand(interactionHand);
            if (itemInHand.is(Items.PLAYER_HEAD)) {
                ResolvableProfile resolvableProfile = itemInHand.get(DataComponents.PROFILE);
                if (resolvableProfile != null &&
                        !Objects.equals(resolvableProfile.name(), this.getProfile().flatMap(ResolvableProfile::name))) {
                    this.setProfile(resolvableProfile);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.interactAt(player, vec, interactionHand);
    }

    public static EventResultHolder<InteractionResult> onUseEntityAt(Player player, Level level, InteractionHand interactionHand, Entity target, Vec3 hitVector) {
        if (!player.isSpectator() && target.getType() == ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()) {
            return ArmorStandInteractHelper.tryOpenArmorStatueMenu(player,
                    level,
                    interactionHand,
                    (ArmorStand) target,
                    ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                    null);
        } else {
            return EventResultHolder.pass();
        }
    }

    @Override
    public boolean showArms() {
        return true;
    }

    @Override
    public boolean showBasePlate() {
        return false;
    }

    @Override
    public boolean isMarker() {
        return false;
    }

    public Optional<ResolvableProfile> getProfile() {
        return this.entityData.get(DATA_PROFILE);
    }

    public void setProfile(@Nullable ResolvableProfile resolvableProfile) {
        this.entityData.set(DATA_PROFILE, Optional.ofNullable(resolvableProfile));
        if (resolvableProfile != null && !resolvableProfile.isResolved()) {
            resolvableProfile.resolve().thenAcceptAsync((ResolvableProfile newResolvableProfile) -> {
                this.entityData.set(DATA_PROFILE, Optional.of(newResolvableProfile));
            }, SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
        }
    }

    public boolean slimArms() {
        return this.entityData.get(DATA_SLIM_ARMS);
    }

    public void setSlimArms(boolean slimArms) {
        this.entityData.set(DATA_SLIM_ARMS, slimArms);
    }

    public boolean isModelPartShown(PlayerModelPart part) {
        return (this.getEntityData().get(DATA_PLAYER_MODEL_CUSTOMISATION) & part.getMask()) == part.getMask();
    }

    public void setModelPart(PlayerModelPart modelPart, boolean enable) {
        this.entityData.set(DATA_PLAYER_MODEL_CUSTOMISATION,
                ArmorStandStyleOption.setBit(this.getEntityData().get(DATA_PLAYER_MODEL_CUSTOMISATION),
                        modelPart.getMask(),
                        enable));
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

    @Override
    public void baseTick() {
        super.baseTick();
        this.scaleO = this.getScale();
        this.entityRotationsO = this.getEntityRotations();
    }

    public void setCrouching(boolean isCrouching) {
        this.setPose(isCrouching ? Pose.CROUCHING : Pose.STANDING);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (!this.isRemoved()) {
            if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.kill(serverLevel);
                return false;
            } else if (!this.isInvulnerableTo(serverLevel, damageSource) && !this.isInvisible() && !this.isMarker()) {
                if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                    this.brokenByAnything(serverLevel, damageSource);
                    this.kill(serverLevel);
                    return false;
                } else if (damageSource.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
                    if (this.isOnFire()) {
                        this.causeDamage(serverLevel, damageSource, 0.15F);
                    } else {
                        this.igniteForSeconds(5.0F);
                    }
                    return false;
                } else if (damageSource.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
                    this.causeDamage(serverLevel, damageSource, 4.0F);
                    return false;
                } else {
                    boolean bl = damageSource.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
                    boolean bl2 = damageSource.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
                    if (!bl && !bl2) {
                        return false;
                    } else if (damageSource.getEntity() instanceof Player &&
                            !((Player) damageSource.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (damageSource.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.kill(serverLevel);
                        return true;
                    } else {
                        long gameTime = this.level().getGameTime();
                        if (gameTime - this.lastHit > 5L && !bl2) {
                            this.level().broadcastEntityEvent(this, EntityEvent.ARMORSTAND_WOBBLE);
                            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
                            this.lastHit = gameTime;
                            this.invulnerableTime = 20;
                            this.hurtDuration = 10;
                            this.hurtTime = this.hurtDuration;
                        } else {
                            this.brokenByPlayer(serverLevel, damageSource);
                            this.showBreakingParticles();
                            this.kill(serverLevel);
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

    private void brokenByPlayer(ServerLevel serverLevel, DamageSource damageSource) {
        ItemStack itemStack = new ItemStack(ModRegistry.STRAW_STATUE_ITEM.value());
        itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        Block.popResource(this.level(), this.blockPosition(), itemStack);
        this.brokenByAnything(serverLevel, damageSource);
    }

    private void playBrokenSound() {
        this.level()
                .playSound(null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.ARMOR_STAND_BREAK,
                        this.getSoundSource(),
                        1.0F,
                        1.0F);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel) this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
                            Blocks.HAY_BLOCK.defaultBlockState()),
                    this.getX(),
                    this.getY(0.6666666666666666),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F,
                    0.05);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityEvent.ARMORSTAND_WOBBLE) {
            this.lastHit = this.level().getGameTime();
        }
        super.handleEntityEvent(id);
    }

    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.GRASS_FALL, SoundEvents.GRASS_FALL);
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
        return new ItemStack(ModRegistry.STRAW_STATUE_ITEM.value());
    }

    @Override
    public ArmorStandScreenType[] getScreenTypes() {
        return new ArmorStandScreenType[]{
                ArmorStandScreenType.ROTATIONS,
                ArmorStandScreenType.POSES,
                ArmorStandScreenType.STYLE,
                ModRegistry.MODEL_PARTS_SCREEN_TYPE,
                ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE,
                ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE,
                ArmorStandScreenType.EQUIPMENT
        };
    }

    @Override
    public PosePartMutator[] getPosePartMutators() {
        return new PosePartMutator[]{
                PosePartMutator.HEAD,
                ModRegistry.CAPE_POSE_PART_MUTATOR,
                PosePartMutator.RIGHT_ARM,
                PosePartMutator.LEFT_ARM,
                PosePartMutator.RIGHT_LEG,
                PosePartMutator.LEFT_LEG
        };
    }

    @Override
    public ArmorStandPose getRandomPose(boolean clampRotations) {
        return ArmorStandPose.randomize(this.getPosePartMutators(), clampRotations);
    }

    @Override
    public ArmorStandStyleOption[] getStyleOptions() {
        return new ArmorStandStyleOption[]{
                ArmorStandStyleOptions.SHOW_NAME,
                ArmorStandStyleOptions.SMALL,
                ModRegistry.SLIM_ARMS_STYLE_OPTION,
                ModRegistry.CROUCHING_STYLE_OPTION,
                ArmorStandStyleOptions.NO_GRAVITY,
                ArmorStandStyleOptions.SEALED
        };
    }

    @Override
    public Runnable setupInInventoryRendering(ArmorStand armorStand) {
        Runnable runnable = ArmorStandDataProvider.super.setupInInventoryRendering(armorStand);
        final Rotations rotations = ((StrawStatue) armorStand).getEntityRotations();
        ((StrawStatue) armorStand).setEntityRotations(StrawStatue.DEFAULT_ENTITY_ROTATIONS.getX(),
                StrawStatue.DEFAULT_ENTITY_ROTATIONS.getZ());
        return () -> {
            runnable.run();
            ((StrawStatue) armorStand).setEntityRotations(rotations.getX(), rotations.getZ());
        };
    }
}
