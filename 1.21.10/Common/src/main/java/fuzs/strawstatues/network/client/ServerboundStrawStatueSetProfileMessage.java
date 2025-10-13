package fuzs.strawstatues.network.client;

import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record ServerboundStrawStatueSetProfileMessage(String profileName,
                                                      boolean clearOfflinePlayerProfile) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundStrawStatueSetProfileMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ServerboundStrawStatueSetProfileMessage::profileName,
            ByteBufCodecs.BOOL,
            ServerboundStrawStatueSetProfileMessage::clearOfflinePlayerProfile,
            ServerboundStrawStatueSetProfileMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof ArmorStandMenu menu &&
                        menu.stillValid(context.player())) {
                    String s = StringUtil.filterText(ServerboundStrawStatueSetProfileMessage.this.profileName);
                    if (StringUtil.isValidPlayerName(s)) {
                        StrawStatue strawStatue = (StrawStatue) menu.getArmorStand();
                        if (!ServerboundStrawStatueSetProfileMessage.this.clearOfflinePlayerProfile ||
                                clearOfflinePlayerProfile(context.server().getProfileCache(), strawStatue)) {
                            ResolvableProfile resolvableProfile;
                            if (s.isEmpty()) {
                                resolvableProfile = null;
                            } else {
                                resolvableProfile = new ResolvableProfile(Optional.of(s),
                                        Optional.empty(),
                                        new PropertyMap());
                            }
                            strawStatue.setProfile(resolvableProfile);
                        }
                    }
                }
            }

            static boolean clearOfflinePlayerProfile(@Nullable GameProfileCache gameProfileCache, StrawStatue strawStatue) {
                GameProfile gameProfile = strawStatue.getProfile().map(ResolvableProfile::gameProfile).orElse(null);
                if (gameProfile != null && isOfflinePlayerProfile(gameProfile)) {
                    return ServerboundStrawStatueSetProfileMessage.clearOfflinePlayerProfile(gameProfile.getName(),
                            gameProfileCache);
                } else {
                    return false;
                }
            }
        };
    }

    public static boolean isOfflinePlayerProfile(GameProfile gameProfile) {
        return UUIDUtil.createOfflinePlayerUUID(gameProfile.getName()).equals(gameProfile.getId());
    }

    public static boolean clearOfflinePlayerProfile(String profileName, @Nullable GameProfileCache gameProfileCache) {
        profileName = profileName.toLowerCase(Locale.ROOT);
        boolean hasClearedProfile = false;
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCache = SkullBlockEntity.profileCacheByName;
        if (profileCache != null && profileCache.getIfPresent(profileName) != null) {
            profileCache.invalidate(profileName);
            hasClearedProfile = true;
        }
        if (gameProfileCache != null && gameProfileCache.profilesByName.containsKey(profileName)) {
            gameProfileCache.profilesByName.remove(profileName);
            hasClearedProfile = true;
        }

        return hasClearedProfile;
    }
}
