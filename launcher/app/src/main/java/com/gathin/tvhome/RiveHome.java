package com.gathin.tvhome;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import app.rive.runtime.kotlin.RiveAnimationView;
import app.rive.runtime.kotlin.core.Alignment;
import app.rive.runtime.kotlin.core.Direction;
import app.rive.runtime.kotlin.core.Fit;
import app.rive.runtime.kotlin.core.Loop;
import app.rive.runtime.kotlin.core.RendererType;
import app.rive.runtime.kotlin.core.Rive;

/**
 * Personal fork (rive_home): plays res/raw/homefx.riv, made with the Rive CLI in tv-fx/, over the wallpaper and under
 * the app list: sakura petals drifting down the right of the screen. It runs only while Home is visible. Any failure
 * (no native library, a file the runtime cannot read) leaves Home exactly as it was.
 */
final class RiveHome {
    private static final String TAG = "TvHome.Rive";
    private static final String ARTBOARD = "HomeFx", STATE_MACHINE = "Home";

    private final RiveAnimationView view;

    private RiveHome(RiveAnimationView view) {
        this.view = view;
    }

    /** Adds the petals to root just above the wallpaper, or returns null and changes nothing if Rive cannot run. */
    static RiveHome attach(Context ctx, FrameLayout root) {
        try {
            Rive.INSTANCE.init(ctx, RendererType.Rive);
            RiveAnimationView view = new RiveAnimationView(ctx, null);
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            view.setFocusable(false);
            // Index 1: over WallpaperPlayer's picture (index 0), under the header and the list.
            int index = Math.min(1, root.getChildCount());
            root.addView(view, index, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setRiveResource(R.raw.homefx, ARTBOARD, null, STATE_MACHINE, true, false,
                    Fit.FILL, Alignment.CENTER, Loop.AUTO);
            return new RiveHome(view);
        } catch (Throwable failure) {
            Log.w(TAG, "Rive petals off: " + failure);
            return null;
        }
    }

    void start() {
        try {
            view.play(Loop.AUTO, Direction.AUTO, true);
        } catch (Throwable ignored) {
        }
    }

    void stop() {
        try {
            view.pause();
        } catch (Throwable ignored) {
        }
    }
}
