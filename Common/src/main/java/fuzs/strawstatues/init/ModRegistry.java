package fuzs.strawstatues.init;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import fuzs.strawstatues.api.world.inventory.data.PosePartMutator;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.item.StrawStatueItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class ModRegistry {
    static final RegistryManager REGISTRY = CoreServices.FACTORIES.registration(StrawStatues.MOD_ID);
    public static final RegistryReference<Item> STRAW_STATUE_ITEM = REGISTRY.registerItem("straw_statue", () -> new StrawStatueItem(new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS)));
    public static final RegistryReference<EntityType<StrawStatue>> STRAW_STATUE_ENTITY_TYPE = REGISTRY.registerEntityTypeBuilder("straw_statue", () -> {
        return EntityType.Builder.of((EntityType<StrawStatue> entityType, Level level) -> new StrawStatue(entityType, level), MobCategory.MISC).sized(0.6F, 1.8F).clientTrackingRange(10);
    });
    public static final RegistryReference<MenuType<ArmorStandMenu>> STRAW_STATUE_MENU_TYPE = REGISTRY.registerExtendedMenuTypeSupplier("straw_statue", () -> (containerId, inventory, data) -> {
        return ArmorStandMenu.create(ModRegistry.STRAW_STATUE_MENU_TYPE.get(), containerId, inventory, data, null);
    });

    public static final ArmorStandScreenType MODEL_PARTS_SCREEN_TYPE = new ArmorStandScreenType("modelParts", new ItemStack(Items.YELLOW_WOOL));
    public static final ArmorStandScreenType STRAW_STATUE_STYLE_SCREEN_TYPE = new ArmorStandScreenType("style", new ItemStack(Items.PAINTING));
    public static final ArmorStandScreenType STRAW_STATUE_POSITION_SCREEN_TYPE = new ArmorStandScreenType("position", new ItemStack(Items.GRASS_BLOCK));
    public static final ArmorStandScreenType STRAW_STATUE_SCALE_SCREEN_TYPE = new ArmorStandScreenType("scale", new ItemStack(Items.HAY_BLOCK));
    public static final ArmorStandStyleOption SLIM_ARMS_STYLE_OPTION = new ArmorStandStyleOption() {

        @Override
        public String getName() {
            return "slimArms";
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

        @Override
        public String getName() {
            return "crouching";
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
    public static final PosePartMutator CAPE_POSE_PART_MUTATOR = new PosePartMutator("cape", ArmorStandPose::getBodyPose, ArmorStandPose::withBodyPose, PosePartMutator.PosePartAxisRange.range(0.0F, 120.0F), PosePartMutator.PosePartAxisRange.range(-60.0F, 60.0F), PosePartMutator.PosePartAxisRange.range(-120.0, 120.0));
    public static final EntityDataSerializer<Optional<GameProfile>> GAME_PROFILE_ENTITY_DATA_SERIALIZER = EntityDataSerializer.optional((friendlyByteBuf, gameProfile) -> {
        if (!gameProfile.isComplete()) {
            UUID uuid = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
            gameProfile = new GameProfile(uuid, gameProfile.getName());
        }
        friendlyByteBuf.writeGameProfile(gameProfile);
    }, FriendlyByteBuf::readGameProfile);

    public static void touch() {
        if (!ModLoaderEnvironment.INSTANCE.getModLoader().isForge()) {
            EntityDataSerializers.registerSerializer(GAME_PROFILE_ENTITY_DATA_SERIALIZER);
        }
    }
}
