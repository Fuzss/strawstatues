package fuzs.strawstatues.network.client;

import com.mojang.authlib.GameProfile;
import fuzs.puzzlesapi.api.statues.v1.world.inventory.ArmorStandMenu;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class C2SStrawStatueOwnerMessage implements MessageV2<C2SStrawStatueOwnerMessage> {
    private String name;

    public C2SStrawStatueOwnerMessage() {

    }

    public C2SStrawStatueOwnerMessage(String name) {
        this.name = name;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
    }

    @Override
    public MessageHandler<C2SStrawStatueOwnerMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SStrawStatueOwnerMessage message, Player player, Object gameInstance) {
                if (player.containerMenu instanceof ArmorStandMenu menu && menu.stillValid(player)) {
                    String s = SharedConstants.filterText(message.name);
                    if (s.length() <= 16) {
                        StrawStatue statue = (StrawStatue) menu.getArmorStand();
                        if (s.isEmpty()) {
                            statue.verifyAndSetOwner(null);
                        } else {
                            statue.verifyAndSetOwner(new GameProfile(null, message.name));
                        }
                    }
                }
            }
        };
    }
}
