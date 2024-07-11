package fuzs.strawstatues;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandPose;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandStyleOption;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.C2SStrawStatueModelPartMessage;
import fuzs.strawstatues.network.client.C2SStrawStatueOwnerMessage;
import fuzs.strawstatues.network.client.C2SStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class StrawStatues implements ModConstructor {
    public static final String MOD_ID = "strawstatues";
    public static final String MOD_NAME = "Straw Statues";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerLegacyServerbound(C2SStrawStatueModelPartMessage.class, C2SStrawStatueModelPartMessage::new)
            .registerLegacyServerbound(C2SStrawStatueOwnerMessage.class, C2SStrawStatueOwnerMessage::new)
            .registerLegacyServerbound(C2SStrawStatueScaleMessage.class, C2SStrawStatueScaleMessage::new);
    ;

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        // high priority so we run before the Quark mod
        PlayerInteractEvents.USE_ENTITY_AT.register(EventPhase.BEFORE, StrawStatue::onUseEntityAt);
    }

    @Override
    public void onCommonSetup() {
        ArmorStandStyleOption.register(id("slimarms"), ModRegistry.SLIM_ARMS_STYLE_OPTION);
        ArmorStandStyleOption.register(id("crouching"), ModRegistry.CROUCHING_STYLE_OPTION);
        DispenserBlock.registerBehavior(ModRegistry.STRAW_STATUE_ITEM.value(), new DefaultDispenseItemBehavior() {

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                ServerLevel serverLevel = blockSource.level();
                Consumer<StrawStatue> consumer = EntityType.appendDefaultStackConfig((StrawStatue strawStatue) -> {
                    strawStatue.setYRot(direction.toYRot());
                    ArmorStandPose.randomValue().applyToEntity(strawStatue);
                }, serverLevel, itemStack, null);
                StrawStatue strawStatue = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                        .spawn(serverLevel, consumer, blockPos, MobSpawnType.DISPENSER, false, false);
                if (strawStatue != null) {
                    itemStack.shrink(1);
                }

                return itemStack;
            }
        });
    }

    @Override
    public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        context.registerEntityAttributes(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), ArmorStand.createAttributes());
    }

    @Override
    public void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsContext context) {
        context.registerBuildListener(CreativeModeTabs.FUNCTIONAL_BLOCKS, (itemDisplayParameters, output) -> {
            output.accept(ModRegistry.STRAW_STATUE_ITEM.value());
        });
        context.registerBuildListener(CreativeModeTabs.REDSTONE_BLOCKS, (itemDisplayParameters, output) -> {
            output.accept(ModRegistry.STRAW_STATUE_ITEM.value());
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
