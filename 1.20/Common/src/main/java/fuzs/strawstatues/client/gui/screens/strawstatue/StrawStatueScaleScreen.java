package fuzs.strawstatues.client.gui.screens.strawstatue;

import com.google.common.collect.Lists;
import fuzs.puzzlesapi.api.client.statues.v1.gui.components.NewTextureTickButton;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandRotationsScreen;
import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandWidgetsScreen;
import fuzs.puzzlesapi.api.statues.v1.network.client.data.DataSyncHandler;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandHolder;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.data.ArmorStandPose;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.data.ArmorStandScreenType;
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
        this.resetButton = Util.make(this.addRenderableWidget(new NewTextureTickButton(this.leftPos + 6, this.topPos + 6, 20, 20, 240, 124, getArmorStandWidgetsLocation(), button -> {
            C2SStrawStatueScaleMessage.ScaleDataType.RESET.consumer.accept((StrawStatue) StrawStatueScaleScreen.this.holder.getArmorStand(), -1.0F);
            C2SStrawStatueScaleMessage.sendReset();
            this.widgets.forEach(PositionScreenWidget::reset);
        })), widget -> {
            widget.setTooltip(Tooltip.create(Component.translatable(ArmorStandRotationsScreen.RESET_TRANSLATION_KEY)));
        });
    }

    @Override
    protected List<ArmorStandWidgetsScreen.PositionScreenWidget> buildWidgets(ArmorStand armorStand) {
        StrawStatue strawStatue = (StrawStatue) armorStand;
        return Lists.newArrayList(
                new ScaleWidget(Component.translatable(SCALE_TRANSLATION_KEY), strawStatue::getEntityScale, C2SStrawStatueScaleMessage::sendScale),
                new StrawRotationWidget(Component.translatable(ROTATION_X_TRANSLATION_KEY), strawStatue::getEntityXRotation, C2SStrawStatueScaleMessage::sendRotationX, strawStatue::setEntityXRotation),
                new RotationWidget(Component.translatable(ROTATION_Y_TRANSLATION_KEY), armorStand::getYRot, this.dataSyncHandler::sendRotation),
                new StrawRotationWidget(Component.translatable(ROTATION_Z_TRANSLATION_KEY), strawStatue::getEntityZRotation, C2SStrawStatueScaleMessage::sendRotationZ, strawStatue::setEntityZRotation)
        );
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

    public static float toModelScale(double newValue) {
        newValue = newValue * (StrawStatue.MAX_MODEL_SCALE - StrawStatue.MIN_MODEL_SCALE) + StrawStatue.MIN_MODEL_SCALE;
        return StrawStatue.clampModelScale(newValue);
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

        public ScaleWidget(Component title, DoubleSupplier currentValue, Consumer<Float> newValue) {
            super(title, currentValue, newValue, -1.0);
        }

        @Override
        protected double getCurrentValue() {
            return (this.currentValue.getAsDouble() - StrawStatue.MIN_MODEL_SCALE) / (StrawStatue.MAX_MODEL_SCALE - StrawStatue.MIN_MODEL_SCALE);
        }

        @Override
        protected void setNewValue(double newValue) {
            this.newValue.accept(toModelScale(newValue));
        }

        @Override
        protected Component getTooltipComponent(double mouseValue) {
            mouseValue = mouseValue * 9.0 + 1.0;
            mouseValue = Mth.clamp(mouseValue, 1.0, 10.0);
            return Component.literal(ArmorStandPose.ROTATION_FORMAT.format(mouseValue));
        }

        @Override
        protected void applyClientValue(double newValue) {
            ((StrawStatue) StrawStatueScaleScreen.this.holder.getArmorStand()).setEntityScale(toModelScale(newValue));
        }
    }
}
