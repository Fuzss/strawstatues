package fuzs.strawstatues.client.gui.screens.strawstatue;

import fuzs.puzzlesapi.api.client.statues.v1.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.puzzlesapi.api.statues.v1.network.client.data.DataSyncHandler;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandHolder;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class StrawStatuePositionScreen extends ArmorStandPositionScreen {

    public StrawStatuePositionScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<PositionScreenWidget> buildWidgets(ArmorStand armorStand) {
        List<PositionScreenWidget> widgets = super.buildWidgets(armorStand);
        widgets = widgets.subList(1, widgets.size());
        widgets.add(new PositionAlignWidget());
        return widgets;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.STRAW_STATUE_POSITION_SCREEN_TYPE;
    }
}
