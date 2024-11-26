package com.github.yukkuritaku.modernwarpmenu.client.gui.screens;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.client.gui.components.*;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.ScaledGrid;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Island;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Warp;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.listeners.InventoryChangeListener;
import com.github.yukkuritaku.modernwarpmenu.mixin.ScreenAccessor;
import com.github.yukkuritaku.modernwarpmenu.state.ModernWarpMenuState;
import com.github.yukkuritaku.modernwarpmenu.utils.ChatUtils;
import com.github.yukkuritaku.modernwarpmenu.utils.GameCheckUtils;
import com.github.yukkuritaku.modernwarpmenu.utils.WarpVisibilityCheckUtils;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModernWarpScreen extends CustomContainerScreen{
    private static final Logger LOGGER = LogUtils.getLogger();
    /** Delay in ms before the player can warp again if the last warp attempt failed */
    private static final long WARP_FAIL_COOL_DOWN = 500L;
    /** The amount of time in ms that the error message remains on-screen after a failed warp attempt */
    private static final long WARP_FAIL_TOOLTIP_DISPLAY_TIME = 2000L;

    public final Menu warpMenu;
    private Layout layout;
    private final SimpleContainer chestInventory;
    private ConfigButton configButton;
    private InventoryChangeListener inventoryListener;

    /**
     * Last slot index in the {@link com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.ItemMatchCondition}
     * list for {@link #menu}
     *
     * @see com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.SkyBlockConstants#menuMatchingMap()
     */
    protected int lastSlotIndexToCheck;

    private RuntimeException guiInitException;
    private Component warpFailMessage;
    private Component originalTitle;
    protected long warpFailCoolDownExpiryTime;
    private long warpFailTooltipExpiryTime;

    public ModernWarpScreen(Menu warpMenu, ChestMenu menu, Inventory playerInventory, Layout layout) {
        super(menu, playerInventory, layout.backgroundTexture(), Component.empty());
        this.warpMenu = warpMenu;
        this.layout = layout;
        this.chestInventory = (SimpleContainer)((ChestMenu)playerInventory.player.containerMenu).getContainer();
        if (SettingsManager.get().general.warpMenuEnabled) {
            /*
            Render a blank custom UI before buttons are enabled to prevent the vanilla chest UI from displaying
            while the fancy warp menu loads
             */
            setCustomUIState(true, true);
            this.inventoryListener = new InventoryChangeListener(new ChestItemChangeCallback(this));
            this.chestInventory.addListener(this.inventoryListener);
        }
        this.originalTitle =  Component.literal(warpMenu.getDisplayName());
    }

    public ScaledGrid getScaledGrid(){
        return this.grid;
    }

    /**
     * Called whenever an item in the inventory of the {@link ChestMenu} changes.
     * This is used to enable the fancy warp menu when the SkyBlock menu the player has open is a warp menu.
     *
     * @param triggerCount number of times {@link #inventoryListener} was triggered
     */
    private void onChestItemChange(int triggerCount) {
        /*
        Don't start checking until the item in the last slot to check has been loaded.

        The item change event is triggered twice for each item, and the item stack is set on the 2nd time it's
        triggered. For example, slot 53 is actually set on the 106th time the item change event triggers.
        (lastSlotIndexToCheck + 1) since slots are 0-indexed but trigger count starts at 1
         */
        if (triggerCount > (this.lastSlotIndexToCheck + 1) * 2) {
            try {
                boolean menuItemsMatch = GameCheckUtils.menuItemsMatch(this.warpMenu, this.chestInventory);
                setCustomUIState(menuItemsMatch, menuItemsMatch);
                updateButtonStates();
                this.configButton.setVisible(menuItemsMatch);
                if (!menuItemsMatch) {
                    ChatUtils.sendMessageWithModNamePrefix("Warning: Chest has correct name but items mismatched");
                }
            } catch (RuntimeException e) {
                ChatUtils.sendErrorMessageWithCopyableThrowable("modernwarpmenu.errors.modernWarpGui.itemMatchFailed", e);
                setCustomUIState(false, false);
            }finally {
                // execute is required, because throw ConcurrentModificationException
                Minecraft.getInstance().execute(() -> this.chestInventory.removeListener(this.inventoryListener));
            }
        }
    }

    /**
     * Draws a simple error screen to display {@link #guiInitException}
     *
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     */
    public void drawExceptionScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        // Labels are under the background for some reason
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 1);
        for (Renderable renderable : ((ScreenAccessor)this).getRenderables()){
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.pose().popPose();
        for (GuiEventListener listener : this.children()){
            ((Button)listener).render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    protected void addIslandButtons() {
        for (Island island : layout.islandList()) {
            addIslandButton(island);
        }
    }

    protected void addIslandButton(Island island) {
        IslandButton button = new IslandButton(this, this.window, island,
                islandButton -> this.islandButtonHandler((IslandButton) islandButton), Supplier::get);
        if (island.warpList.size() == 1){
            this.addRenderableWidget(button);
        }
        for (Warp warp : island.warpList) {
            this.addRenderableWidget(new WarpButton(button, warp,
                    warpButton -> this.warpButtonHandler((WarpButton) warpButton), Supplier::get));
        }
        if (island.warpList.size() > 1) {
            this.addRenderableWidget(button);
        }
    }

    protected void islandButtonHandler(IslandButton button){}
    protected void warpButtonHandler(WarpButton button){}

    /**
     * Called when a warp attempt fails
     *
     * @param failMessageKey the translation key of the failure message to display on the Gui
     * @param replacements replacement objects to substitute in place of placeholders in the translated message
     */
    public void onWarpFail(String failMessageKey, Object... replacements) {
        long currentTime = Util.getMillis();
        this.warpFailCoolDownExpiryTime = currentTime + WARP_FAIL_COOL_DOWN;
        this.warpFailTooltipExpiryTime = currentTime + WARP_FAIL_TOOLTIP_DISPLAY_TIME;
        this.warpFailMessage = Component.translatable(failMessageKey, replacements).withStyle(ChatFormatting.RED);
    }

    /**
     * Left-clicks an inventory slot at the given index in the current screen if the current screen
     * is an instance of {@link net.minecraft.client.gui.screens.inventory.ContainerScreen} and the mouse is not already holding an item
     *
     * @param slotIndex the index of the inventory slot to click
     */
    protected void clickSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < this.menu.slots.size()) {
            Slot slotToClick = this.menu.slots.get(slotIndex);

            if (slotToClick.hasItem()) {
                if (this.menu.getCarried().isEmpty()) {
                    // Left click no shift
                    slotClicked(this.menu.slots.get(slotIndex), slotIndex,
                            GLFW.GLFW_MOUSE_BUTTON_LEFT, ClickType.PICKUP);
                } else {
                    onWarpFail(ModernWarpMenu.getFullLanguageKey("errors.mouseIsHoldingItem"));
                }
            } else {
                onWarpFail(ModernWarpMenu.getFullLanguageKey("errors.slotHasNoItem"), slotIndex);
            }
        } else {
            onWarpFail(ModernWarpMenu.getFullLanguageKey("errors.slotNumberOutOfBounds"), slotIndex);
        }
    }

    private void renderDebugStrings(GuiGraphics guiGraphics,
                                    List<Component> debugStrings,
                                    int drawX, int drawY,
                                    int nearestGridX, int nearestGridY,
                                    int zLevel) {
        debugStrings.add(Component.literal("gridX: " + nearestGridX));
        debugStrings.add(Component.literal("gridY: " + nearestGridY));
        // zLevel of -1 means z is not relevant, like in the case of screen coordinates
        if (zLevel > -1) {
            debugStrings.add(Component.literal("zLevel: " + zLevel));
        }
        guiGraphics.renderTooltip(Minecraft.getInstance().font, debugStrings,
                Optional.empty(), drawX, drawY);
        guiGraphics.fill(drawX - 2, drawY - 2, drawX + 2, drawY + 2, Color.RED.getRGB());
    }


    @Override
    protected void init() {
        super.init();
        this.window = Minecraft.getInstance().getWindow();
        this.grid = new ScaledGrid(0, 0,
                this.window.getGuiScaledWidth(),
                this.window.getGuiScaledHeight(),
                Island.GRID_UNIT_HEIGHT_FACTOR, Island.GRID_UNIT_WIDTH_FACTOR, false);
        Warp.initDefaults(this.window);
        this.configButton = new ConfigButton(layout, this.window, button -> {
            if (SettingsManager.get().general.warpMenuEnabled) {
                ModernWarpMenuState.setOpenConfigMenuRequested(true);
                if (Minecraft.getInstance().player != null)
                    Minecraft.getInstance().player.closeContainer();
            } else {
                SettingsManager.get().general.warpMenuEnabled = true;
                SettingsManager.save();
                ChatUtils.sendMessageWithModNamePrefix(Component.translatable(
                        "modernwarpmenu.messages.modernWarpMenuEnabled").withStyle(ChatFormatting.GREEN));
                if (GameCheckUtils.menuItemsMatch(this.warpMenu, chestInventory)) {
                    setCustomUIState(true, true);
                } else {
                    ModernWarpMenuState.setOpenConfigMenuRequested(true);
                    if (Minecraft.getInstance().player != null)
                        Minecraft.getInstance().player.closeContainer();
                }
            }
        }, Supplier::get);
        this.addRenderableWidget(this.configButton);
        if (this.lastSlotIndexToCheck > this.chestInventory.getContainerSize()) {
            ChatUtils.sendMessageWithModNamePrefix(Component.translatable(
                            "modernwarpmenu.errors.modernWarpScreen.chestInventoryTooSmall", this.chestInventory.getContainerSize(), lastSlotIndexToCheck)
                    .withStyle(ChatFormatting.RED));
            setCustomUIState(false, false);
            this.chestInventory.removeListener(this.inventoryListener);
            return;
        }
        if (SettingsManager.get().general.showRegularWarpMenuButton) {
            this.addRenderableWidget(new RegularWarpMenuButton(this.layout, this.window, this.grid, button -> {
                if (SettingsManager.get().general.warpMenuEnabled) {
                    SettingsManager.get().general.warpMenuEnabled = false;
                    SettingsManager.save();
                    setCustomUIState(false, false);
                }
            }, Supplier::get));
        }
        /*
        Sometimes button initialization and visibility checks go wrong.
        This halts screen initialization and lets the user copy the exception in those cases.
         */
        try {
            addIslandButtons();
            updateButtonStates();
        }catch (RuntimeException e){
            this.guiInitException = e;
            this.clearWidgets();
            Minecraft.getInstance().execute(() -> this.chestInventory.removeListener(this.inventoryListener));
            int lineCount = 2;
            int labelX = 0;
            int labelY = this.height / 5;
            int ySpacing = Minecraft.getInstance().font.lineHeight + 3;

            for (int i = 0; i < lineCount; i++) {
                if (i == 0) {
                    MultiLineTextWidget widget = new MultiLineTextWidget(labelX, labelY,
                            Component.translatable("modernwarpmenu.errors.modernWarpGui.initFailed", getClass().getSimpleName()).withStyle(ChatFormatting.RED),
                            Minecraft.getInstance().font);
                    widget.setColor(ARGB.white(1.0f));
                    widget.setWidth(this.width);
                    widget.setCentered(true);
                    this.addRenderableOnly(widget);
                } else {
                    MultiLineTextWidget widget = new MultiLineTextWidget(labelX, labelY,
                            Component.literal(String.format("%s : %s", guiInitException.getClass().getName(), guiInitException.getLocalizedMessage()))
                                    .withStyle(ChatFormatting.WHITE),
                            Minecraft.getInstance().font);
                    widget.setColor(ARGB.white(1.0f));
                    widget.setWidth(this.width);
                    widget.setCentered(true);
                    this.addRenderableOnly(widget);
                }
                labelY = labelY + ySpacing;
            }
            this.addRenderableWidget(new TimedMessageButton(this.width / 2 - 100, labelY + ySpacing,
                    Component.translatable("modernwarpmenu.gui.buttons.copyToClipboard"),
                    button -> {
                        TextFieldHelper.setClipboardContents(Minecraft.getInstance(), ExceptionUtils.getStackTrace(guiInitException));
                        ((TimedMessageButton) button).setTimedMessage(
                                Component.translatable("modernwarpmenu.gui.buttons.copyToClipboard.copied"), 1500);
                    }, Supplier::get));
        }
    }

    @Override
    protected void updateButtonStates() {
        for (GuiEventListener listener : this.children()) {
            // Skip the config button as it's active on both the custom and default UI.
            if (listener instanceof CustomContainerButton button && !(listener instanceof ConfigButton)) {

                button.setActive(this.customUIInteractionEnabled);
                button.setVisible(this.renderCustomUI);

                if (!this.renderCustomUI) {
                    continue;
                }

                if (button instanceof IslandButton islandButton) {
                    Island island = islandButton.island;

                    if (island.warpList.size() == 1) {
                        boolean showIsland = WarpVisibilityCheckUtils.shouldShowSingleWarpIsland(island);

                        button.setVisible(showIsland);
                    }
                } else if (button instanceof WarpButton warpButton) {
                    Island island = warpButton.getIsland();
                    Warp warp = warpButton.getWarp();
                    boolean shouldShowWarp = WarpVisibilityCheckUtils.shouldShowWarp(warp);

                    if (island.warpList.size() == 1) {
                        warpButton.setDrawWarpLabel(!SettingsManager.get().general.hideWarpLabelForIslandsWithOneWarp);
                    }

                    warpButton.setVisible(shouldShowWarp);
                }
            }
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.customUIInteractionEnabled) {
            return customUIMouseClicked(mouseX, mouseY, button);
        } else {
            /*
             Don't send a C0EPacketClickWindow when clicking the config button while the custom UI is disabled
             A null check is required here as it's possible for clicks to occur before the button is initialized.
             */
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.configButton != null && this.configButton.isHoveredOrFocused()) {
                return this.configButton.mouseClicked(mouseX, mouseY, button);
            } else {
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    protected void renderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : ((ScreenAccessor)this).getRenderables()) {
            if (renderable instanceof ConfigButton || SettingsManager.get().general.warpMenuEnabled) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    protected void renderCustomUI(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (guiInitException != null) {
            drawExceptionScreen(guiGraphics, mouseX, mouseY, partialTick);
        }
        /*
        Patcher inventory scale doesn't reset inventory scale until the first draw of the screen after
        the inventory is closed. Setting res in initGui would use Patcher's scaled resolution instead of Minecraft's
        resolution.
        */
        /*if (!screenDrawn && EnvironmentDetails.isPatcherInstalled()) {
            screenDrawn = true;
            initGui();
            this.width = res.getScaledWidth();
            this.height = res.getScaledHeight();
        }*/
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        List<CustomContainerButton> hoveredButtons = new ArrayList<>();
        // When multiple island buttons overlap, mark only the top one as hovered.
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof IslandButton islandButton) {
                islandButton.calculateHoverState(mouseX, mouseY);
                if (islandButton.isHoveredOrFocused()) {
                    hoveredButtons.add(islandButton);
                }
            }
        }

        for (int i = 0; i < hoveredButtons.size() - 1; i++) {
            hoveredButtons.get(i).setHovered(false);
        }
        renderButtons(guiGraphics, mouseX, mouseY, partialTick);
        // Draw warp fail tooltip
        if (Util.getMillis() <= warpFailTooltipExpiryTime && warpFailMessage != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.warpFailMessage, mouseX, mouseY);
        }

        if (SettingsManager.get().debug.debugModeEnabled && SettingsManager.get().debug.showDebugOverlay) {
            List<Component> debugMessages = new ArrayList<>();
            int drawX;
            int drawY;
            int nearestX;
            int nearestY;
            boolean tooltipDrawn = false;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 20);
            // Draw screen resolution
            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    String.format("%d x %d (%d)",
                            this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight(),
                            this.window.calculateScale(Minecraft.getInstance().options.guiScale().get(),
                                    Minecraft.getInstance().isEnforceUnicode())),
                    this.width / 2, this.height - 20, 14737632);
            // Draw version number
            FabricLoader.getInstance().getModContainer(ModernWarpMenu.MOD_ID).ifPresent(modContainer -> {
                String name = modContainer.getMetadata().getName();
                String version = modContainer.getMetadata().getVersion().getFriendlyString();
                guiGraphics.drawString(Minecraft.getInstance().font, name + " " + version, this.width / 2, this.height - 10, 14737632);
            });
            // Shift to draw island grid instead of warp grid
            if (!hasShiftDown()) {
                for (GuiEventListener button : this.children()) {
                    // Draw island button coordinate tooltips, draw last to prevent clipping
                    if (button instanceof IslandButton islandBtn && islandBtn.isHoveredOrFocused()) {
                        debugMessages.add(islandBtn.getMessage().copy().withStyle(ChatFormatting.GREEN));
                        nearestX = islandBtn.scaledGrid.findNearestGridX(mouseX);
                        nearestY = islandBtn.scaledGrid.findNearestGridY(mouseY);
                        drawX = (int) islandBtn.scaledGrid.getActualX(nearestX);
                        drawY = (int) islandBtn.scaledGrid.getActualY(nearestY);
                        renderDebugStrings(guiGraphics, debugMessages, drawX, drawY, nearestX, nearestY, islandBtn.getZLevel());
                        tooltipDrawn = true;
                        break;
                    }
                }
            }
            // Draw screen coordinate tooltips
            if (!tooltipDrawn) {
                nearestX = this.grid.findNearestGridX(mouseX);
                nearestY = this.grid.findNearestGridY(mouseY);
                drawX = (int) this.grid.getActualX(nearestX);
                drawY = (int) this.grid.getActualY(nearestY);
                renderDebugStrings(guiGraphics, debugMessages, drawX, drawY, nearestX, nearestY, -1);
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.originalTitle, this.titleLabelX, this.titleLabelY, 4210752, false);
        super.renderLabels(guiGraphics, mouseX, mouseY);

    }

    @Override
    protected boolean customUIKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (guiInitException != null) return false;
        if (SettingsManager.get().debug.debugModeEnabled) {
            if (keyCode == GLFW.GLFW_KEY_R) {
                if (hasShiftDown()) {
                    Minecraft.getInstance().reloadResourcePacks().thenAcceptAsync( v -> {
                        this.layout = ModernWarpMenuState.getLayoutForMenu(this.warpMenu);
                        init();
                    }, this.screenExecutor);
                }
            } else if (keyCode == GLFW.GLFW_KEY_TAB) {
                SettingsManager.get().debug.showDebugOverlay = !SettingsManager.get().debug.showDebugOverlay;
                SettingsManager.save();
            } else if (keyCode == GLFW.GLFW_KEY_B) {
                SettingsManager.get().debug.drawBorders = !SettingsManager.get().debug.drawBorders;
                SettingsManager.save();
            }
        }
        return true;
    }

    @Override
    protected boolean customUIMouseClicked(double mouseX, double mouseY, int button) {
        // Left click
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            for (GuiEventListener listener : this.children()) {
                if (listener instanceof CustomContainerButton) {
                    if (listener.mouseClicked(mouseX, mouseY, button)) {
                        break;
                    }
                }
            }
        }
        return true;
    }


    /**
     * A callback called when any item in the chest it is attached to changes
     */
    private static class ChestItemChangeCallback implements Consumer<Container> {
        private final ModernWarpScreen modernWarpScreen;
        private int triggerCount;

        ChestItemChangeCallback(ModernWarpScreen modernWarpScreen) {
            this.modernWarpScreen = modernWarpScreen;
            this.triggerCount = 0;
        }

        @Override
        public void accept(Container chestInventory) {
            this.triggerCount++;
            this.modernWarpScreen.onChestItemChange(this.triggerCount);
        }
    }
}
