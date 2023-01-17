package fuzs.strawstatues;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.strawstatues.api.ArmorStatuesApi;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class StrawStatuesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new ArmorStatuesApi());
        CoreServices.FACTORIES.modConstructor(StrawStatues.MOD_ID).accept(new StrawStatues());
        registerHandlers();
    }

    private static void registerHandlers() {
        UseEntityCallback.EVENT.register((Player player, Level level, InteractionHand interactionHand, Entity target, EntityHitResult entityHitResult) -> {
            // this callback runs in two places, one runs only for armor stands and is hit location aware, that's the one we need
            if (entityHitResult == null) return InteractionResult.PASS;
            Vec3 vec3 = entityHitResult.getLocation().subtract(target.getX(), target.getY(), target.getZ());
            return StrawStatue.onEntityInteract(player, level, interactionHand, target, vec3).orElse(InteractionResult.PASS);
        });
    }
}
