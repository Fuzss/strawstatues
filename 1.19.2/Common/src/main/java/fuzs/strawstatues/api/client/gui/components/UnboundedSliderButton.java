package fuzs.strawstatues.api.client.gui.components;

public interface UnboundedSliderButton {

    default boolean isDirty() {
        return false;
    }

    default void clearDirty() {

    }
}
