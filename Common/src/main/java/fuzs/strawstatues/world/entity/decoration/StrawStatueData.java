package fuzs.strawstatues.world.entity.decoration;

import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import fuzs.strawstatues.api.world.inventory.data.PosePartMutator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class StrawStatueData {
    public static final ArmorStandScreenType MODEL_PARTS_SCREEN_TYPE = new ArmorStandScreenType("modelParts", new ItemStack(Items.YELLOW_WOOL));
    public static final ArmorStandScreenType STRAW_STATUE_STYLE_SCREEN_TYPE = new ArmorStandScreenType("style", new ItemStack(Items.PAINTING));
    public static final ArmorStandScreenType STRAW_STATUE_POSITION_SCREEN_TYPE = new ArmorStandScreenType("position", new ItemStack(Items.GRASS_BLOCK));
    public static final ArmorStandScreenType STRAW_STATUE_SCALE_SCREEN_TYPE = new ArmorStandScreenType("scale", new ItemStack(Items.HAY_BLOCK));
    public static final ArmorStandStyleOption SLIM_ARMS_STYLE_OPTION = new ArmorStandStyleOption() {

        @Override
        public String getName() {
            return "slimArms";
        }

        @Override
        public void setOption(ArmorStand armorStand, boolean setting) {
            ((StrawStatue) armorStand).setSlimArms(setting);
        }

        @Override
        public boolean getOption(ArmorStand armorStand) {
            return ((StrawStatue) armorStand).slimArms();
        }

        @Override
        public void toTag(CompoundTag tag, boolean currentValue) {
            tag.putBoolean(StrawStatue.SLIM_ARMS_KEY, currentValue);
        }
    };
    public static final ArmorStandStyleOption CROUCHING_STYLE_OPTION = new ArmorStandStyleOption() {

        @Override
        public String getName() {
            return "crouching";
        }

        @Override
        public void setOption(ArmorStand armorStand, boolean setting) {
            ((StrawStatue) armorStand).setCrouching(setting);
        }

        @Override
        public boolean getOption(ArmorStand armorStand) {
            return armorStand.isCrouching();
        }

        @Override
        public void toTag(CompoundTag tag, boolean currentValue) {
            tag.putBoolean(StrawStatue.CROUCHING_KEY, currentValue);
        }
    };
    public static final PosePartMutator CAPE_POSE_PART_MUTATOR = new PosePartMutator("cape", ArmorStandPose::getBodyPose, ArmorStandPose::withBodyPose, PosePartMutator.PosePartAxisRange.range(0.0F, 120.0F), PosePartMutator.PosePartAxisRange.range(-60.0F, 60.0F), PosePartMutator.PosePartAxisRange.range(-120.0, 120.0));

    private StrawStatueData() {

    }
}
