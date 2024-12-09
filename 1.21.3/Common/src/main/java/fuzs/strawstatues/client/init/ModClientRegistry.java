package fuzs.strawstatues.client.init;

import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import fuzs.strawstatues.StrawStatues;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ModClientRegistry {
    static final ModelLayerFactory REGISTRY = ModelLayerFactory.from(StrawStatues.MOD_ID);
    public static final ModelLayerLocation STRAW_STATUE = REGISTRY.register("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_INNER_ARMOR = REGISTRY.registerInnerArmor("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_OUTER_ARMOR = REGISTRY.registerOuterArmor("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_CAPE = REGISTRY.register("straw_statue", "cape");
}
