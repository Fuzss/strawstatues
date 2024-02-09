package fuzs.strawstatues.data;

import fuzs.puzzleslib.api.data.v1.AbstractRecipeProvider;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.function.Consumer;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModRegistry.STRAW_STATUE_ITEM.get())
                .define('#', Blocks.HAY_BLOCK)
                .define('X', Items.STICK)
                .pattern("XXX")
                .pattern(" X ")
                .pattern("X#X")
                .unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK))
                .save(exporter);
    }
}
