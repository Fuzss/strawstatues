package fuzs.strawstatues.network.client;

import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandMenu;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

public class C2SStrawStatueModelPartMessage implements MessageV2<C2SStrawStatueModelPartMessage> {
    private PlayerModelPart modelPart;
    private boolean value;

    public C2SStrawStatueModelPartMessage() {

    }

    public C2SStrawStatueModelPartMessage(PlayerModelPart modelPart, boolean value) {
        this.modelPart = modelPart;
        this.value = value;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.modelPart);
        buf.writeBoolean(this.value);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.modelPart = buf.readEnum(PlayerModelPart.class);
        this.value = buf.readBoolean();
    }

    @Override
    public MessageHandler<C2SStrawStatueModelPartMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SStrawStatueModelPartMessage message, Player player, Object gameInstance) {
                if (player.containerMenu instanceof ArmorStandMenu menu && menu.stillValid(player)) {
                    ((StrawStatue) menu.getArmorStand()).setModelPart(message.modelPart, message.value);
                }
            }
        };
    }
}
