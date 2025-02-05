package fuzs.strawstatues.client.init;

import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import fuzs.strawstatues.StrawStatues;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ModClientRegistry {
    static final ModelLayerFactory MODEL_LAYERS = ModelLayerFactory.from(StrawStatues.MOD_ID);
    public static final ModelLayerLocation STRAW_STATUE = MODEL_LAYERS.register("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_INNER_ARMOR = MODEL_LAYERS.registerInnerArmor("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_OUTER_ARMOR = MODEL_LAYERS.registerOuterArmor("straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_SLIM = MODEL_LAYERS.register("straw_statue_slim");
    public static final ModelLayerLocation STRAW_STATUE_SLIM_INNER_ARMOR = MODEL_LAYERS.registerInnerArmor(
            "straw_statue_slim");
    public static final ModelLayerLocation STRAW_STATUE_SLIM_OUTER_ARMOR = MODEL_LAYERS.registerOuterArmor(
            "straw_statue_slim");
    public static final ModelLayerLocation STRAW_STATUE_BABY = MODEL_LAYERS.register("straw_statue_baby");
    public static final ModelLayerLocation STRAW_STATUE_BABY_INNER_ARMOR = MODEL_LAYERS.registerInnerArmor(
            "straw_statue_baby");
    public static final ModelLayerLocation STRAW_STATUE_BABY_OUTER_ARMOR = MODEL_LAYERS.registerOuterArmor(
            "straw_statue_baby");
    public static final ModelLayerLocation STRAW_STATUE_BABY_SLIM = MODEL_LAYERS.register("straw_statue_baby_slim");
    public static final ModelLayerLocation STRAW_STATUE_BABY_SLIM_INNER_ARMOR = MODEL_LAYERS.registerInnerArmor(
            "straw_statue_baby_slim");
    public static final ModelLayerLocation STRAW_STATUE_BABY_SLIM_OUTER_ARMOR = MODEL_LAYERS.registerOuterArmor(
            "straw_statue_baby_slim");
    public static final ModelLayerLocation STRAW_STATUE_CAPE = MODEL_LAYERS.register("straw_statue", "cape");
    public static final ModelLayerLocation STRAW_STATUE_BABY_CAPE = MODEL_LAYERS.register("straw_statue_baby", "cape");
}
