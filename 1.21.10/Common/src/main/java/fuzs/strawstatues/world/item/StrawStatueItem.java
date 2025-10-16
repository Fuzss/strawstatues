package fuzs.strawstatues.world.item;

import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import fuzs.statuemenus.api.v1.world.inventory.data.StatuePose;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
            ItemStack itemInHand = context.getItemInHand();
            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            AABB aABB = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                    .getDimensions()
                    .makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aABB) && level.getEntities(null, aABB).isEmpty()) {
                if (level instanceof ServerLevel serverLevel) {
                    // do not use EntityType::spawn which adds the entity directly, as it will not send the initial rotations correctly
                    Player player = context.getPlayer();
                    Consumer<StrawStatue> consumer = EntityType.createDefaultStackConfig(serverLevel,
                            itemInHand,
                            context.getPlayer());
                    StrawStatue strawStatue = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                            .create(serverLevel, consumer, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
                    if (strawStatue != null) {
                        float yRot =
                                Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                        strawStatue.snapTo(strawStatue.getX(), strawStatue.getY(), strawStatue.getZ(), yRot, 0.0F);
                        serverLevel.addFreshEntityWithPassengers(strawStatue);
                        level.playSound(null,
                                strawStatue.getX(),
                                strawStatue.getY(),
                                strawStatue.getZ(),
                                SoundEvents.GRASS_PLACE,
                                SoundSource.BLOCKS,
                                0.75F,
                                0.8F);
                        strawStatue.gameEvent(GameEvent.ENTITY_PLACE, player);
                        if (player != null && !player.isShiftKeyDown()) {
                            StatuePose.randomValue().applyToEntity(strawStatue);
                        }
                    } else {
                        return InteractionResult.FAIL;
                    }
                }

                itemInHand.shrink(1);
                return InteractionResultHelper.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    /**
     * @see net.minecraft.world.item.PlayerHeadItem#getName(ItemStack)
     */
    @Override
    public Component getName(ItemStack itemStack) {
        ResolvableProfile resolvableProfile = itemStack.get(DataComponents.PROFILE);
        return resolvableProfile != null && resolvableProfile.name().isPresent() ?
                Component.translatable(this.getDescriptionId() + ".named", resolvableProfile.name().get()) :
                super.getName(itemStack);
    }
}
