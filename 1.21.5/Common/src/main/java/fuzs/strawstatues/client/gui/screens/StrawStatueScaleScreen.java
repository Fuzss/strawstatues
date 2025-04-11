package fuzs.strawstatues.client.gui.screens;

import fuzs.statuemenus.api.v1.client.gui.components.NewTextureTickButton;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandPositionScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandRotationsScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.C2SStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class StrawStatueScaleScreen extends ArmorStandPositionScreen {
    public static final String ROTATION_X_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationX";
    public static final String ROTATION_Y_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationY";
    public static final String ROTATION_Z_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationZ";
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_X_WIDGET_FACTORY = (StrawStatueScaleScreen screen, ArmorStand armorStand) -> {
        return screen.new StrawStatueRotationWidget(Component.translatable(ROTATION_X_TRANSLATION_KEY),
                ((StrawStatue) armorStand)::getEntityXRotation,
                C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.ROTATION_X),
                ((StrawStatue) armorStand)::setEntityXRotation);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_Y_WIDGET_FACTORY = (StrawStatueScaleScreen screen, ArmorStand armorStand) -> {
        return screen.new RotationWidget(Component.translatable(ROTATION_Y_TRANSLATION_KEY),
                armorStand::getYRot,
                screen.dataSyncHandler::sendRotation);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueScaleScreen> ROTATION_Z_WIDGET_FACTORY = (StrawStatueScaleScreen screen, ArmorStand armorStand) -> {
        return screen.new StrawStatueRotationWidget(Component.translatable(ROTATION_Z_TRANSLATION_KEY),
                ((StrawStatue) armorStand)::getEntityZRotation,
                C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.ROTATION_Z),
                ((StrawStatue) armorStand)::setEntityZRotation);
    };

    private AbstractWidget resetButton;

    public StrawStatueScaleScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected void init() {
        super.init();
        this.resetButton = Util.make(this.addRenderableWidget(new NewTextureTickButton(this.leftPos + 6,
                this.topPos + 6,
                20,
                20,
                240,
                124,
                getArmorStandWidgetsLocation(),
                button -> {
                    C2SStrawStatueScaleMessage.ScaleDataType.RESET.consumer.accept((StrawStatue) StrawStatueScaleScreen.this.holder.getArmorStand(),
                            -1.0F);
                    C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.RESET)
                            .accept(-1.0F);
                    this.widgets.forEach(ArmorStandWidget::reset);
                })), widget -> {
            widget.setTooltip(Tooltip.create(Component.translatable(ArmorStandRotationsScreen.RESET_TRANSLATION_KEY)));
        });
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(ArmorStand armorStand) {
        return buildWidgets(this,
                armorStand,
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
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE;
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
