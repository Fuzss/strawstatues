package fuzs.strawstatues.api.client.gui.screens.armorstand;

import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;

public interface ArmorStandScreen {

    ArmorStandHolder getHolder();

    ArmorStandScreenType getScreenType();

    <T extends Screen & MenuAccess<ArmorStandMenu> & ArmorStandScreen> T createScreenType(ArmorStandScreenType screenType);

    void setMouseX(int mouseX);

    void setMouseY(int mouseY);

    DataSyncHandler getDataSyncHandler();

    default void renderArmorStandInInventory(int posX, int posY, int scale, float mouseX, float mouseY) {
        AbstractArmorStandScreen.armorStandRenderer.renderEntityInInventory(posX, posY, scale, mouseX, mouseY, this.getHolder().getArmorStand());
    }
}
