package fuzs.strawstatues.network.client;

import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandMenu;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class C2SStrawStatueScaleMessage implements MessageV2<C2SStrawStatueScaleMessage> {
    private ScaleDataType type;
    private float value;

    public C2SStrawStatueScaleMessage() {

    }

    private C2SStrawStatueScaleMessage(ScaleDataType type, float value) {
        this.type = type;
        this.value = value;
    }

    public static void sendScale(float value) {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(ScaleDataType.SCALE, value));
    }

    public static void sendRotationX(float value) {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(ScaleDataType.ROTATION_X, value));
    }

    public static void sendRotationZ(float value) {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(ScaleDataType.ROTATION_Z, value));
    }

    public static void sendReset() {
        StrawStatues.NETWORK.sendToServer(new C2SStrawStatueScaleMessage(ScaleDataType.RESET, -1.0F));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.type);
        buf.writeFloat(this.value);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.type = buf.readEnum(ScaleDataType.class);
        this.value = buf.readFloat();
    }

    @Override
    public MessageHandler<C2SStrawStatueScaleMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SStrawStatueScaleMessage message, Player player, Object gameInstance) {
                if (player.containerMenu instanceof ArmorStandMenu menu && menu.stillValid(player)) {
                    message.type.consumer.accept((StrawStatue) menu.getArmorStand(), message.value);
                }
            }
        };
    }

    public enum ScaleDataType {
        SCALE(StrawStatue::setEntityScale), ROTATION_X(StrawStatue::setEntityXRotation), ROTATION_Z(StrawStatue::setEntityZRotation), RESET((strawStatue, value) -> {
            strawStatue.setEntityScale(StrawStatue.DEFAULT_ENTITY_SCALE);
            strawStatue.setEntityRotations(StrawStatue.DEFAULT_ENTITY_ROTATIONS.getX(), StrawStatue.DEFAULT_ENTITY_ROTATIONS.getZ());
        });

        public final BiConsumer<StrawStatue, Float> consumer;

        ScaleDataType(BiConsumer<StrawStatue, Float> consumer) {
            this.consumer = consumer;
        }
    }
}
