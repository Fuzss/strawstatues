package fuzs.strawstatues.api.client.gui.screens.armorstand;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.ArmorStatuesApi;
import fuzs.strawstatues.api.client.gui.components.TickingButton;
import fuzs.strawstatues.api.client.gui.components.UnboundedSliderButton;
import fuzs.strawstatues.api.network.client.data.DataSyncHandler;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.function.UnaryOperator;

public abstract class AbstractArmorStandScreen extends Screen implements MenuAccess<ArmorStandMenu>, ArmorStandScreen {
    private static final ResourceLocation ARMOR_STAND_LIGHT_BACKGROUND_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/light_background.png");
    private static final ResourceLocation ARMOR_STAND_LIGHT_WIDGETS_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/light_widgets.png");
    private static final ResourceLocation ARMOR_STAND_LIGHT_EQUIPMENT_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/light_equipment.png");
    private static final ResourceLocation ARMOR_STAND_DARK_BACKGROUND_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/dark_background.png");
    private static final ResourceLocation ARMOR_STAND_DARK_WIDGETS_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/dark_widgets.png");
    private static final ResourceLocation ARMOR_STAND_DARK_EQUIPMENT_LOCATION = ArmorStatuesApi.id("textures/gui/container/armor_stand/dark_equipment.png");

    static ArmorStandInInventoryRenderer armorStandRenderer = ArmorStandInInventoryRenderer.SIMPLE;

    protected final int imageWidth = 210;
    protected final int imageHeight = 188;
    protected final ArmorStandHolder holder;
    private final Inventory inventory;
    protected final DataSyncHandler dataSyncHandler;
    protected int leftPos;
    protected int topPos;
    protected int inventoryEntityX;
    protected int inventoryEntityY;
    protected boolean smallInventoryEntity;
    protected int mouseX;
    protected int mouseY;
    private AbstractWidget[] buttons;

    public AbstractArmorStandScreen(ArmorStandHolder holder, Inventory inventory, Component component, DataSyncHandler dataSyncHandler) {
        super(component);
        this.holder = holder;
        this.inventory = inventory;
        this.dataSyncHandler = dataSyncHandler;
    }

    public static ResourceLocation getArmorStandBackgroundLocation() {
        return StrawStatues.CONFIG.get(CommonConfig.class).darkTheme.get() ? ARMOR_STAND_DARK_BACKGROUND_LOCATION : ARMOR_STAND_LIGHT_BACKGROUND_LOCATION;
    }

    public static ResourceLocation getArmorStandWidgetsLocation() {
        return StrawStatues.CONFIG.get(CommonConfig.class).darkTheme.get() ? ARMOR_STAND_DARK_WIDGETS_LOCATION : ARMOR_STAND_LIGHT_WIDGETS_LOCATION;
    }

    public static ResourceLocation getArmorStandEquipmentLocation() {
        return StrawStatues.CONFIG.get(CommonConfig.class).darkTheme.get() ? ARMOR_STAND_DARK_EQUIPMENT_LOCATION : ARMOR_STAND_LIGHT_EQUIPMENT_LOCATION;
    }

    @Override
    public ArmorStandHolder getHolder() {
        return this.holder;
    }

    @Override
    public <T extends Screen & MenuAccess<ArmorStandMenu> & ArmorStandScreen> T createScreenType(ArmorStandScreenType screenType) {
        T screen = ArmorStandScreenFactory.createScreenType(screenType, this.holder, this.inventory, this.title, this.dataSyncHandler);
        screen.setMouseX(this.mouseX);
        screen.setMouseY(this.mouseY);
        return screen;
    }

    @Override
    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    @Override
    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    @Override
    public void tick() {
        super.tick();
        for (GuiEventListener child : this.children()) {
            if (child instanceof TickingButton button) button.tick();
        }
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.buttons = makeButtons(this, this.leftPos, this.imageWidth, this.topPos, this::addRenderableWidget);
    }

