package fuzs.strawstatues;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesContext;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.event.v1.BuildCreativeModeTabContentsCallback;
import fuzs.statuemenus.api.v1.world.inventory.data.StatuePose;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.ServerboundStrawStatueModelPartMessage;
import fuzs.strawstatues.network.client.ServerboundStrawStatueProfileMessage;
import fuzs.strawstatues.network.client.ServerboundStrawStatueScaleMessage;
import fuzs.strawstatues.network.client.ServerboundStrawStatueSkinPatchMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.inventory.data.StrawStatueStyleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.CreativeModeTab;
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

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        BuildCreativeModeTabContentsCallback.buildCreativeModeTabContents(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(StrawStatues::onBuildCreativeModeTabContents);
        BuildCreativeModeTabContentsCallback.buildCreativeModeTabContents(CreativeModeTabs.REDSTONE_BLOCKS)
                .register(StrawStatues::onBuildCreativeModeTabContents);
    }

    private static void onBuildCreativeModeTabContents(CreativeModeTab creativeModeTab, CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(ModRegistry.STRAW_STATUE_ITEM.value());
    }

    @Override
    public void onCommonSetup() {
        StrawStatueStyleOptions.bootstrap();
        DispenserBlock.registerBehavior(ModRegistry.STRAW_STATUE_ITEM.value(), new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                ServerLevel serverLevel = blockSource.level();
                Consumer<StrawStatue> consumer = EntityType.appendDefaultStackConfig((StrawStatue strawStatue) -> {
                    strawStatue.setYRot(direction.toYRot());
                    StatuePose.randomValue().applyToEntity(strawStatue);
                }, serverLevel, itemStack, null);
                StrawStatue strawStatue = ModRegistry.STRAW_STATUE_ENTITY_TYPE.value()
                        .spawn(serverLevel, consumer, blockPos, EntitySpawnReason.DISPENSER, false, false);
                if (strawStatue != null) {
                    itemStack.shrink(1);
                }

                return itemStack;
            }
        });
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundStrawStatueModelPartMessage.class,
                ServerboundStrawStatueModelPartMessage.STREAM_CODEC);
        context.playToServer(ServerboundStrawStatueProfileMessage.class,
                ServerboundStrawStatueProfileMessage.STREAM_CODEC);
        context.playToServer(ServerboundStrawStatueScaleMessage.class, ServerboundStrawStatueScaleMessage.STREAM_CODEC);
        context.playToServer(ServerboundStrawStatueSkinPatchMessage.class,
                ServerboundStrawStatueSkinPatchMessage.STREAM_CODEC);
    }

    @Override
    public void onRegisterEntityAttributes(EntityAttributesContext context) {
        context.registerAttributes(ModRegistry.STRAW_STATUE_ENTITY_TYPE.value(), ArmorStand.createAttributes());
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
