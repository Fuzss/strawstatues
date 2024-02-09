package fuzs.strawstatues.client.gui.screens.strawstatue;

import com.mojang.authlib.GameProfile;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.client.gui.components.TickBoxButton;
import fuzs.strawstatues.api.client.gui.screens.armorstand.ArmorStandTickBoxScreen;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.C2SStrawStatueModelPartMessage;
import fuzs.strawstatues.network.client.C2SStrawStatueOwnerMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;

public class StrawStatueModelPartsScreen extends ArmorStandTickBoxScreen<PlayerModelPart> {
    public static final String TEXT_BOX_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.modelParts.name";

    public StrawStatueModelPartsScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(holder, inventory, component, dataSyncHandler);
    }

    @Override
    protected PlayerModelPart[] getAllTickBoxValues() {
        return PlayerModelPart.values();
    }

    @Override
    protected AbstractWidget makeTickBoxWidget(ArmorStand armorStand, int buttonStartY, int index, PlayerModelPart option) {
        StrawStatue strawStatue = (StrawStatue) armorStand;
        return new TickBoxButton(this.leftPos + 96, this.topPos + buttonStartY + index * 22, 6, 76, option.getName(), strawStatue.isModelPartShown(option), (Button button) -> {
            boolean value = ((TickBoxButton) button).isSelected();
            strawStatue.setModelPart(option, value);
            StrawStatues.NETWORK.sendToServer(new C2SStrawStatueModelPartMessage(option, value));
        }, Button.NO_TOOLTIP);
    }

    @Override
    protected void syncNameChange(String input) {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueOwnerMessage(input));
    }

    @Override
    protected int getNameMaxLength() {
        return 16;
    }

    @Override
    protected String getNameDefaultValue() {
        return ((StrawStatue) this.getHolder().getArmorStand()).getOwner().map(GameProfile::getName).orElse("");
    }

    @Override
    protected Component getNameComponent() {
        return Component.translatable(TEXT_BOX_TRANSLATION_KEY);
    }

    @Override
    public ArmorStandScreenType getScreenType() {
        return ModRegistry.MODEL_PARTS_SCREEN_TYPE;
    }
}
