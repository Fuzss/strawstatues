package fuzs.strawstatues.client.gui.screens;

import fuzs.statuemenus.api.v1.client.gui.screens.StatuePositionScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.StatueHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class StrawStatuePositionScreen extends StatuePositionScreen {
    protected static final ArmorStandWidgetFactory<StrawStatuePositionScreen> ALIGNMENTS_WIDGET_FACTORY = (StrawStatuePositionScreen screen, LivingEntity livingEntity) -> {
        return screen.new DoubleButtonWidget(Component.translatable(StatuePositionScreen.CENTERED_TRANSLATION_KEY),
                Component.translatable(StatuePositionScreen.CORNERED_TRANSLATION_KEY),
                Component.translatable(StatuePositionScreen.CENTERED_DESCRIPTION_TRANSLATION_KEY),
                Component.translatable(StatuePositionScreen.CORNERED_DESCRIPTION_TRANSLATION_KEY),
                Component.translatable(StatuePositionScreen.ALIGNED_TRANSLATION_KEY),
                (Button button) -> {
                    Vec3 newPosition = livingEntity.position()
                            .align(EnumSet.allOf(Direction.Axis.class))
                            .add(0.5, 0.0, 0.5);
                    screen.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
                },
                (Button button) -> {
                    Vec3 newPosition = livingEntity.position().align(EnumSet.allOf(Direction.Axis.class));
                    screen.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
                });
    };

    public StrawStatuePositionScreen(StatueHolder statueHolder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(statueHolder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(LivingEntity livingEntity) {
        return buildWidgets(this,
                livingEntity,
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
    public StatueScreenType getScreenType() {
        return StrawStatueScreenTypes.POSITION;
    }
}
