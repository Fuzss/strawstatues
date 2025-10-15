package fuzs.strawstatues.network.client;

import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.world.inventory.StatueMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Optional;

public record ServerboundStrawStatueSkinPatchMessage(DataType dataType,
                                                     Optional<ClientAsset.ResourceTexture> resourceTexture) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueSkinPatchMessage> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.fromEnum(DataType.class),
            ServerboundStrawStatueSkinPatchMessage::dataType,
            ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional),
            ServerboundStrawStatueSkinPatchMessage::resourceTexture,
            ServerboundStrawStatueSkinPatchMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof StatueMenu statueMenu
                        && statueMenu.stillValid(context.player())) {
                    StrawStatue strawStatue = (StrawStatue) statueMenu.getEntity();
                    PlayerSkin.Patch skinPatch = ServerboundStrawStatueSkinPatchMessage.this.dataType.createSkinPatch(
                            strawStatue.getSkinPatch(),
                            ServerboundStrawStatueSkinPatchMessage.this.resourceTexture);
                    strawStatue.setSkinPatch(skinPatch);
                }
            }
        };
    }

    public enum DataType {
        SKIN {
            @Override
            public Optional<ClientAsset.ResourceTexture> getTexturePath(PlayerSkin.Patch patch) {
                return patch.body();
            }

            @Override
            public PlayerSkin.Patch createSkinPatch(PlayerSkin.Patch patch, Optional<ClientAsset.ResourceTexture> resourceTexture) {
                return PlayerSkin.Patch.create(resourceTexture,
                        patch.cape(),
                        patch.elytra(),
                        resourceTexture.isEmpty() ? Optional.empty() : patch.model());
            }
        },
        CAPE {
            @Override
            public Optional<ClientAsset.ResourceTexture> getTexturePath(PlayerSkin.Patch patch) {
                return patch.cape();
            }

            @Override
            public PlayerSkin.Patch createSkinPatch(PlayerSkin.Patch patch, Optional<ClientAsset.ResourceTexture> resourceTexture) {
                return PlayerSkin.Patch.create(patch.body(), resourceTexture, patch.elytra(), patch.model());
            }
        },
        ELYTRA {
            @Override
            public Optional<ClientAsset.ResourceTexture> getTexturePath(PlayerSkin.Patch patch) {
                return patch.elytra();
            }

            @Override
            public PlayerSkin.Patch createSkinPatch(PlayerSkin.Patch patch, Optional<ClientAsset.ResourceTexture> resourceTexture) {
                return PlayerSkin.Patch.create(patch.body(), patch.cape(), resourceTexture, patch.model());
            }
        };

        public abstract Optional<ClientAsset.ResourceTexture> getTexturePath(PlayerSkin.Patch patch);

        public abstract PlayerSkin.Patch createSkinPatch(PlayerSkin.Patch patch, Optional<ClientAsset.ResourceTexture> resourceTexture);
    }
}
