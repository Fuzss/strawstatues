package fuzs.strawstatues.world.inventory.data;

import fuzs.statuemenus.api.v1.world.inventory.data.StatueStyleOption;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;

import java.util.List;

public class StrawStatueStyleOptions {
    public static final StatueStyleOption<StrawStatue> SMALL = StatueStyleOption.create(StrawStatues.id("small"),
            StrawStatue::setBaby,
            StrawStatue::isBaby);
    public static final StatueStyleOption<StrawStatue> PUSHABLE = StatueStyleOption.create(StrawStatues.id("pushable"),
            StrawStatue::setPushable,
            StrawStatue::isPushable);
    public static final StatueStyleOption<StrawStatue> DYNAMIC_PROFILE = StatueStyleOption.create(StrawStatues.id(
            "dynamic_profile"), StrawStatue::setDynamic, StrawStatue::isDynamic);
    public static final StatueStyleOption<StrawStatue> IMMOVABLE = StatueStyleOption.create(StrawStatues.id("immovable"),
            Mannequin::setImmovable,
            Mannequin::getImmovable);
    public static final StatueStyleOption<StrawStatue> CROUCHING = StatueStyleOption.create(StrawStatues.id("crouching"),
            StrawStatue::setCrouching,
            Entity::isCrouching);
    public static final StatueStyleOption<StrawStatue> SEALED = StatueStyleOption.create(StrawStatues.id("sealed"),
            StrawStatue::setSealed,
            StrawStatue::isSealed);
    public static final List<StatueStyleOption<? super StrawStatue>> TYPES = List.of(SMALL,
            CROUCHING,
            PUSHABLE,
            DYNAMIC_PROFILE,
            StatueStyleOption.NO_GRAVITY,
            StatueStyleOption.INVULNERABLE,
            SEALED);

    public static void bootstrap() {
        StatueStyleOption.register(StrawStatueStyleOptions.SMALL);
        StatueStyleOption.register(StrawStatueStyleOptions.PUSHABLE);
        StatueStyleOption.register(StrawStatueStyleOptions.DYNAMIC_PROFILE);
        StatueStyleOption.register(StrawStatueStyleOptions.CROUCHING);
        StatueStyleOption.register(StrawStatueStyleOptions.SEALED);
    }
}
