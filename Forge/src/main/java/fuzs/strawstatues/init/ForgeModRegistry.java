package fuzs.strawstatues.init;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.init.RegistryReference;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ForgeModRegistry {
    public static final RegistryReference<EntityDataSerializer<Optional<GameProfile>>> GAME_PROFILE_ENTITY_DATA_SERIALIZER = ModRegistry.REGISTRY.register(
            ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "game_profile",
            () -> ModRegistry.GAME_PROFILE_ENTITY_DATA_SERIALIZER
    );

    public static void touch() {
        // NO-OP
    }
}
