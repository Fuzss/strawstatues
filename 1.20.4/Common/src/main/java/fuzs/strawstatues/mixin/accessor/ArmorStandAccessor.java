package fuzs.strawstatues.mixin.accessor;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandAccessor {

    @Invoker("brokenByAnything")
    void strawstatues$callBrokenByAnything(DamageSource damageSource);

    @Invoker("causeDamage")
    void strawstatues$callCauseDamage(DamageSource damageSource, float amount);
}
