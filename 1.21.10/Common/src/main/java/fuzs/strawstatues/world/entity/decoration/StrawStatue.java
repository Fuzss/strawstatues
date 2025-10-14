package fuzs.strawstatues.world.entity.decoration;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import fuzs.statuemenus.api.v1.helper.ArmorStandInteractHelper;
import fuzs.statuemenus.api.v1.world.entity.decoration.StatueEntity;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueStyleOption;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StrawStatue extends Mannequin implements StatueEntity {
    public static final ResourceLocation STRAW_STATUE_LOCATION = StrawStatues.id("entity/straw_statue");
    public static final PlayerSkin DEFAULT_SKIN = new PlayerSkin(new ClientAsset.ResourceTexture(STRAW_STATUE_LOCATION),
            null,
            null,
            PlayerModelType.WIDE,
            true);
    public static final Rotations DEFAULT_ENTITY_POSE = new Rotations(180.0F, 0.0F, 180.0F);
    // old keys
    public static final String OWNER_KEY = "Owner";
    public static final String PROFILE_KEY = "profile";
    // new keys
    public static final String SMALL_KEY = "Small";
    public static final String PUSHABLE_KEY = "pushable";
    public static final String DYNAMIC_PROFILE_KEY = "dynamic_profile";
    public static final String SEALED_KEY = "sealed";
    public static final String ARMOR_STAND_POSE_KEY = "Pose";
    public static final String CROUCHING_KEY = "Crouching";
    public static final String ENTITY_POSE_KEY = "EntityRotations";
    public static final EntityDataAccessor<Boolean> DATA_SMALL = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_PUSHABLE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_DYNAMIC_PROFILE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_SEALED = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Rotations> DATA_ENTITY_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_BODY_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(StrawStatue.class,
            EntityDataSerializers.ROTATIONS);
    public static final StreamCodec<ByteBuf, ArmorStand.ArmorStandPose> POSE_STREAM_CODEC = StreamCodec.composite(
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::head,
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::body,
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::leftArm,
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::rightArm,
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::leftLeg,
            Rotations.STREAM_CODEC,
            ArmorStand.ArmorStandPose::rightLeg,
            ArmorStand.ArmorStandPose::new);

    @Nullable
    private CompletableFuture<GameProfile> profileLookup;
    public long lastHit;

    public StrawStatue(EntityType<? extends StrawStatue> entityType, Level level) {
        super((EntityType<Mannequin>) (EntityType<?>) entityType, level);
    }

    public StrawStatue(Level level, double x, double y, double z) {
        this(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), level);
        this.setPos(x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SMALL, false);
        builder.define(DATA_PUSHABLE, false);
        builder.define(DATA_DYNAMIC_PROFILE, false);
        builder.define(DATA_SEALED, false);
        builder.define(DATA_ENTITY_POSE, DEFAULT_ENTITY_POSE);
        builder.define(DATA_HEAD_POSE, ArmorStand.DEFAULT_HEAD_POSE);
        builder.define(DATA_BODY_POSE, ArmorStand.DEFAULT_BODY_POSE);
        builder.define(DATA_LEFT_ARM_POSE, ArmorStand.DEFAULT_LEFT_ARM_POSE);
        builder.define(DATA_RIGHT_ARM_POSE, ArmorStand.DEFAULT_RIGHT_ARM_POSE);
        builder.define(DATA_LEFT_LEG_POSE, ArmorStand.DEFAULT_LEFT_LEG_POSE);
        builder.define(DATA_RIGHT_LEG_POSE, ArmorStand.DEFAULT_RIGHT_LEG_POSE);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean(SMALL_KEY, this.isBaby());
        valueOutput.putBoolean(PUSHABLE_KEY, this.entityData.get(DATA_PUSHABLE));
        valueOutput.putBoolean(DYNAMIC_PROFILE_KEY, this.isDynamic());
        valueOutput.putBoolean(SEALED_KEY, this.isSealed());
        valueOutput.store(ARMOR_STAND_POSE_KEY, ArmorStand.ArmorStandPose.CODEC, this.getArmorStandPose());
        valueOutput.putBoolean(CROUCHING_KEY, this.isCrouching());
        valueOutput.store(ENTITY_POSE_KEY, Rotations.CODEC, this.getEntityPose());
    }

    @Override
    public void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setBaby(valueInput.getBooleanOr(SMALL_KEY, false));
        this.setPushable(valueInput.getBooleanOr(PUSHABLE_KEY, false));
        this.setDynamic(valueInput.getBooleanOr(DYNAMIC_PROFILE_KEY, false));
        this.setSealed(valueInput.getBooleanOr(SEALED_KEY, false));
        valueInput.read(ARMOR_STAND_POSE_KEY, ArmorStand.ArmorStandPose.CODEC).ifPresent(this::setArmorStandPose);
        this.setCrouching(valueInput.getBooleanOr(CROUCHING_KEY, false));
        valueInput.read(ENTITY_POSE_KEY, Rotations.CODEC).ifPresent(this::setEntityPose);
        this.readLegacySaveData(valueInput);
    }

    @Deprecated
    private void readLegacySaveData(ValueInput valueInput) {
        // TODO what to do with old profiles
        Optional<Dynamic<?>> optional = valueInput.read(PROFILE_KEY, CompoundTag.CODEC)
                .<Dynamic<?>>map((CompoundTag compoundTag) -> new Dynamic<>(NbtOps.INSTANCE, compoundTag))
                .or(() -> {
                    // backwards compatibility with the old game profile format
                    return valueInput.read(OWNER_KEY, CompoundTag.CODEC)
                            .<Dynamic<?>>map((CompoundTag compoundTag) -> ItemStackComponentizationFix.fixProfile(new Dynamic<>(
                                    NbtOps.INSTANCE,
                                    compoundTag)));
                });
        optional.map(ResolvableProfile.CODEC::parse)
                .flatMap((DataResult<ResolvableProfile> dataResult) -> dataResult.resultOrPartial((String string) -> {
                    StrawStatues.LOGGER.error("Failed to load profile from straw statue: {}", string);
                }))
                .ifPresent(this::setProfile);
    }

    @Override
    public void refreshDimensions() {
        if (this.isPushable()) {
            super.refreshDimensions();
        } else {
            Vec3 position = this.getPosition(1.0F);
            super.refreshDimensions();
            this.setPos(position);
        }
    }

    @Override
    public boolean isPushable() {
        return this.entityData.get(DATA_PUSHABLE) && super.isPushable();
    }

    public void setPushable(boolean isPushable) {
        this.entityData.set(DATA_PUSHABLE, isPushable);
        if (isPushable) {
            this.setNoGravity(false);
        }
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        super.setNoGravity(noGravity);
        if (noGravity) {
            this.setPushable(false);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (this.isPushable()) {
            super.doPush(entity);
        }
    }

    @Override
    protected void pushEntities() {
        if (this.isPushable()) {
            for (Entity entity : this.level().getPushableEntities(this, this.getBoundingBox())) {
                this.doPush(entity);
            }
        }
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        return slot.getType() == EquipmentSlot.Type.HAND || slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
    }

    @Override
    public boolean isEquippableInSlot(ItemStack itemStack, EquipmentSlot slot) {
        if (slot.getType() == EquipmentSlot.Type.HAND) {
            return true;
        } else if (slot == EquipmentSlot.HEAD && itemStack.get(DataComponents.EQUIPPABLE) == null) {
            return true;
        } else {
            return super.isEquippableInSlot(itemStack, slot);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        InteractionResult interactionResult = super.interact(player, interactionHand);
        if (interactionResult.consumesAction()) {
            return interactionResult;
        } else {
            return ArmorStandInteractHelper.openStatueMenu(player,
                    this.level(),
                    interactionHand,
                    this,
                    ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                    this);
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(this.getAgeScale());
    }

    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        this.lookupProfile();
    }

    private void lookupProfile() {
        if (this.profileLookup != null && this.profileLookup.isDone()) {
            try {
                this.setProfile(ResolvableProfile.createResolved(this.profileLookup.get()));
                this.profileLookup = null;
            } catch (Exception exception) {
                StrawStatues.LOGGER.error("Error when trying to look up profile", exception);
            }
        }
    }

    @Override
    public void setProfile(ResolvableProfile resolvableProfile) {
        super.setProfile(resolvableProfile);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (!Objects.equals(resolvableProfile.partialProfile(), this.getProfile().partialProfile())) {
                ProfileResolver profileResolver = serverLevel.getServer().services().profileResolver();
                if (this.profileLookup != null) {
                    CompletableFuture<GameProfile> completableFuture = this.profileLookup;
                    this.profileLookup = null;
                    completableFuture.cancel(false);
                }

                this.profileLookup = resolvableProfile.resolveProfile(profileResolver);
            }
        }
    }

    public boolean isDynamic() {
        return this.entityData.get(DATA_DYNAMIC_PROFILE);
    }

    public void setDynamic(boolean isDynamic) {
        this.entityData.set(DATA_DYNAMIC_PROFILE, isDynamic);
    }

    @Override
    public boolean isSealed() {
        return this.entityData.get(DATA_SEALED);
    }

    public void setSealed(boolean isSealed) {
        this.entityData.set(DATA_SEALED, isSealed);
    }

    @Override
    public boolean isBaby() {
        return this.getEntityData().get(DATA_SMALL);
    }

    public void setBaby(boolean isBaby) {
        this.getEntityData().set(DATA_SMALL, isBaby);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (Objects.equals(DATA_SMALL, dataAccessor)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.setCustomNameVisible(name != null);
    }

    public String getProfileName() {
        return this.getProfile().name().orElseGet(this.getProfile().partialProfile()::name);
    }

    public void setModelPartShown(PlayerModelPart modelPart, boolean value) {
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION,
                StatueStyleOption.setBit(this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION),
                        modelPart.getMask(),
                        value));
    }

    public void setCrouching(boolean isCrouching) {
        this.setPose(isCrouching ? Pose.CROUCHING : Pose.STANDING);
    }

    public Rotations getEntityPose() {
        return this.entityData.get(DATA_ENTITY_POSE);
    }

    public void setPoseX(float xRot) {
        this.setEntityPose(new Rotations(xRot, 0.0F, this.getEntityPose().z()));
    }

    public void setPoseZ(float zRot) {
        this.setEntityPose(new Rotations(this.getEntityPose().x(), 0.0F, zRot));
    }

    public void setEntityPose(Rotations rotations) {
        this.entityData.set(DATA_ENTITY_POSE, rotations);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (!this.isRemoved()) {
            if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.kill(serverLevel);
                return false;
            } else if (!this.isInvulnerableTo(serverLevel, damageSource) && !this.isInvisible()) {
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
                    } else if (damageSource.getEntity() instanceof Player
                            && !((Player) damageSource.getEntity()).getAbilities().mayBuild) {
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

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityEvent.ARMORSTAND_WOBBLE) {
            if (this.level().isClientSide()) {
                this.level()
                        .playLocalSound(this.getX(),
                                this.getY(),
                                this.getZ(),
                                this.getHurtSound(this.damageSources().generic()),
                                this.getSoundSource(),
                                0.3F,
                                1.0F,
                                false);
                this.lastHit = this.level().getGameTime();
            }
        }

        super.handleEntityEvent(id);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
                            Blocks.HAY_BLOCK.defaultBlockState()),
                    this.getX(),
                    this.getY(0.67),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F,
                    0.05);
        }
    }

    private void causeDamage(ServerLevel level, DamageSource damageSource, float damageAmount) {
        float f = this.getHealth();
        f -= damageAmount;
        if (f <= 0.5F) {
            this.brokenByAnything(level, damageSource);
            this.kill(level);
        } else {
            this.setHealth(f);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        }
    }

    private void brokenByPlayer(ServerLevel serverLevel, DamageSource damageSource) {
        // custom name is missing on purpose, so the profile can show as such
        // might revert if the profile is not stored on the item after all
        ItemStack itemStack = new ItemStack(ModRegistry.STRAW_STATUE_ITEM);
        itemStack.set(DataComponents.PROFILE, this.getProfile());
        itemStack.set(ModRegistry.POSE_DATA_COMPONENT_TYPE.value(), this.getArmorStandPose());
        Block.popResource(this.level(), this.blockPosition(), itemStack);
        this.brokenByAnything(serverLevel, damageSource);
    }

    private void brokenByAnything(ServerLevel serverLevel, DamageSource damageSource) {
        this.playBrokenSound();
        this.dropAllDeathLoot(serverLevel, damageSource);
        for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            ItemStack itemStack = this.equipment.set(equipmentSlot, ItemStack.EMPTY);
            if (!itemStack.isEmpty()) {
                Block.popResource(this.level(), this.blockPosition().above(), itemStack);
            }
        }
    }

    private void playBrokenSound() {
        this.level()
                .playSound(null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        this.getDeathSound(),
                        this.getSoundSource(),
                        1.0F,
                        1.0F);
    }

    @Override
    protected void tickHeadTurn(float yBodyRot) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
    }

    @Override
    public void setYBodyRot(float yBodyRot) {
        this.yBodyRotO = this.yRotO = yBodyRot;
        this.yHeadRotO = this.yHeadRot = yBodyRot;
    }

    @Override
    public void setYHeadRot(float yHeadRot) {
        this.yBodyRotO = this.yRotO = yHeadRot;
        this.yHeadRotO = this.yHeadRot = yHeadRot;
    }

    @Override
    public void kill(ServerLevel level) {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return !explosion.shouldAffectBlocklikeEntities() || super.ignoreExplosion(explosion);
    }

    @Override
    public void setHeadPose(Rotations headPose) {
        this.entityData.set(DATA_HEAD_POSE, headPose);
    }

    @Override
    public void setBodyPose(Rotations bodyPose) {
        this.entityData.set(DATA_BODY_POSE, bodyPose);
    }

    @Override
    public void setLeftArmPose(Rotations leftArmPose) {
        this.entityData.set(DATA_LEFT_ARM_POSE, leftArmPose);
    }

    @Override
    public void setRightArmPose(Rotations rightArmPose) {
        this.entityData.set(DATA_RIGHT_ARM_POSE, rightArmPose);
    }

    @Override
    public void setLeftLegPose(Rotations leftLegPose) {
        this.entityData.set(DATA_LEFT_LEG_POSE, leftLegPose);
    }

    @Override
    public void setRightLegPose(Rotations rightLegPose) {
        this.entityData.set(DATA_RIGHT_LEG_POSE, rightLegPose);
    }

    @Override
    public Rotations getHeadPose() {
        return this.entityData.get(DATA_HEAD_POSE);
    }

    @Override
    public Rotations getBodyPose() {
        return this.entityData.get(DATA_BODY_POSE);
    }

    @Override
    public Rotations getLeftArmPose() {
        return this.entityData.get(DATA_LEFT_ARM_POSE);
    }

    @Override
    public Rotations getRightArmPose() {
        return this.entityData.get(DATA_RIGHT_ARM_POSE);
    }

    @Override
    public Rotations getLeftLegPose() {
        return this.entityData.get(DATA_LEFT_LEG_POSE);
    }

    @Override
    public Rotations getRightLegPose() {
        return this.entityData.get(DATA_RIGHT_LEG_POSE);
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof Player player && !this.level().mayInteract(player, this.blockPosition());
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
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightning) {
        // NO-OP
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModRegistry.STRAW_STATUE_ITEM);
    }

    @Override
    public @Nullable <T> T get(DataComponentType<? extends T> component) {
        if (component == ModRegistry.POSE_DATA_COMPONENT_TYPE.value()) {
            return castComponentValue(component, this.getArmorStandPose());
        } else {
            return super.get(component);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        this.applyImplicitComponentIfPresent(componentGetter, ModRegistry.POSE_DATA_COMPONENT_TYPE.value());
        super.applyImplicitComponents(componentGetter);
    }

    @Override
    protected <T> boolean applyImplicitComponent(DataComponentType<T> component, T value) {
        if (component == ModRegistry.POSE_DATA_COMPONENT_TYPE.value()) {
            this.setArmorStandPose(castComponentValue(ModRegistry.POSE_DATA_COMPONENT_TYPE.value(), value));
            return true;
        } else {
            return super.applyImplicitComponent(component, value);
        }
    }

    @Override
    public List<StatueScreenType> getScreenTypes() {
        return StrawStatueScreenTypes.TYPES;
    }

    @Override
    public StatueScreenType getDefaultScreenType() {
        return StrawStatueScreenTypes.ROTATIONS;
    }

    @Override
    public Runnable setupInInventoryRendering(LivingEntity livingEntity) {
        Rotations rotations = this.getEntityPose();
        this.setEntityPose(StrawStatue.DEFAULT_ENTITY_POSE);
        return () -> this.setEntityPose(rotations);
    }
}
