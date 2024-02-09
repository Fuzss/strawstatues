package fuzs.strawstatues.network.client;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class C2SStrawStatueOwnerMessage implements WritableMessage<C2SStrawStatueOwnerMessage> {
    private final String name;

    public C2SStrawStatueOwnerMessage(String name) {
        this.name = name;
    }

    public C2SStrawStatueOwnerMessage(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
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
                            statue.setOwner(null);
                        } else {
                            SkullBlockEntity.fetchGameProfile(message.name).thenApply(gameProfile -> gameProfile.orElse(new GameProfile(
                                    Util.NIL_UUID, message.name))).thenAccept(statue::setOwner);
                        }
                    }
                }
            }
        };
    }
}
