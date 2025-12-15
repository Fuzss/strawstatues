package fuzs.strawstatues.core;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public interface Proxy {
    Proxy INSTANCE = ModLoaderEnvironment.INSTANCE.isClient() ? new ClientProxy() : new ServerProxy();

    StrawStatue createStrawStatue(EntityType<? extends StrawStatue> entityType, Level level);
}
