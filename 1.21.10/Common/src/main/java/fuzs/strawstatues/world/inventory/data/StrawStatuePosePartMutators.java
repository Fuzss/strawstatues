package fuzs.strawstatues.world.inventory.data;

import fuzs.statuemenus.api.v1.world.inventory.data.PosePartMutator;
import fuzs.statuemenus.api.v1.world.inventory.data.StatuePose;
import fuzs.strawstatues.StrawStatues;

import java.util.List;

public class StrawStatuePosePartMutators {
    public static final PosePartMutator CAPE = new PosePartMutator(StrawStatues.id("cape"),
            StatuePose::getBodyPose,
            StatuePose::withBodyPose,
            PosePartMutator.PosePartAxisRange.range(0.0F, 120.0F),
            PosePartMutator.PosePartAxisRange.range(-60.0F, 60.0F),
            PosePartMutator.PosePartAxisRange.range(-120.0, 120.0));
    public static final List<PosePartMutator> TYPES = List.of(PosePartMutator.HEAD,
            CAPE,
            PosePartMutator.RIGHT_ARM,
            PosePartMutator.LEFT_ARM,
            PosePartMutator.RIGHT_LEG,
            PosePartMutator.LEFT_LEG);
}
