package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.network.Message;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class C2SStrawStatueScaleMessage implements Message<C2SStrawStatueScaleMessage> {
    private float scale;

    public C2SStrawStatueScaleMessage() {

    }

    public C2SStrawStatueScaleMessage(float scale) {
        this.scale = scale;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.scale);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.scale = buf.readFloat();
    }

    @Override
    public MessageHandler<C2SStrawStatueScaleMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SStrawStatueScaleMessage message, Player player, Object gameInstance) {
                if (player.containerMenu instanceof ArmorStandMenu menu && menu.stillValid(player)) {
                    ((StrawStatue) menu.getArmorStand()).setModelScale(message.scale);
                }
            }
        };
    }
}
