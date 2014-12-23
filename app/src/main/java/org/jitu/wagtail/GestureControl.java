package org.jitu.wagtail;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.Toast;

import java.util.ArrayList;

public class GestureControl implements GestureOverlayView.OnGesturePerformedListener {
    private MainActivity activity;
    private GestureLibrary library;

    public GestureControl(MainActivity activity) {
        this.activity = activity;
    }

    public void onCreate() {
        library = GestureLibraries.fromRawResource(activity, R.raw.tggt);
        if (library == null) {
            Toast.makeText(activity, "GestureLibraries.fromRawResource failed",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!library.load()) {
            Toast.makeText(activity, "library.load failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
        if (library == null) {
            return;
        }
        ArrayList<Prediction> predictions = library.recognize(gesture);
        if (predictions.isEmpty()) {
            return;
        }
        Prediction prediction = predictions.get(0);
        if (prediction.score > 1.0) {
            onGesture(prediction.name);
        }
    }

    private void onGesture(String name) {
        if ("up".equals(name)) {
            activity.moveCursorHome();
        } else if ("down".equals(name)) {
            activity.moveCursorEnd();
        } else if ("L2R".equals(name)) {
            activity.tabify();
        } else if ("R2L".equals(name)) {
            activity.untabify();
        }
    }
}
