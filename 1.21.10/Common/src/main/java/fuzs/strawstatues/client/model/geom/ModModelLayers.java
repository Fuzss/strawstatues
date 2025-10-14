package fuzs.strawstatues.client.model.geom;

import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import fuzs.strawstatues.StrawStatues;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public class ModModelLayers {
    static final ModelLayerFactory MODEL_LAYERS = ModelLayerFactory.from(StrawStatues.MOD_ID);
    public static final ModelLayerLocation STRAW_STATUE = MODEL_LAYERS.registerModelLayer("straw_statue");
    public static final ArmorModelSet<ModelLayerLocation> STRAW_STATUE_ARMOR = MODEL_LAYERS.registerArmorSet(
            "straw_statue");
    public static final ModelLayerLocation STRAW_STATUE_SLIM = MODEL_LAYERS.registerModelLayer("straw_statue_slim");
    public static final ArmorModelSet<ModelLayerLocation> STRAW_STATUE_SLIM_ARMOR = MODEL_LAYERS.registerArmorSet(
            "straw_statue_slim");
    public static final ModelLayerLocation STRAW_STATUE_CAPE = MODEL_LAYERS.registerModelLayer("straw_statue", "cape");
    public static final ModelLayerLocation STRAW_STATUE_BABY = MODEL_LAYERS.registerModelLayer("straw_statue_baby");
    public static final ArmorModelSet<ModelLayerLocation> STRAW_STATUE_BABY_ARMOR = MODEL_LAYERS.registerArmorSet(
            "straw_statue_baby");
    public static final ModelLayerLocation STRAW_STATUE_BABY_SLIM = MODEL_LAYERS.registerModelLayer(
            "straw_statue_baby_slim");
    public static final ArmorModelSet<ModelLayerLocation> STRAW_STATUE_BABY_SLIM_ARMOR = MODEL_LAYERS.registerArmorSet(
            "straw_statue_baby_slim");
    public static final ModelLayerLocation STRAW_STATUE_BABY_CAPE = MODEL_LAYERS.registerModelLayer("straw_statue_baby", "cape");
}
