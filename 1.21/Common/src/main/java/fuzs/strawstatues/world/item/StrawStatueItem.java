package fuzs.strawstatues.world.item;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.statuemenus.api.v1.helper.ArmorStandInteractHelper;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class StrawStatueItem extends Item {

    public StrawStatueItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level level = context.getLevel();
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(context);
            BlockPos blockPos = blockPlaceContext.getClickedPos();
            ItemStack itemStack = context.getItemInHand();
            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            AABB aABB = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                    .getDimensions()
                    .makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aABB) && level.getEntities(null, aABB).isEmpty()) {
                if (level instanceof ServerLevel serverLevel) {
                    Player player = context.getPlayer();
                    Consumer<StrawStatue> consumer = EntityType.appendDefaultStackConfig((StrawStatue strawStatue) -> {
                        strawStatue.setOwner(itemStack.get(DataComponents.PROFILE));
                        if (itemStack.has(DataComponents.CUSTOM_NAME)) {
                            strawStatue.setCustomNameVisible(true);
                        }
                    }, serverLevel, itemStack, player);
                    ArmorStand armorStand = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                            .create(serverLevel, consumer, blockPos, MobSpawnType.SPAWN_EGG, true, true);
                    if (armorStand == null) {
                        return InteractionResult.FAIL;
                    }
                    float yRot = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) *
                            45.0F;
                    armorStand.moveTo(armorStand.getX(), armorStand.getY(), armorStand.getZ(), yRot, 0.0F);
                    this.randomizePose(armorStand, level.random);
                    serverLevel.addFreshEntityWithPassengers(armorStand);
                    level.playSound(null,
                            armorStand.getX(),
                            armorStand.getY(),
                            armorStand.getZ(),
                            SoundEvents.ARMOR_STAND_PLACE,
                            SoundSource.BLOCKS,
                            0.75F,
                            0.8F
                    );
                    armorStand.gameEvent(GameEvent.ENTITY_PLACE, player);
                    if (player != null && !player.isShiftKeyDown()) {
                        ArmorStandPose.randomValue().applyToEntity(armorStand);
                    }
                }
                itemStack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    private void randomizePose(ArmorStand armorStand, RandomSource random) {
        Rotations rotations = armorStand.getHeadPose();
        float f = random.nextFloat() * 5.0F;
        float g = random.nextFloat() * 20.0F - 10.0F;
        Rotations rotations2 = new Rotations(rotations.getX() + f, rotations.getY() + g, rotations.getZ());
        armorStand.setHeadPose(rotations2);
        rotations = armorStand.getBodyPose();
        f = random.nextFloat() * 10.0F - 5.0F;
        rotations2 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
        armorStand.setBodyPose(rotations2);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.addAll(Proxy.INSTANCE.splitTooltipLines(ArmorStandInteractHelper.getArmorStandHoverText()));
    }

    @Override
    public Component getName(ItemStack stack) {
        ResolvableProfile resolvableProfile = stack.get(DataComponents.PROFILE);
        return resolvableProfile != null && resolvableProfile.name().isPresent() ?
                Component.translatable(this.getDescriptionId() + ".named", resolvableProfile.name().get()) :
                super.getName(stack);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack itemStack) {
        ResolvableProfile resolvableProfile = itemStack.get(DataComponents.PROFILE);
        if (resolvableProfile != null && !resolvableProfile.isResolved()) {
            resolvableProfile.resolve()
                    .thenAcceptAsync(newResolvableProfile -> itemStack.set(DataComponents.PROFILE,
                            newResolvableProfile
                    ), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
        }
    }
}
