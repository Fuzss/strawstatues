package fuzs.strawstatues.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;

public class StrawStatueRenderState extends ArmorStandRenderState {
    public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
    public float capeFlap;
    public float capeLean;
    public float capeLean2;
    public boolean showHat = true;
    public boolean showJacket = true;
    public boolean showLeftPants = true;
    public boolean showRightPants = true;
    public boolean showLeftSleeve = true;
    public boolean showRightSleeve = true;
    public boolean showCape = true;
    public String name = "Steve";
    public float rotationZ;
    public float rotationX;
    public boolean slimArms;
}
