package fuzs.strawstatues.network.client;

import com.mojang.authlib.properties.PropertyMap;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Optional;

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
                    String s = StringUtil.filterText(message.name);
                    if (StringUtil.isValidPlayerName(s)) {
                        ResolvableProfile resolvableProfile;
                        if (s.isEmpty()) {
                            resolvableProfile = null;
                        } else {
                            resolvableProfile = new ResolvableProfile(Optional.of(s),
                                    Optional.empty(),
                                    new PropertyMap()
                            );
                        }
                        ((StrawStatue) menu.getArmorStand()).setOwner(resolvableProfile);
                    }
                }
            }
        };
    }
}
