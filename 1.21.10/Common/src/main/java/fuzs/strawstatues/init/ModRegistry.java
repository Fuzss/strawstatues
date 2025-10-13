package fuzs.strawstatues.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandPose;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandStyleOption;
import fuzs.statuemenus.api.v1.world.inventory.data.PosePartMutator;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.item.StrawStatueItem;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(StrawStatues.MOD_ID);
    public static final Holder.Reference<Item> STRAW_STATUE_ITEM = REGISTRY.registerItem("straw_statue",
            StrawStatueItem::new,
            () -> new Item.Properties().stacksTo(16));
    public static final Holder.Reference<EntityType<StrawStatue>> STRAW_STATUE_ENTITY_TYPE = REGISTRY.registerEntityType(
            "straw_statue",
            () -> {
                return EntityType.Builder.of((EntityType<StrawStatue> entityType, Level level) -> new StrawStatue(
                                entityType,
                                level), MobCategory.MISC)
                        .sized(0.6F, 1.8F)
                        .eyeHeight(1.62F)
                        .vehicleAttachment(Player.DEFAULT_VEHICLE_ATTACHMENT)
                        .clientTrackingRange(10);
            });
    public static final Holder.Reference<MenuType<ArmorStandMenu>> STRAW_STATUE_MENU_TYPE = REGISTRY.registerMenuType(
            "straw_statue",
            (int containerId, Inventory inventory, ArmorStandMenu.ArmorStandData data) -> {
                return new ArmorStandMenu(ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                        containerId,
                        inventory,
                        data,
                        null);
            },
            ArmorStandMenu.ArmorStandData.STREAM_CODEC);
    public static final Holder.Reference<EntityDataSerializer<Optional<ResolvableProfile>>> RESOLVABLE_PROFILE_ENTITY_DATA_SERIALIZER = REGISTRY.registerEntityDataSerializer(
            "resolvable_profile",
            () -> EntityDataSerializer.forValueType(ResolvableProfile.STREAM_CODEC.apply(ByteBufCodecs::optional)));

    public static final ArmorStandScreenType MODEL_PARTS_SCREEN_TYPE = new ArmorStandScreenType(StrawStatues.id(
            "model_parts"), new ItemStack(Items.YELLOW_WOOL));
    public static final ArmorStandScreenType STRAW_STATUE_POSITION_SCREEN_TYPE = new ArmorStandScreenType(StrawStatues.id(
            "position"), new ItemStack(Items.GRASS_BLOCK));
    public static final ArmorStandScreenType STRAW_STATUE_SCALE_SCREEN_TYPE = new ArmorStandScreenType(StrawStatues.id(
            "scale"), new ItemStack(Items.HAY_BLOCK));
    public static final ArmorStandStyleOption SLIM_ARMS_STYLE_OPTION = new ArmorStandStyleOption() {
        private final ResourceLocation name = StrawStatues.id("slim_arms");

        @Override
        public ResourceLocation getName() {
            return this.name;
        }

        @Override
        public void setOption(ArmorStand armorStand, boolean setting) {
            ((StrawStatue) armorStand).setSlimArms(setting);
        }

        @Override
        public boolean getOption(ArmorStand armorStand) {
            return ((StrawStatue) armorStand).slimArms();
        }

        @Override
        public void toTag(CompoundTag tag, boolean currentValue) {
            tag.putBoolean(StrawStatue.SLIM_ARMS_KEY, currentValue);
        }
    };
    public static final ArmorStandStyleOption CROUCHING_STYLE_OPTION = new ArmorStandStyleOption() {
        private final ResourceLocation name = StrawStatues.id("crouching");

        @Override
        public ResourceLocation getName() {
            return this.name;
        }

        @Override
        public void setOption(ArmorStand armorStand, boolean setting) {
            ((StrawStatue) armorStand).setCrouching(setting);
        }

        @Override
        public boolean getOption(ArmorStand armorStand) {
            return armorStand.isCrouching();
        }

        @Override
        public void toTag(CompoundTag tag, boolean currentValue) {
            tag.putBoolean(StrawStatue.CROUCHING_KEY, currentValue);
        }
    };
    public static final PosePartMutator CAPE_POSE_PART_MUTATOR = new PosePartMutator("cape",
            ArmorStandPose::getBodyPose,
            ArmorStandPose::withBodyPose,
            PosePartMutator.PosePartAxisRange.range(0.0F, 120.0F),
            PosePartMutator.PosePartAxisRange.range(-60.0F, 60.0F),
            PosePartMutator.PosePartAxisRange.range(-120.0, 120.0));

    public static void bootstrap() {
        // NO-OP
    }
}
