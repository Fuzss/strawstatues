package fuzs.strawstatues.network.client;

import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import fuzs.statuemenus.api.v1.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class C2SStrawStatueSetProfileMessage implements WritableMessage<C2SStrawStatueSetProfileMessage> {
    private final String profileName;
    private final boolean clearOfflinePlayerProfile;

    public C2SStrawStatueSetProfileMessage(String profileName, boolean clearOfflinePlayerProfile) {
        this.profileName = profileName;
        this.clearOfflinePlayerProfile = clearOfflinePlayerProfile;
    }

    public C2SStrawStatueSetProfileMessage(FriendlyByteBuf buf) {
        this.profileName = buf.readUtf();
        this.clearOfflinePlayerProfile = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.profileName);
        buf.writeBoolean(this.clearOfflinePlayerProfile);
    }

    @Override
    public MessageHandler<C2SStrawStatueSetProfileMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SStrawStatueSetProfileMessage message, Player player, Object gameInstance) {
                if (player.containerMenu instanceof ArmorStandMenu menu && menu.stillValid(player)) {
                    String s = StringUtil.filterText(message.profileName);
                    if (StringUtil.isValidPlayerName(s)) {
                        StrawStatue strawStatue = (StrawStatue) menu.getArmorStand();
                        if (!message.clearOfflinePlayerProfile ||
                                clearOfflinePlayerProfile(((MinecraftServer) gameInstance).getProfileCache(),
                                        strawStatue
                                )) {
                            ResolvableProfile resolvableProfile;
                            if (s.isEmpty()) {
                                resolvableProfile = null;
                            } else {
                                resolvableProfile = new ResolvableProfile(Optional.of(s), Optional.empty(),
                                        new PropertyMap()
                                );
                            }
                            strawStatue.setProfile(resolvableProfile);
                        }
                    }
                }
            }

            private static boolean clearOfflinePlayerProfile(@Nullable GameProfileCache gameProfileCache, StrawStatue strawStatue) {
                GameProfile gameProfile = strawStatue.getProfile().map(ResolvableProfile::gameProfile).orElse(null);
                if (gameProfile != null && isOfflinePlayerProfile(gameProfile)) {
                    return C2SStrawStatueSetProfileMessage.clearOfflinePlayerProfile(gameProfile.getName(),
                            gameProfileCache
                    );
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
