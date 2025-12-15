package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Rotations;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public record ServerboundStrawStatueScaleMessage(ValueAccessor valueAccessor,
                                                 float value) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueScaleMessage> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.fromEnum(ValueAccessor.class),
            ServerboundStrawStatueScaleMessage::valueAccessor,
            ByteBufCodecs.FLOAT,
            ServerboundStrawStatueScaleMessage::value,
            ServerboundStrawStatueScaleMessage::new);

    public static Consumer<Float> getValueSender(ValueAccessor valueAccessor) {
        return (Float value) -> {
            MessageSender.broadcast(new ServerboundStrawStatueScaleMessage(valueAccessor, value));
        };
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof StatueMenu statueMenu
                        && statueMenu.stillValid(context.player())) {
                    StrawStatue strawStatue = (StrawStatue) statueMenu.getEntity();
                    Rotations rotations = ServerboundStrawStatueScaleMessage.this.valueAccessor.setRotationsComponent(
                            strawStatue.getEntityPose(),
                            ServerboundStrawStatueScaleMessage.this.value);
                    strawStatue.setEntityPose(rotations);
                }
            }
        };
    }

    public enum ValueAccessor {
        ROTATION_X {
            @Override
            public Rotations setRotationsComponent(Rotations rotations, float value) {
                return new Rotations(value, rotations.y(), rotations.z());
            }

            @Override
            public float getRotationsComponent(Rotations rotations) {
                return rotations.x();
            }
        },
        ROTATION_Z {
            @Override
            public Rotations setRotationsComponent(Rotations rotations, float value) {
                return new Rotations(rotations.x(), rotations.y(), value);
            }

            @Override
            public float getRotationsComponent(Rotations rotations) {
                return rotations.z();
            }
        };

        public abstract Rotations setRotationsComponent(Rotations rotations, float value);

        public abstract float getRotationsComponent(Rotations rotations);
    }
}
