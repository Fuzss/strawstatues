package fuzs.strawstatues.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.decoration.ArmorStand;

public class StrawStatueRenderState extends AvatarRenderState {
    public float rotX;
    public float rotZ;
    public float wiggle;
    public Rotations headPose = ArmorStand.DEFAULT_HEAD_POSE;
    public Rotations bodyPose = ArmorStand.DEFAULT_BODY_POSE;
    public Rotations leftArmPose = ArmorStand.DEFAULT_LEFT_ARM_POSE;
    public Rotations rightArmPose = ArmorStand.DEFAULT_RIGHT_ARM_POSE;
    public Rotations leftLegPose = ArmorStand.DEFAULT_LEFT_LEG_POSE;
    public Rotations rightLegPose = ArmorStand.DEFAULT_RIGHT_LEG_POSE;
}
