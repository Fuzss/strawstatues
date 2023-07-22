package fuzs.strawstatues.client.gui.screens.strawstatue;

import com.google.common.collect.Lists;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandWidgetsScreen;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.networking.client.C2SStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.entity.decoration.StrawStatueData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class StrawStatueScaleScreen extends ArmorStandPositionScreen {

    public StrawStatueScaleScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<ArmorStandWidgetsScreen.PositionScreenWidget> buildWidgets(ArmorStand armorStand) {
        // only move server-side to prevent rubber banding
        return Lists.newArrayList(
                new ScaleWidget(Component.translatable("armorstatues.screen.position.scale"), ((StrawStatue) armorStand)::getModelScale, scale -> StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(scale))),
                new RotationWidget(Component.translatable("armorstatues.screen.position.rotation"), armorStand::getYRot, this.dataSyncHandler::sendRotation, ArmorStandPose.DEGREES_SNAP_INTERVAL),
                new RotationWidget(Component.translatable("armorstatues.screen.position.rotation"), armorStand::getYRot, this.dataSyncHandler::sendRotation, ArmorStandPose.DEGREES_SNAP_INTERVAL),
                new RotationWidget(Component.translatable("armorstatues.screen.position.rotation"), armorStand::getYRot, this.dataSyncHandler::sendRotation, ArmorStandPose.DEGREES_SNAP_INTERVAL)
        );
    }

    @Override
    protected int getWidgetRenderOffset() {
        return 7;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return StrawStatueData.STRAW_STATUE_SCALE_SCREEN_TYPE;
    }

    public static float toModelScale(double newValue) {
        newValue = newValue * (StrawStatue.MAX_MODEL_SCALE - StrawStatue.MIN_MODEL_SCALE) + StrawStatue.MIN_MODEL_SCALE;
        return StrawStatue.fixModelScale(newValue);
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
            ((StrawStatue) StrawStatueScaleScreen.this.holder.getArmorStand()).setModelScale(toModelScale(newValue));
        }
    }
}
