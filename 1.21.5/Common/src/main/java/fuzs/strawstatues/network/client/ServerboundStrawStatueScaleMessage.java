package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.helper.ScaleAttributeHelper;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record ServerboundStrawStatueScaleMessage(DataType dataType, float value) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueScaleMessage> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.fromEnum(DataType.class),
            ServerboundStrawStatueScaleMessage::dataType,
            ByteBufCodecs.FLOAT,
            ServerboundStrawStatueScaleMessage::value,
            ServerboundStrawStatueScaleMessage::new);

    public static Consumer<Float> getValueSender(DataType dataType) {
        return (Float value) -> {
            MessageSender.broadcast(new ServerboundStrawStatueScaleMessage(dataType, value));
        };
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof ArmorStandMenu menu &&
                        menu.stillValid(context.player())) {
                    ServerboundStrawStatueScaleMessage.this.dataType.consumer.accept((StrawStatue) menu.getArmorStand(),
                            ServerboundStrawStatueScaleMessage.this.value);
                }
            }
        };
    }

    public enum DataType {
        ROTATION_X(StrawStatue::setEntityXRotation),
        ROTATION_Z(StrawStatue::setEntityZRotation),
        RESET((StrawStatue strawStatue, Float value) -> {
            ScaleAttributeHelper.setScale(strawStatue, ScaleAttributeHelper.DEFAULT_SCALE);
            strawStatue.setEntityRotations(StrawStatue.DEFAULT_ENTITY_ROTATIONS.x(),
                    StrawStatue.DEFAULT_ENTITY_ROTATIONS.z());
        });

        public final BiConsumer<StrawStatue, Float> consumer;

        DataType(BiConsumer<StrawStatue, Float> consumer) {
            this.consumer = consumer;
        }
    }
}
