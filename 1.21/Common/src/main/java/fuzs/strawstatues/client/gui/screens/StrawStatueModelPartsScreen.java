package fuzs.strawstatues.client.gui.screens;

import com.mojang.authlib.GameProfile;
import fuzs.statuemenus.api.v1.client.gui.components.TickBoxButton;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandTickBoxScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.StrawStatues;
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
import net.minecraft.world.item.component.ResolvableProfile;

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
        return new TickBoxButton(this.leftPos + 96, this.topPos + buttonStartY + index * 22, 6, 76, option.getName(), () -> strawStatue.isModelPartShown(option), (Button button) -> {
            boolean value = !strawStatue.isModelPartShown(option);
            strawStatue.setModelPart(option, value);
            StrawStatues.NETWORK.sendToServer(new C2SStrawStatueModelPartMessage(option, value).toServerboundMessage());
        });
    }

    @Override
    protected void syncNameChange(String input) {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueOwnerMessage(input).toServerboundMessage());
    }

    @Override
    protected int getNameMaxLength() {
        return 16;
    }

    @Override
    protected String getNameDefaultValue() {
        return ((StrawStatue) this.getHolder().getArmorStand()).getOwner().map(ResolvableProfile::gameProfile).map(GameProfile::getName).orElse("");
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