    public static AbstractWidget[] makeButtons(Screen screen, int leftPos, int imageWidth, int topPos, UnaryOperator<AbstractWidget> addRenderableWidget) {
        AbstractWidget[] abstractWidgets = new AbstractWidget[3];
        abstractWidgets[0] = addRenderableWidget.apply(new ImageButton(leftPos + imageWidth - 3 - 21, topPos - 18, 15, 15, 48, 188, getArmorStandBackgroundLocation(), button -> {
            screen.onClose();
        }));
        abstractWidgets[1] = addRenderableWidget.apply(new ImageButton(leftPos + imageWidth - 3 - 76, topPos - 20, 24, 19, 0, 188, getArmorStandBackgroundLocation(), button -> {
            toggleThemeButtons(abstractWidgets[1], abstractWidgets[2], true);
        }));
        abstractWidgets[2] = addRenderableWidget.apply(new ImageButton(leftPos + imageWidth - 3 - 49, topPos - 20, 24, 19, 24, 188, getArmorStandBackgroundLocation(), button -> {
            toggleThemeButtons(abstractWidgets[1], abstractWidgets[2], true);
        }));
        toggleThemeButtons(abstractWidgets[1], abstractWidgets[2], false);
        return abstractWidgets;
    }

    private static void toggleThemeButtons(AbstractWidget lightThemeWidget, AbstractWidget darkThemeWidget, boolean toggleSetting) {
        boolean darkTheme = StrawStatues.CONFIG.get(CommonConfig.class).darkTheme.get();
        if (toggleSetting) {
            darkTheme = !darkTheme;
            StrawStatues.CONFIG.get(CommonConfig.class).darkTheme.set(darkTheme);
        }
        lightThemeWidget.active = darkTheme;
        darkThemeWidget.active = !darkTheme;
    }

    protected boolean renderInventoryEntity() {
        return true;
    }

    protected boolean disableMenuRendering() {
        return false;
    }

