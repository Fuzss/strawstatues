package fuzs.strawstatues.proxy;

import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientProxy extends ServerProxy {

    @Override
    public void appendStrawStatueHoverText(List<Component> lines) {
        Component shiftComponent = Component.empty().append(Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useComponent = Component.empty().append(Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useItemComponent = Component.empty().append(StrawStatues.CONFIG.get(CommonConfig.class).useItem.getDescription()).withStyle(ChatFormatting.GOLD);
        lines.add(Component.translatable("armorstatues.item.armor_stand.description", shiftComponent, useComponent, useItemComponent).withStyle(ChatFormatting.GRAY));
    }
}
