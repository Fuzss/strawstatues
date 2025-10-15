package fuzs.strawstatues.world.inventory.data;

import fuzs.statuemenus.api.v1.world.inventory.data.StatueStyleOption;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.List;
import java.util.Optional;

public class StrawStatueStyleOptions {
    public static final StatueStyleOption<StrawStatue> SMALL = StatueStyleOption.create(StrawStatues.id("small"),
            StrawStatue::setBaby,
            StrawStatue::isBaby);
    public static final StatueStyleOption<StrawStatue> SLIM = new StatueStyleOption<StrawStatue>(StrawStatues.id("slim")) {
        @Override
        public void setOption(StrawStatue strawStatue, boolean value) {
            PlayerSkin.Patch oldSkinPatch = strawStatue.getSkinPatch();
            PlayerSkin.Patch newSkinPatch = new PlayerSkin.Patch(oldSkinPatch.body(),
                    oldSkinPatch.cape(),
                    oldSkinPatch.elytra(),
                    Optional.of(value ? PlayerModelType.SLIM : PlayerModelType.WIDE));
            strawStatue.setSkinPatch(newSkinPatch);
        }

        @Override
        public boolean getOption(StrawStatue strawStatue) {
            return strawStatue.getSkinPatch().model().orElse(StrawStatue.DEFAULT_SKIN.model()) == PlayerModelType.SLIM;
        }

        @Override
        public boolean mayEdit(Player player) {
            // TODO only enable if skin texture is overridden
//            return strawStatue.getSkinPatch().body().isPresent();
            return super.mayEdit(player);
        }
    };
    public static final StatueStyleOption<StrawStatue> PUSHABLE = StatueStyleOption.create(StrawStatues.id("pushable"),
            StrawStatue::setPushable,
            StrawStatue::isPushable);
    public static final StatueStyleOption<StrawStatue> DYNAMIC_PROFILE = StatueStyleOption.create(StrawStatues.id(
            "dynamic_profile"), StrawStatue::setDynamicProfile, StrawStatue::isDynamicProfile);
    public static final StatueStyleOption<StrawStatue> CROUCHING = StatueStyleOption.create(StrawStatues.id("crouching"),
            StrawStatue::setCrouching,
            Entity::isCrouching);
    public static final StatueStyleOption<StrawStatue> SEALED = StatueStyleOption.create(StrawStatues.id("sealed"),
            StrawStatue::setSealed,
            StrawStatue::isSealed);
    public static final List<StatueStyleOption<? super StrawStatue>> TYPES = List.of(SMALL,
            CROUCHING,
            PUSHABLE,
            StatueStyleOption.IMMOVABLE,
            StatueStyleOption.INVULNERABLE,
            DYNAMIC_PROFILE,
            SLIM,
            SEALED);

    public static void bootstrap() {
        StatueStyleOption.register(StrawStatueStyleOptions.SMALL);
        StatueStyleOption.register(StrawStatueStyleOptions.SLIM);
        StatueStyleOption.register(StrawStatueStyleOptions.PUSHABLE);
        StatueStyleOption.register(StrawStatueStyleOptions.DYNAMIC_PROFILE);
        StatueStyleOption.register(StrawStatueStyleOptions.CROUCHING);
        StatueStyleOption.register(StrawStatueStyleOptions.SEALED);
    }
}
