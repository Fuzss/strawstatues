package fuzs.strawstatues.client.gui.screens;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.TooltipBuilder;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.statuemenus.api.v1.client.gui.components.TickBoxButton;
import fuzs.statuemenus.api.v1.client.gui.screens.ArmorStandTickBoxScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.init.ModRegistry;
import fuzs.strawstatues.network.client.ServerboundStrawStatueModelPartMessage;
import fuzs.strawstatues.network.client.ServerboundStrawStatueSetProfileMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Locale;

public class StrawStatueModelPartsScreen extends ArmorStandTickBoxScreen<PlayerModelPart> {
    public static final String TEXT_BOX_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.modelParts.name";
    public static final String REFRESH_SKIN_TRANSLATION_KEY = StrawStatues.MOD_ID + ".screen.modelParts.refreshSkin";
    private static final ResourceLocation UNSEEN_NOTIFICATION_SPRITE = ResourceLocation.withDefaultNamespace(
            "textures/gui/sprites/icon/unseen_notification.png");

    private AbstractWidget refreshProfileButton;

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
        return new TickBoxButton(this.leftPos + 96,
                this.topPos + buttonStartY + index * 22,
                6,
                76,
                option.getName(),
                () -> strawStatue.isModelPartShown(option),
                (Button button) -> {
                    boolean value = !strawStatue.isModelPartShown(option);
                    strawStatue.setModelPart(option, value);
                    MessageSender.broadcast(new ServerboundStrawStatueModelPartMessage(option, value));
                });
    }

    @Override
    protected void syncNameChange(String input) {
        MessageSender.broadcast(new ServerboundStrawStatueSetProfileMessage(input, false));
    }

    @Override
    protected void init() {
        super.init();
        this.refreshProfileButton = this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 3,
                this.topPos + 31,
                10,
                10,
                0,
                0,
                10,
                UNSEEN_NOTIFICATION_SPRITE,
                10,
                10,
                (Button button) -> {
                    MessageSender.broadcast(new ServerboundStrawStatueSetProfileMessage(this.name.getValue().trim(),
                            true));
                    button.visible = false;
                }).setTextureLayout(SpritelessImageButton.SINGLE_TEXTURE_LAYOUT));
        TooltipBuilder.create()
                .addLines(Component.translatable(REFRESH_SKIN_TRANSLATION_KEY))
                .splitLines()
                .build(this.refreshProfileButton);
        this.refreshProfileButton.visible = false;
        ((StrawStatue) this.getHolder().getArmorStand()).getProfile()
                .map(ResolvableProfile::gameProfile)
                .ifPresent(this::setGameProfile);
    }

    public void setGameProfile(GameProfile gameProfile) {
        if (this.refreshProfileButton != null) {
            this.refreshProfileButton.visible = ServerboundStrawStatueSetProfileMessage.isOfflinePlayerProfile(
                    gameProfile);
        }
        if (this.name.getValue().toLowerCase(Locale.ROOT).equals(gameProfile.getName().toLowerCase(Locale.ROOT))) {
            this.name.setValue(gameProfile.getName());
        }
    }

    @Override
    protected int getNameMaxLength() {
        return 16;
    }

    @Override
    protected String getNameDefaultValue() {
        return ((StrawStatue) this.getHolder().getArmorStand()).getProfile()
                .map(ResolvableProfile::gameProfile)
                .map(GameProfile::getName)
                .orElse("");
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
