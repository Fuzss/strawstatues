package fuzs.strawstatues.proxy;

import fuzs.puzzleslib.core.DistTypeExecutor;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface Proxy {
    @SuppressWarnings("Convert2MethodRef")
    Proxy INSTANCE = DistTypeExecutor.getForDistType(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    void appendStrawStatueHoverText(List<Component> lines);
}
