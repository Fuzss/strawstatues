package fuzs.strawstatues.client.gui.screens;

import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandPositionScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class StrawStatuePositionScreen extends ArmorStandPositionScreen {
    protected static final ArmorStandWidgetFactory<StrawStatuePositionScreen> ALIGNMENTS_WIDGET_FACTORY = (StrawStatuePositionScreen screen, ArmorStand armorStand) -> {
        return screen.new DoubleButtonWidget(Component.translatable(ArmorStandPositionScreen.CENTERED_TRANSLATION_KEY),
                Component.translatable(ArmorStandPositionScreen.CORNERED_TRANSLATION_KEY),
                Component.translatable(ArmorStandPositionScreen.CENTERED_DESCRIPTION_TRANSLATION_KEY),
                Component.translatable(ArmorStandPositionScreen.CORNERED_DESCRIPTION_TRANSLATION_KEY),
                Component.translatable(ArmorStandPositionScreen.ALIGNED_TRANSLATION_KEY),
                button -> {
                    Vec3 newPosition = armorStand.position()
                            .align(EnumSet.allOf(Direction.Axis.class))
                            .add(0.5, 0.0, 0.5);
                    screen.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
                },
                button -> {
                    Vec3 newPosition = armorStand.position().align(EnumSet.allOf(Direction.Axis.class));
                    screen.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
                });
    };

    public StrawStatuePositionScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(ArmorStand armorStand) {
        return buildWidgets(this,
                armorStand,
                List.of(POSITION_INCREMENT_WIDGET_FACTORY,
                        POSITION_X_WIDGET_FACTORY,
                        POSITION_Y_WIDGET_FACTORY,
                        POSITION_Z_WIDGET_FACTORY,
                        ALIGNMENTS_WIDGET_FACTORY));
    }

    @Override
    protected boolean withCloseButton() {
        return true;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE;
    }
}
