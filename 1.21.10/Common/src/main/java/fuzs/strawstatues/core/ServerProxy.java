package fuzs.strawstatues.core;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ServerProxy implements Proxy {

    @Override
    public StrawStatue createStrawStatue(EntityType<? extends StrawStatue> entityType, Level level) {
        return new StrawStatue(entityType, level);
    }
}
