package fuzs.strawstatues.api.network.client.data;

import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CommandDataSyncHandler implements DataSyncHandler {
    @Nullable
    static ArmorStandScreenType lastType;

    private final ArmorStand armorStand;
    private ArmorStandPose lastSyncedPose;

    public CommandDataSyncHandler(ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.lastSyncedPose = ArmorStandPose.fromEntity(armorStand);
    }

    @Override
    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    @Override
    public void sendName(String name) {
        if (!this.testPermissionLevel()) return;
        DataSyncHandler.super.sendName(name);
        CompoundTag tag = new CompoundTag();
        tag.putString("CustomName", Component.Serializer.toJson(new TextComponent(name)));
        this.sendCommand(tag);
    }

    @Override
    public void sendPose(ArmorStandPose currentPose) {
        if (!this.testPermissionLevel()) return;
        DataSyncHandler.super.sendPose(currentPose);
        // split this into multiple chat messages as the client chat field has a very low character limit
        this.sendPosePart(currentPose::serializeBodyPoses, this.lastSyncedPose);
        this.sendPosePart(currentPose::serializeArmPoses, this.lastSyncedPose);
        this.sendPosePart(currentPose::serializeLegPoses, this.lastSyncedPose);
        this.lastSyncedPose = currentPose;
    }

    private void sendPosePart(BiPredicate<CompoundTag, ArmorStandPose> dataWriter, ArmorStandPose lastSyncedPose) {
        CompoundTag tag = new CompoundTag();
        if (dataWriter.test(tag, lastSyncedPose)) {
            CompoundTag tag1 = new CompoundTag();
            tag1.put("Pose", tag);
            this.sendCommand(tag1);
        }
    }

    @Override
    public void sendPosition(double posX, double posY, double posZ) {
        if (!this.testPermissionLevel()) return;
        DataSyncHandler.super.sendPosition(posX, posY, posZ);
        ListTag listTag = new ListTag();
        listTag.add(DoubleTag.valueOf(posX));
        listTag.add(DoubleTag.valueOf(posY));
        listTag.add(DoubleTag.valueOf(posZ));
        CompoundTag tag = new CompoundTag();
        tag.put("Pos", listTag);
        this.sendCommand(tag);

    }

    @Override
    public void sendRotation(float rotation) {
        if (!this.testPermissionLevel()) return;
        DataSyncHandler.super.sendRotation(rotation);
        ListTag listTag = new ListTag();
        listTag.add(FloatTag.valueOf(rotation));
        CompoundTag tag = new CompoundTag();
        tag.put("Rotation", listTag);
        this.sendCommand(tag);
    }

    @Override
    public void sendStyleOption(ArmorStandStyleOption styleOption, boolean value) {
        if (!this.testPermissionLevel()) return;
        DataSyncHandler.super.sendStyleOption(styleOption, value);
        CompoundTag tag = new CompoundTag();
        styleOption.toTag(tag, value);
        this.sendCommand(tag);
    }

    @Override
    public ArmorStandScreenType[] tabs() {
        return Stream.of(this.getDataProvider().getScreenTypes()).filter(Predicate.not(ArmorStandScreenType::requiresServer)).toArray(ArmorStandScreenType[]::new);
    }

    @Override
    public Optional<ArmorStandScreenType> getLastType() {
        List<ArmorStandScreenType> screenTypes = Arrays.asList(this.getDataProvider().getScreenTypes());
        return Optional.ofNullable(lastType).filter(screenTypes::contains).filter(Predicate.not(ArmorStandScreenType::requiresServer));
    }

    @Override
    public void setLastType(ArmorStandScreenType lastType) {
        CommandDataSyncHandler.lastType = lastType;
    }

    private boolean testPermissionLevel() {
        Player player = Minecraft.getInstance().player;
        if (!player.hasPermissions(2)) {
            player.displayClientMessage(new TranslatableComponent("armorstatues.screen.noPermission").withStyle(ChatFormatting.RED), false);
            return false;
        }
        return true;
    }

    private void sendCommand(CompoundTag tag) {
        Minecraft.getInstance().player.chat("data merge entity %s %s".formatted(this.getArmorStand().getStringUUID(), tag.getAsString()));
    }
}
