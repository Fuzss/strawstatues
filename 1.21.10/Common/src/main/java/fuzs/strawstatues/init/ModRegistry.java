package fuzs.strawstatues.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.core.Proxy;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.item.StrawStatueItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(StrawStatues.MOD_ID);
    public static final Holder.Reference<DataComponentType<ArmorStand.ArmorStandPose>> POSE_DATA_COMPONENT_TYPE = REGISTRY.registerDataComponentType(
            "pose",
            (DataComponentType.Builder<ArmorStand.ArmorStandPose> builder) -> {
                return builder.persistent(ArmorStand.ArmorStandPose.CODEC)
                        .networkSynchronized(StrawStatue.POSE_STREAM_CODEC);
            });
    public static final Holder.Reference<Item> STRAW_STATUE_ITEM = REGISTRY.registerItem("straw_statue",
            StrawStatueItem::new,
            () -> new Item.Properties().stacksTo(16)
                    .component(DataComponents.TOOLTIP_DISPLAY,
                            TooltipDisplay.DEFAULT.withHidden(DataComponents.PROFILE, true)));
    public static final Holder.Reference<EntityType<StrawStatue>> STRAW_STATUE_ENTITY_TYPE = REGISTRY.registerEntityType(
            "straw_statue",
            () -> {
                return EntityType.Builder.of(Proxy.INSTANCE::createStrawStatue, MobCategory.MISC)
                        .sized(0.6F, 1.8F)
                        .eyeHeight(1.62F)
                        .vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT)
                        .clientTrackingRange(32)
                        .updateInterval(2);
            });
    public static final Holder.Reference<MenuType<StatueMenu>> STRAW_STATUE_MENU_TYPE = REGISTRY.registerMenuType(
            "straw_statue",
            (int containerId, Inventory inventory, StatueMenu.Data data) -> {
                return new <StrawStatue>StatueMenu(ModRegistry.STRAW_STATUE_MENU_TYPE.value(),
                        containerId,
                        inventory,
                        data,
                        (StrawStatue strawStatue) -> strawStatue);
            },
            StatueMenu.Data.STREAM_CODEC);

    public static void bootstrap() {
        // NO-OP
    }
}
