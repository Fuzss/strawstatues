package fuzs.strawstatues.api.network.client.data;

import fuzs.strawstatues.api.StatuesApi;
import fuzs.strawstatues.api.network.client.*;
import fuzs.strawstatues.api.world.inventory.ArmorStandHolder;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandPose;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandScreenType;
import fuzs.strawstatues.api.world.inventory.data.ArmorStandStyleOption;
import net.minecraft.nbt.CompoundTag;

public class NetworkDataSyncHandler implements DataSyncHandler {
    private final ArmorStandHolder holder;

    public NetworkDataSyncHandler(ArmorStandHolder holder) {
        this.holder = holder;
    }

    @Override
    public ArmorStandHolder getArmorStandHolder() {
        return this.holder;
    }

    @Override
    public void sendName(String name) {
        DataSyncHandler.setCustomArmorStandName(this.getArmorStand(), name);
        StatuesApi.NETWORK.sendToServer(new C2SArmorStandNameMessage(name));
    }

    @Override
    public void sendPose(ArmorStandPose pose) {
        pose.applyToEntity(this.getArmorStand());
        CompoundTag tag = new CompoundTag();
        pose.serializeAllPoses(tag);
        StatuesApi.NETWORK.sendToServer(new C2SArmorStandPoseMessage(tag));
    }

    @Override
    public void sendPosition(double posX, double posY, double posZ) {
        StatuesApi.NETWORK.sendToServer(new C2SArmorStandPositionMessage(posX, posY, posZ));
    }

    @Override
    public void sendRotation(float rotation) {
        StatuesApi.NETWORK.sendToServer(new C2SArmorStandRotationMessage(rotation));
    }

    @Override
    public void sendStyleOption(ArmorStandStyleOption styleOption, boolean value) {
        styleOption.setOption(this.getArmorStand(), value);
        StatuesApi.NETWORK.sendToServer(new C2SArmorStandStyleMessage(styleOption, value));
    }

    @Override
    public ArmorStandScreenType[] tabs() {
        return this.getArmorStandHolder().getDataProvider().getScreenTypes();
    }
}
