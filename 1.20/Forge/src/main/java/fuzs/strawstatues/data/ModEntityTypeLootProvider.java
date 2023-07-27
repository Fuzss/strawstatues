package fuzs.strawstatues.data;

import fuzs.puzzleslib.api.data.v1.AbstractLootProvider;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModEntityTypeLootProvider extends AbstractLootProvider.EntityTypes {

    public ModEntityTypeLootProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    public void generate() {
        this.add(ModRegistry.STRAW_STATUE_ENTITY_TYPE.get(), LootTable.lootTable());
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> entityType) {
        return entityType == ModRegistry.STRAW_STATUE_ENTITY_TYPE.get() || super.canHaveLootTable(entityType);
    }
}
