package fuzs.strawstatues.api.world.inventory;

import fuzs.strawstatues.api.world.entity.decoration.ArmorStandDataProvider;
import net.minecraft.world.entity.decoration.ArmorStand;

public interface ArmorStandHolder {

    ArmorStand getArmorStand();

    default ArmorStandDataProvider getDataProvider() {
        return this.getArmorStand() instanceof ArmorStandDataProvider dataProvider ? dataProvider : ArmorStandDataProvider.INSTANCE;
    }
}
