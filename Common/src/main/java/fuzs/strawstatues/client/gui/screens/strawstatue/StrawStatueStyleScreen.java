package fuzs.strawstatues.client.gui.screens.strawstatue;

import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandStyleScreen;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOptions;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.stream.Stream;

public class StrawStatueStyleScreen extends ArmorStandStyleScreen {

    public StrawStatueStyleScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected ArmorStandStyleOption[] getAllTickBoxValues() {
        return Stream.of(ArmorStandStyleOptions.SHOW_NAME, ArmorStandStyleOptions.SMALL, StrawStatue.SLIM_ARMS_STYLE_OPTION, ArmorStandStyleOptions.NO_GRAVITY, ArmorStandStyleOptions.SEALED)
                .filter(option -> option.allowChanges(this.minecraft.player))
                .toArray(ArmorStandStyleOption[]::new);
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return StrawStatue.STRAW_STATUE_STYLE_SCREEN_TYPE;
    }
}
