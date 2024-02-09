package fuzs.strawstatues.data;

import fuzs.puzzleslib.api.data.v1.AbstractModelProvider;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void registerStatesAndModels() {
        this.basicItem(ModRegistry.STRAW_STATUE_ITEM.get());
    }
}
