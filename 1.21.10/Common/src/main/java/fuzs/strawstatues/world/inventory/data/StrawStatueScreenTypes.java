package fuzs.strawstatues.world.inventory.data;

import fuzs.statuemenus.api.v1.world.inventory.data.StatueScreenType;
import fuzs.strawstatues.StrawStatues;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class StrawStatueScreenTypes {
    public static final StatueScreenType ROTATIONS = new StatueScreenType(StrawStatues.id("rotations"),
            new ItemStack(Items.COMPASS));
    public static final StatueScreenType STYLE = new StatueScreenType(StrawStatues.id("style"),
            new ItemStack(Items.PAINTING));
    public static final StatueScreenType POSITION = new StatueScreenType(StrawStatues.id("position"),
            new ItemStack(Items.GRASS_BLOCK));
    public static final StatueScreenType MODEL_PARTS = new StatueScreenType(StrawStatues.id("model_parts"),
            new ItemStack(Items.YELLOW_WOOL));
    public static final StatueScreenType SCALE = new StatueScreenType(StrawStatues.id("scale"),
            new ItemStack(Items.HAY_BLOCK));
    public static final List<StatueScreenType> TYPES = List.of(ROTATIONS,
            StatueScreenType.POSES,
            STYLE,
            MODEL_PARTS,
            POSITION,
            SCALE,
            StatueScreenType.EQUIPMENT);
}
