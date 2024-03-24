package fuzs.strawstatues.init;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.strawstatues.StrawStatues;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ForgeModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(StrawStatues.MOD_ID);
    public static final RegistryReference<EntityDataSerializer<Optional<GameProfile>>> GAME_PROFILE_ENTITY_DATA_SERIALIZER = REGISTRY.register(
            ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "game_profile", () -> ModRegistry.GAME_PROFILE_ENTITY_DATA_SERIALIZER);

    public static void touch() {

    }
}
