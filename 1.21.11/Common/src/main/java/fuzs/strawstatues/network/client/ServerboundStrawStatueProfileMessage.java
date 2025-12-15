package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;

public record ServerboundStrawStatueProfileMessage(String inputName) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueProfileMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ServerboundStrawStatueProfileMessage::inputName,
            ServerboundStrawStatueProfileMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof StatueMenu statueMenu
                        && statueMenu.stillValid(context.player())) {
                    String inputName = StringUtil.filterText(ServerboundStrawStatueProfileMessage.this.inputName);
                    if (StringUtil.isValidPlayerName(inputName)) {
                        ((StrawStatue) statueMenu.getEntity()).setProfile(ServerboundStrawStatueProfileMessage.this.getResolvableProfile());
                    }
                }
            }
        };
    }

    private ResolvableProfile getResolvableProfile() {
        if (this.inputName.isEmpty()) {
            return StrawStatue.DEFAULT_PROFILE;
        } else {
            return ResolvableProfile.createUnresolved(this.inputName);
        }
    }
}
