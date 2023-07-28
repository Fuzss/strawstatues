package fuzs.strawstatues.client.gui.screens.strawstatue;

import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.puzzlesapi.api.statues.v1.network.client.data.DataSyncHandler;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandHolder;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class StrawStatuePositionScreen extends ArmorStandPositionScreen {

    public StrawStatuePositionScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(ArmorStand armorStand) {
        List<ArmorStandWidget> widgets = super.buildWidgets(armorStand);
        widgets = widgets.subList(1, widgets.size());
        widgets.add(new DoubleButtonWidget(Component.translatable(ArmorStandPositionScreen.CENTERED_TRANSLATION_KEY), Component.translatable(ArmorStandPositionScreen.CORNERED_TRANSLATION_KEY), Component.translatable(ArmorStandPositionScreen.CENTERED_DESCRIPTION_TRANSLATION_KEY), Component.translatable(ArmorStandPositionScreen.CORNERED_DESCRIPTION_TRANSLATION_KEY), Component.translatable(ArmorStandPositionScreen.ALIGNED_TRANSLATION_KEY), button -> {
            Vec3 newPosition = this.holder.getArmorStand().position().align(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.0, 0.5);
            this.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
        }, button -> {
            Vec3 newPosition = this.holder.getArmorStand().position().align(EnumSet.allOf(Direction.Axis.class));
            this.dataSyncHandler.sendPosition(newPosition.x(), newPosition.y(), newPosition.z());
        }));
        return widgets;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE;
    }
}
