package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.PlayerModelPart;

public record ServerboundStrawStatueModelPartMessage(PlayerModelPart modelPart,
                                                     boolean value) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueModelPartMessage> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.fromEnum(PlayerModelPart.class),
            ServerboundStrawStatueModelPartMessage::modelPart,
            ByteBufCodecs.BOOL,
            ServerboundStrawStatueModelPartMessage::value,
            ServerboundStrawStatueModelPartMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof ArmorStandMenu menu &&
                        menu.stillValid(context.player())) {
                    ((StrawStatue) menu.getArmorStand()).setModelPart(ServerboundStrawStatueModelPartMessage.this.modelPart,
                            ServerboundStrawStatueModelPartMessage.this.value);
                }
            }
        };
    }
}
