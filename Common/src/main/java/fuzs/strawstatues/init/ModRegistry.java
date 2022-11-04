package fuzs.strawstatues.init;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.init.builder.ExtendedModMenuSupplier;
import fuzs.strawstatues.StrawStatues;
import fuzs.strawstatues.api.world.inventory.ArmorStandMenu;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import fuzs.strawstatues.world.item.StrawStatueItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class ModRegistry {
    private static final RegistryManager REGISTRY = CoreServices.FACTORIES.registration(StrawStatues.MOD_ID);
    public static final RegistryReference<Item> STRAW_STATUE_ITEM = REGISTRY.registerItem("straw_statue", () -> new StrawStatueItem(new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS)));
    public static final RegistryReference<EntityType<StrawStatue>> STRAW_STATUE_ENTITY_TYPE = REGISTRY.registerEntityTypeBuilder("straw_statue", () -> {
        return EntityType.Builder.of((EntityType<StrawStatue> entityType, Level level) -> new StrawStatue(entityType, level), MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10);
    });
    public static final RegistryReference<MenuType<ArmorStandMenu>> STRAW_STATUE_MENU_TYPE = REGISTRY.registerExtendedMenuTypeSupplier("straw_statue", () -> getStrawStatueMenuTypeSupplier());

    public static final EntityDataSerializer<Optional<GameProfile>> GAME_PROFILE_ENTITY_DATA_SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buffer, Optional<GameProfile> value) {
            buffer.writeBoolean(value.isPresent());
            value.ifPresent(gameProfile -> {
                if (!gameProfile.isComplete()) {
                    UUID uuid = Player.createPlayerUUID(gameProfile.getName());
                    gameProfile = new GameProfile(uuid, gameProfile.getName());
                }
                buffer.writeUUID(gameProfile.getId());
                buffer.writeUtf(gameProfile.getName());
                buffer.writeCollection(gameProfile.getProperties().values(), this::writeProperty);
            });
        }

        private void writeProperty(FriendlyByteBuf buffer, Property property) {
            buffer.writeUtf(property.getName());
            buffer.writeUtf(property.getValue());
            if (property.hasSignature()) {
                buffer.writeBoolean(true);
                buffer.writeUtf(property.getSignature());
            } else {
                buffer.writeBoolean(false);
            }
        }

        @Override
        public Optional<GameProfile> read(FriendlyByteBuf buffer) {
            if (!buffer.readBoolean()) return Optional.empty();
            GameProfile gameProfile = new GameProfile(buffer.readUUID(), buffer.readUtf(16));
            PropertyMap propertyMap = gameProfile.getProperties();
            buffer.readWithCount((friendlyByteBuf) -> {
                String string = friendlyByteBuf.readUtf();
                String string2 = friendlyByteBuf.readUtf();
                if (friendlyByteBuf.readBoolean()) {
                    String string3 = friendlyByteBuf.readUtf();
                    propertyMap.put(string, new Property(string, string2, string3));
                } else {
                    propertyMap.put(string, new Property(string, string2));
                }

            });
            return Optional.of(gameProfile);
        }

        @Override
        public Optional<GameProfile> copy(Optional<GameProfile> value) {
            return value;
        }
    };

    public static void touch() {
        EntityDataSerializers.registerSerializer(GAME_PROFILE_ENTITY_DATA_SERIALIZER);
    }

    private static ExtendedModMenuSupplier<ArmorStandMenu> getStrawStatueMenuTypeSupplier() {
        return (containerId, inventory, data) -> ArmorStandMenu.create(STRAW_STATUE_MENU_TYPE.get(), containerId, inventory, data);
    }
}
