package fuzs.strawstatues.client.gui.screens.strawstatue;

import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandPositionScreen;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.world.entity.decoration.StrawStatueData;
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
        return widgets.subList(1, widgets.size());
    }

    @Override
    protected int getWidgetRenderOffset() {
        return 7;
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return StrawStatueData.STRAW_STATUE_POSITION_SCREEN_TYPE;
    }
}
