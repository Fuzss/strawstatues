package fuzs.strawstatues.client.gui.screens;

import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.statuemenus.api.v1.client.gui.screens.StatuePositionScreen;
import fuzs.statuemenus.api.v1.network.client.data.DataSyncHandler;
import fuzs.statuemenus.api.v1.world.inventory.StatueHolder;
import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.strawstatues.network.client.ServerboundStrawStatueScaleMessage;
import fuzs.strawstatues.network.client.ServerboundStrawStatueSkinPatchMessage;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.inventory.data.StrawStatueScreenTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class StrawStatueTexturesScreen extends StatuePositionScreen {
    public static final Component SKIN_TEXTURE_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "texture.skin"));
    public static final Component CAPE_TEXTURE_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "texture.cape"));
    public static final Component ELYTRA_TEXTURE_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "texture.elytra"));
    public static final Component ROTATION_X_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "rotation.x"));
    public static final Component ROTATION_Y_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "rotation.y"));
    public static final Component ROTATION_Z_COMPONENT = Component.translatable(StrawStatueScreenTypes.TEXTURES.id()
            .toLanguageKey("screen", "rotation.z"));
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> SKIN_TEXTURE_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new TextureWidget(SKIN_TEXTURE_COMPONENT, ServerboundStrawStatueSkinPatchMessage.DataType.SKIN);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> CAPE_TEXTURE_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new TextureWidget(CAPE_TEXTURE_COMPONENT, ServerboundStrawStatueSkinPatchMessage.DataType.CAPE);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> ELYTRA_TEXTURE_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new TextureWidget(ELYTRA_TEXTURE_COMPONENT,
                ServerboundStrawStatueSkinPatchMessage.DataType.ELYTRA);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> ROTATION_X_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new CustomRotationWidget(ROTATION_X_COMPONENT,
                (StrawStatue) livingEntity,
                ServerboundStrawStatueScaleMessage.ValueAccessor.ROTATION_X);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> ROTATION_Y_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new RotationWidget(ROTATION_Y_COMPONENT,
                livingEntity::getYRot,
                screen.dataSyncHandler::sendRotation);
    };
    protected static final ArmorStandWidgetFactory<StrawStatueTexturesScreen> ROTATION_Z_WIDGET_FACTORY = (StrawStatueTexturesScreen screen, LivingEntity livingEntity) -> {
        return screen.new CustomRotationWidget(ROTATION_Z_COMPONENT,
                (StrawStatue) livingEntity,
                ServerboundStrawStatueScaleMessage.ValueAccessor.ROTATION_Z);
    };

    public StrawStatueTexturesScreen(StatueHolder statueHolder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(statueHolder, inventory, component, dataSyncHandler);
    }

    @Override
    protected List<ArmorStandWidget> buildWidgets(LivingEntity livingEntity) {
        return buildWidgets(this,
                livingEntity,
                List.of(ROTATION_X_WIDGET_FACTORY,
                        ROTATION_Y_WIDGET_FACTORY,
                        ROTATION_Z_WIDGET_FACTORY,
                        SKIN_TEXTURE_WIDGET_FACTORY,
                        CAPE_TEXTURE_WIDGET_FACTORY,
                        ELYTRA_TEXTURE_WIDGET_FACTORY));
    }

    @Override
    public StatueScreenType getScreenType() {
        return StrawStatueScreenTypes.TEXTURES;
    }

    protected class CustomRotationWidget extends RotationWidget {
        private final ServerboundStrawStatueScaleMessage.ValueAccessor valueAccessor;

        public CustomRotationWidget(Component title, StrawStatue strawStatue, ServerboundStrawStatueScaleMessage.ValueAccessor valueAccessor) {
            super(title, () -> valueAccessor.getRotationsComponent(strawStatue.getEntityPose()), (Float value) -> {
                strawStatue.setEntityPose(valueAccessor.setRotationsComponent(strawStatue.getEntityPose(), value));
            });
            this.valueAccessor = valueAccessor;
        }

        @Override
        protected OptionalDouble getDefaultValue() {
            return OptionalDouble.of(
                    this.valueAccessor.getRotationsComponent(StrawStatue.DEFAULT_ENTITY_POSE) / 360.0F);
        }

        @Override
        protected double getCurrentValue() {
            return this.valueGetter.getAsDouble() / 360.0;
        }

        @Override
        protected void setNewValue(double newValue) {
            this.valueSetter.accept((float) (newValue * 360.0));
        }

        @Override
        protected void applyClientValue(double newValue) {
            MessageSender.broadcast(new ServerboundStrawStatueScaleMessage(this.valueAccessor,
                    (float) (newValue * 360.0)));
        }
    }

    private class TextureWidget extends ArmorStandWidget {
        private final ServerboundStrawStatueSkinPatchMessage.DataType dataType;
        private EditBox editBox;

        public TextureWidget(Component component, ServerboundStrawStatueSkinPatchMessage.DataType dataType) {
            super(component);
            this.dataType = dataType;
        }

        @Override
        public void init(int posX, int posY) {
            super.init(posX, posY);
            AbstractWidget checkmarkButton = this.addRenderableWidget(new SpritelessImageButton(posX + 174,
                    posY + 1,
                    20,
                    20,
                    212,
                    0,
                    getArmorStandWidgetsLocation(),
                    (Button button) -> {
                        Identifier identifier = Identifier.tryParse(this.editBox.getValue());
                        if (identifier != null) {
                            Optional<ClientAsset.ResourceTexture> resourceTexture;
                            if (identifier.getPath().isEmpty()) {
                                resourceTexture = Optional.empty();
                            } else {
                                resourceTexture = Optional.of(new ClientAsset.ResourceTexture(identifier));
                            }

                            MessageSender.broadcast(new ServerboundStrawStatueSkinPatchMessage(this.dataType,
                                    resourceTexture));
                        } else {
                            button.active = false;
                        }
                    }));
            checkmarkButton.setTooltip(Tooltip.create(SAVE_COMPONENT));
            this.editBox = new EditBox(StrawStatueTexturesScreen.this.font,
                    posX + 77,
                    posY,
                    91,
                    22,
                    StrawStatueTexturesScreen.this.getHolder().getEntity().getType().getDescription()) {
                @Override
                public boolean keyPressed(KeyEvent event) {
                    return super.keyPressed(event) || this.canConsumeInput();
                }
            };
            this.editBox.setMaxLength(256);
            this.editBox.setResponder((String string) -> {
                Identifier identifier = Identifier.tryParse(string);
                if (identifier != null) {
                    checkmarkButton.active = true;
                    this.editBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                } else {
                    checkmarkButton.active = false;
                    this.editBox.setTextColor(ARGB.opaque(ChatFormatting.RED.getColor()));
                }

                if (identifier != null && !identifier.getPath().isEmpty()) {
                    String texturePath = new ClientAsset.ResourceTexture(identifier).texturePath().toString();
                    this.editBox.setTooltip(Tooltip.create(Component.literal(texturePath)));
                } else {
                    this.editBox.setTooltip(null);
                }
            });
            PlayerSkin.Patch skinPatch = ((StrawStatue) StrawStatueTexturesScreen.this.getHolder()
                    .getEntity()).getSkinPatch();
            this.editBox.setValue(this.dataType.getTexturePath(skinPatch)
                    .map(ClientAsset.ResourceTexture::id)
                    .map(Identifier::toString)
                    .orElse(""));
            this.addRenderableWidget(this.editBox);
        }
    }
}
