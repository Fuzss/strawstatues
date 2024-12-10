package fuzs.strawstatues.client.gui.screens;

import com.google.common.collect.Lists;
import fuzs.statuemenus.api.v1.client.gui.components.NewTextureTickButton;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandPositionScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandRotationsScreen;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandWidgetsScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandPose;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.C2SStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class StrawStatueScaleScreen extends ArmorStandPositionScreen {
    public static final String SCALE_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.scale";
    public static final String ROTATION_X_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationX";
    public static final String ROTATION_Y_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationY";
    public static final String ROTATION_Z_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.position.rotationZ";

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
    protected List<ArmorStandWidgetsScreen.ArmorStandWidget> buildWidgets(ArmorStand armorStand) {
        StrawStatue strawStatue = (StrawStatue) armorStand;
        return Lists.newArrayList(new ScaleWidget(Component.translatable(SCALE_TRANSLATION_KEY),
                        strawStatue::getScale,
                        C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.SCALE)),
                new StrawRotationWidget(Component.translatable(ROTATION_X_TRANSLATION_KEY),
                        strawStatue::getEntityXRotation,
                        C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.ROTATION_X),
                        strawStatue::setEntityXRotation),
                new RotationWidget(Component.translatable(ROTATION_Y_TRANSLATION_KEY),
                        armorStand::getYRot,
                        this.dataSyncHandler::sendRotation),
                new StrawRotationWidget(Component.translatable(ROTATION_Z_TRANSLATION_KEY),
                        strawStatue::getEntityZRotation,
                        C2SStrawStatueScaleMessage.getValueSender(C2SStrawStatueScaleMessage.ScaleDataType.ROTATION_Z),
                        strawStatue::setEntityZRotation));
    }

    @Override
    protected void toggleMenuRendering(boolean disableMenuRendering) {
        super.toggleMenuRendering(disableMenuRendering);
        this.resetButton.visible = !disableMenuRendering;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.STRAW_STATUE_SCALE_SCREEN_TYPE;
    }

    private class StrawRotationWidget extends RotationWidget {
        private final Consumer<Float> newClientValue;

        public StrawRotationWidget(Component title, DoubleSupplier currentValue, Consumer<Float> newValue, Consumer<Float> newClientValue) {
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

    private class ScaleWidget extends RotationWidget {
        static final double LOGARITHMIC_SCALE = 2.0;
        static final double LOGARITHMIC_SCALE_POW = Math.pow(10.0, -LOGARITHMIC_SCALE);

        public ScaleWidget(Component title, DoubleSupplier currentValue, Consumer<Float> newValue) {
            super(title, currentValue, newValue, -1.0);
        }

        @Override
        protected double getCurrentValue() {
            double value = Mth.inverseLerp(this.currentValue.getAsDouble(), StrawStatue.MIN_SCALE, StrawStatue.MAX_SCALE);
            return (Math.log10(value + LOGARITHMIC_SCALE_POW) + LOGARITHMIC_SCALE) / LOGARITHMIC_SCALE;
        }

        @Override
        protected void setNewValue(double newValue) {
            this.newValue.accept(getScaledValue(newValue));
        }

        @Override
        protected Component getTooltipComponent(double mouseValue) {
            mouseValue = getScaledValue(mouseValue);
            mouseValue = (int) (mouseValue * 100.0F) / 100.0F;
            mouseValue = Mth.clamp(mouseValue, StrawStatue.MIN_SCALE, StrawStatue.MAX_SCALE);
            return Component.literal(ArmorStandPose.ROTATION_FORMAT.format(mouseValue));
        }

        @Override
        protected void applyClientValue(double newValue) {
            ((StrawStatue) StrawStatueScaleScreen.this.holder.getArmorStand()).setScale(getScaledValue(newValue));
        }

        public static float getScaledValue(double value) {
            value = Math.pow(10.0, value * LOGARITHMIC_SCALE - LOGARITHMIC_SCALE) -
                    LOGARITHMIC_SCALE_POW;
            return (float) Mth.lerp(value, StrawStatue.MIN_SCALE, StrawStatue.MAX_SCALE);
        }
    }
}
