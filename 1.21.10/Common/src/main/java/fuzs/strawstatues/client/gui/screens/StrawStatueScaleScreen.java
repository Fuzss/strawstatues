package fuzs.strawstatues.client.gui.screens;

import fuzs.statuemenus.api.v1.client.gui.components.FlatTickButton;
import fuzs.statuemenus.api.v1.client.gui.screens.StatuePositionScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.StatueRotationsScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.StatueHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.strawstatues.network.client.ServerboundStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class StrawStatueScaleScreen extends StatuePositionScreen {
    public static final Component ROTATION_X_TRANSLATION_KEY = Component.translatable(StrawStatueScreenTypes.SCALE.id()
            .toLanguageKey("screen", "rotation_x"));
    public static final Component ROTATION_Y_TRANSLATION_KEY = Component.translatable(StrawStatueScreenTypes.SCALE.id()
            .toLanguageKey("screen", "rotation_y"));
    public static final Component ROTATION_Z_TRANSLATION_KEY = Component.translatable(StrawStatueScreenTypes.SCALE.id()
            .toLanguageKey("screen", "rotation_z"));
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_X_WIDGET_FACTORY = (StrawStatueScaleScreen screen, LivingEntity livingEntity) -> {
        return screen.new StrawStatueRotationWidget(ROTATION_X_TRANSLATION_KEY,
                () -> ((StrawStatue) livingEntity).getEntityPose().x(),
                ServerboundStrawStatueScaleMessage.getValueSender(ServerboundStrawStatueScaleMessage.DataType.ROTATION_X),
                ((StrawStatue) livingEntity)::setPoseX);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_Y_WIDGET_FACTORY = (StrawStatueScaleScreen screen, LivingEntity livingEntity) -> {
        return screen.new RotationWidget(ROTATION_Y_TRANSLATION_KEY,
                livingEntity::getYRot,
                screen.dataSyncHandler::sendRotation);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_Z_WIDGET_FACTORY = (StrawStatueScaleScreen screen, LivingEntity livingEntity) -> {
        return screen.new StrawStatueRotationWidget(ROTATION_Z_TRANSLATION_KEY,
                () -> ((StrawStatue) livingEntity).getEntityPose().z(),
                ServerboundStrawStatueScaleMessage.getValueSender(ServerboundStrawStatueScaleMessage.DataType.ROTATION_Z),
                ((StrawStatue) livingEntity)::setPoseZ);
    };

    private AbstractWidget resetButton;

    public StrawStatueScaleScreen(StatueHolder statueHolder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(statueHolder, inventory, component, dataSyncHandler);
    }

    @Override
    protected void init() {
        super.init();
        this.resetButton = Util.make(this.addRenderableWidget(new FlatTickButton(this.leftPos + 6,
                this.topPos + 6,
                20,
                20,
                240,
                124,
                getArmorStandWidgetsLocation(),
                (Button button) -> {
                    ServerboundStrawStatueScaleMessage.DataType.RESET.consumer.accept((StrawStatue) StrawStatueScaleScreen.this.holder.getEntity(),
                            -1.0F);
                    ServerboundStrawStatueScaleMessage.getValueSender(ServerboundStrawStatueScaleMessage.DataType.RESET)
                            .accept(-1.0F);
                    this.widgets.forEach(ArmorStandWidget::reset);
                })), widget -> {
            widget.setTooltip(Tooltip.create(Component.translatable(StatueRotationsScreen.RESET_TRANSLATION_KEY)));
        });
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(LivingEntity livingEntity) {
        return buildWidgets(this,
                livingEntity,
                List.of(SCALE_WIDGET_FACTORY,
                        ROTATION_X_WIDGET_FACTORY,
                        ROTATION_Y_WIDGET_FACTORY,
                        ROTATION_Z_WIDGET_FACTORY));
    }

    @Override
    protected void toggleMenuRendering(boolean disableMenuRendering) {
        super.toggleMenuRendering(disableMenuRendering);
        this.resetButton.visible = !disableMenuRendering;
    }

    @Override
    protected boolean withCloseButton() {
        return true;
    }

    @Override
    public StatueScreenType getScreenType() {
        return StrawStatueScreenTypes.SCALE;
    }

    protected class StrawStatueRotationWidget extends RotationWidget {
        private final Consumer<Float> newClientValue;

        public StrawStatueRotationWidget(Component title, DoubleSupplier currentValue, Consumer<Float> newValue, Consumer<Float> newClientValue) {
            super(title, currentValue, newValue);
            this.newClientValue = newClientValue;
        }

        @Override
        protected double getCurrentValue() {
            return this.currentValue.getAsDouble() / 360.0;
        }

        @Override
        protected void setNewValue(double newValue) {
            this.newValue.accept((float) (newValue * 360.0));
        }

        @Override
        protected void applyClientValue(double newValue) {
            this.newClientValue.accept((float) (newValue * 360.0));
        }
    }
}
