package fuzs.strawstatues.client.gui.screens.strawstatue;

import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.network.client.C2SStrawStatueScaleMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.entity.decoration.StrawStatueData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class StrawStatuePositionScreen extends ArmorStandPositionScreen {

    public StrawStatuePositionScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<PositionScreenWidget> buildWidgets(ArmorStand armorStand) {
        List<PositionScreenWidget> widgets = super.buildWidgets(armorStand);
        widgets.add(1, new ScaleWidget(Component.translatable("armorstatues.screen.position.scale"), ((StrawStatue) armorStand)::getModelScale, scale -> StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(scale))));
        return widgets;
    }

    @Override
    protected boolean withCloseButton() {
        return false;
    }

    @Override
    protected int getWidgetRenderOffset() {
        return 0;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return StrawStatueData.STRAW_STATUE_POSITION_SCREEN_TYPE;
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
            ((StrawStatue) StrawStatuePositionScreen.this.holder.getArmorStand()).setModelScale(toModelScale(newValue));
        }
    }
}