    protected void toggleMenuRendering(boolean disableMenuRendering) {
        for (AbstractWidget button : this.buttons) {
            button.visible = !disableMenuRendering;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (!this.disableMenuRendering() && handleTabClicked((int) mouseX, (int) mouseY, this.leftPos, this.topPos, this.imageHeight, this, this.dataSyncHandler.tabs())) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (!this.disableMenuRendering()) {
            this.renderBackground(poseStack);
        }
        this.renderBg(poseStack, partialTick, mouseX, mouseY);
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (!this.disableMenuRendering()) {
            findHoveredTab(this.leftPos, this.topPos, this.imageHeight, mouseX, mouseY, this.dataSyncHandler.tabs()).ifPresent(hoveredTab -> {
                this.renderTooltip(poseStack, hoveredTab.getComponent(), mouseX, mouseY);
            });
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        if (!this.disableMenuRendering()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getArmorStandBackgroundLocation());
            this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            drawThemeBg(poseStack, this.leftPos, this.topPos, this.imageWidth);
            drawTabs(poseStack, this.leftPos, this.topPos, this.imageHeight, this, this.dataSyncHandler.tabs());
            this.renderEntityInInventory(poseStack);
        }
    }

    private void renderEntityInInventory(PoseStack poseStack) {
        if (this.renderInventoryEntity()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getArmorStandWidgetsLocation());
            if (this.smallInventoryEntity) {
                this.blit(poseStack, this.leftPos + this.inventoryEntityX, this.topPos + this.inventoryEntityY, 200, 184, 50, 72);
                this.renderArmorStandInInventory(this.leftPos + this.inventoryEntityX + 24, this.topPos + this.inventoryEntityY + 65, 30, this.leftPos + this.inventoryEntityX + 24 - 10 - this.mouseX, this.topPos + this.inventoryEntityY + 65 - 44 - this.mouseY);
            } else {
                this.blit(poseStack, this.leftPos + this.inventoryEntityX, this.topPos + this.inventoryEntityY, 0, 0, 76, 108);
                this.renderArmorStandInInventory(this.leftPos + this.inventoryEntityX + 38, this.topPos + this.inventoryEntityY + 98, 45, (float) (this.leftPos + this.inventoryEntityX + 38 - 5) - this.mouseX, (float) (this.topPos + this.inventoryEntityY + 98 - 66) - this.mouseY);
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // make sure value is sent to server when mouse is released outside of slider widget, but when the slider value has been changed
        boolean mouseReleased = false;
        for (GuiEventListener child : this.children()) {
            if (child instanceof UnboundedSliderButton sliderButton) {
                if (sliderButton.isDirty()) {
                    mouseReleased |= child.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
        return mouseReleased || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (super.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return handleMouseScrolled((int) mouseX, (int) mouseY, delta, this.leftPos, this.topPos, this.imageHeight, this, this.dataSyncHandler.tabs());
    }

    public static <T extends Screen & ArmorStandScreen> boolean handleMouseScrolled(int mouseX, int mouseY, double delta, int leftPos, int topPos, int imageHeight, T screen, ArmorStandScreenType[] tabs) {
        delta = Math.signum(delta);
        if (delta != 0.0) {
            Optional<ArmorStandScreenType> optional = findHoveredTab(leftPos, topPos, imageHeight, mouseX, mouseY, tabs);
            if (optional.isPresent()) {
                ArmorStandScreenType screenType = cycleTabs(screen.getScreenType(), screen.getHolder().getDataProvider().getScreenTypes(), delta > 0.0);
                return openTabScreen(screen, screenType, false);
            }
        }
        return false;
    }

    public static <T extends Screen & ArmorStandScreen> boolean handleTabClicked(int mouseX, int mouseY, int leftPos, int topPos, int imageHeight, T screen, ArmorStandScreenType[] tabs) {
        Optional<ArmorStandScreenType> hoveredTab = findHoveredTab(leftPos, topPos, imageHeight, mouseX, mouseY, tabs);
        return hoveredTab.filter(armorStandScreenType -> openTabScreen(screen, armorStandScreenType, true)).isPresent();
    }

    private static <T extends Screen & ArmorStandScreen> boolean openTabScreen(T screen, ArmorStandScreenType screenType, boolean clickSound) {
        if (screenType != screen.getScreenType()) {
            Minecraft minecraft = ClientCoreServices.SCREENS.getMinecraft(screen);
            if (clickSound) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            minecraft.setScreen(screen.createScreenType(screenType));
            return true;
        }
        return false;
    }

    private static ArmorStandScreenType cycleTabs(ArmorStandScreenType currentScreenType, ArmorStandScreenType[] screenTypes, boolean backwards) {
        int index = ArrayUtils.indexOf(screenTypes, currentScreenType);
        return screenTypes[((backwards ? --index : ++index) % screenTypes.length + screenTypes.length) % screenTypes.length];
    }

    public static <T extends Screen & ArmorStandScreen> void drawTabs(PoseStack poseStack, int leftPos, int topPos, int imageHeight, T screen, ArmorStandScreenType[] tabs) {
        int tabsStartY = getTabsStartY(imageHeight, tabs.length);
        for (int i = 0; i < tabs.length; i++) {
            ArmorStandScreenType tabType = tabs[i];
            int tabX = leftPos - 32;
            int tabY = topPos + tabsStartY + 27 * i;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, getArmorStandWidgetsLocation());
            GuiComponent.blit(poseStack, tabX, tabY, 212, tabType == screen.getScreenType() ? 0 : 27, 35, 26, 256, 256);
            ItemRenderer itemRenderer = ClientCoreServices.SCREENS.getItemRenderer(screen);
            itemRenderer.blitOffset = 100.0F;
            itemRenderer.renderAndDecorateItem(tabType.getIcon(), tabX + 10, tabY + 5);
            itemRenderer.blitOffset = 0.0F;
        }
    }

    public static void drawThemeBg(PoseStack poseStack, int leftPos, int topPos, int imageWidth) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getArmorStandBackgroundLocation());
        GuiComponent.blit(poseStack, leftPos + imageWidth - 3 - 84, topPos - 24, 63, 188, 84, 24, 256, 256);
    }

    public static Optional<ArmorStandScreenType> findHoveredTab(int leftPos, int topPos, int imageHeight, int mouseX, int mouseY, ArmorStandScreenType[] tabs) {
        int tabsStartY = getTabsStartY(imageHeight, tabs.length);
        for (int i = 0; i < tabs.length; i++) {
            int tabX = leftPos - 32;
            int tabY = topPos + tabsStartY + 27 * i;
            if (mouseX > tabX && mouseX <= tabX + 32 && mouseY > tabY && mouseY <= tabY + 26) {
                return Optional.of(tabs[i]);
            }
        }
        return Optional.empty();
    }

    private static int getTabsStartY(int imageHeight, int tabsCount) {
        int tabsHeight = tabsCount * 26 + (tabsCount - 1);
        return (imageHeight - tabsHeight) / 2;
    }

    @Override
    public ArmorStandMenu getMenu() {
        return (ArmorStandMenu) this.holder;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (this.holder instanceof AbstractContainerMenu) {
            this.minecraft.player.closeContainer();
        }
        super.onClose();
    }
}
