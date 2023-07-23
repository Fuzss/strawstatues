package fuzs.strawstatues.proxy;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientProxy extends ServerProxy {

    @Override
    public void appendStrawStatueHoverText(List<Component> lines) {
        Minecraft minecraft = Minecraft.getInstance();
        Component shiftComponent = Component.empty().append(minecraft.options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useComponent = Component.empty().append(minecraft.options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        lines.add(Component.translatable("armorstatues.item.armor_stand.description", shiftComponent, useComponent).withStyle(ChatFormatting.GRAY));
    }
}
