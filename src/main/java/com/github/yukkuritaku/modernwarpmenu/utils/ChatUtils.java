package com.github.yukkuritaku.modernwarpmenu.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ChatUtils {

    public static final String COPY_TO_CLIPBOARD_TRANSLATION_KEY = "modernwarpmenu.gui.buttons.copyToClipboard";
    /**
     * Sends a client-side chat message with a warning message and a clickable component used to copy the stacktrace of
     * the given throwable.
     *
     * @param warningMessageTranslationKey translation key of the warning message to be displayed before the clickable component
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    public static void sendWarningMessageWithCopyableThrowable(String warningMessageTranslationKey, Throwable throwable) {
        Style errorMessageStyle = Style.EMPTY.withColor(ChatFormatting.GOLD);
        sendMessageWithCopyableThrowable(warningMessageTranslationKey, errorMessageStyle, throwable);
    }

    /**
     * Sends a client-side chat message with an error message and a clickable component used to copy the stacktrace of
     * the given throwable.
     *
     * @param errorMessageTranslationKey translation key of the error message to be displayed before the clickable component
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    public static void sendErrorMessageWithCopyableThrowable(String errorMessageTranslationKey, Throwable throwable) {
        Style errorMessageStyle = Style.EMPTY.withColor(ChatFormatting.RED);
        sendMessageWithCopyableThrowable(errorMessageTranslationKey, errorMessageStyle, throwable);
    }

    /**
     * Sends a client-side chat message with the mod name acronym as a prefix.
     * <br>
     * Example: [FWM] message
     *
     * @param message the message to send
     */
    public static void sendMessageWithModNamePrefix(String message) {
        sendMessageWithModNamePrefix(Component.literal(message));
    }

    /**
     * Sends a client-side chat message with the mod name acronym as a prefix.
     * <br>
     * Example: [FWM] message
     *
     * @param message the message to send
     */
    public static void sendMessageWithModNamePrefix(Component message) {
        MutableComponent prefixComponent = createModNamePrefixComponent();
        prefixComponent.append(message);
        Minecraft.getInstance().player.displayClientMessage(prefixComponent, false);
    }

    /**
     * Returns an {@code IChatComponent} with the acronym of the mod name ("FWM") to be used as a prefix in chat messages
     * sent by the mod
     *
     * @return an {@code IChatComponent} with the acronym of the mod name ("FWM") to be used as a prefix in chat messages
     * sent by the mod
     */
    private static MutableComponent createModNamePrefixComponent() {
        Style plainStyle = Style.EMPTY.withColor(ChatFormatting.RESET);
        Style acronymStyle = Style.EMPTY
                .withColor(ChatFormatting.LIGHT_PURPLE)
                .withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Modern Warp Menu")));

        return Component.literal("[")
                .append(Component.literal("MWM").withStyle(acronymStyle))
                .append(Component.literal("] ").withStyle(plainStyle));
    }

    /**
     * Sends a client-side chat message with a clickable component used to copy the stacktrace of a given throwable.
     *
     * @param messageTranslationKey translation key of the message to be displayed before the clickable component
     * @param messageStyle the {@link Style} to assign to the {@code IChatComponent} containing the message
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    private static void sendMessageWithCopyableThrowable(String messageTranslationKey, Style messageStyle, Throwable throwable) {
        if (messageTranslationKey == null) {
            throw new NullPointerException("messageTranslationKey cannot be null");
        } else if (messageStyle == null) {
            throw new NullPointerException("messageStyle cannot be null");
        } else if (throwable == null) {
            throw new NullPointerException("throwable cannot be null");
        }

        Style plainStyle = Style.EMPTY.withColor(ChatFormatting.RESET);
        // setInsertion gives the component a unique identifier for ca.tirelesstraveler.fancywarpmenu.listeners.ChatListener to look for
        Style copyThrowableStyle = Style.EMPTY.withColor(ChatFormatting.BLUE)
                .withInsertion(messageTranslationKey)
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ExceptionUtils.getStackTrace(throwable)));

        Component component = createModNamePrefixComponent()
                .append(Component.translatable(messageTranslationKey).withStyle(messageStyle))
                .append(Component.literal(" [").withStyle(plainStyle))
                .append(Component.translatable(COPY_TO_CLIPBOARD_TRANSLATION_KEY).withStyle(copyThrowableStyle))
                .append(Component.literal("]").withStyle(plainStyle));
        if (Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.displayClientMessage(component, false);
    }
}
