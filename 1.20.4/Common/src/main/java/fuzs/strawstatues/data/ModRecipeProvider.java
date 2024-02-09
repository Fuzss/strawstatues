package fuzs.strawstatues.data;

import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.strawstatues.init.ModRegistry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModRegistry.STRAW_STATUE_ITEM.value())
                .define('#', Blocks.HAY_BLOCK)
                .define('X', Items.STICK)
                .pattern("XXX")
                .pattern(" X ")
                .pattern("X#X")
                .unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK))
                .save(recipeOutput);
    }
}
