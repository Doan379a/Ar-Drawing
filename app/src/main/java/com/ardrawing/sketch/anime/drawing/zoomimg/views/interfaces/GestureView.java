package com.ardrawing.sketch.anime.drawing.zoomimg.views.interfaces;

import androidx.annotation.NonNull;

import com.ardrawing.sketch.anime.drawing.zoomimg.GestureController;

/**
 * Common interface for all Gesture* views.
 * <p>
 * All classes implementing this interface should be descendants of {@link android.view.View}.
 */
public interface GestureView {

    /**
     * Returns {@link GestureController} which is a main engine for all gestures interactions.
     * <p>
     * Use it to apply settings, access and modify image state and so on.
     *
     * @return {@link GestureController}.
     */
    @NonNull
    GestureController getController();

}
