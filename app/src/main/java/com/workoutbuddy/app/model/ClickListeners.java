package com.workoutbuddy.app.model;

import android.view.View.OnClickListener;
import android.view.View;
import javax.annotation.Nullable;

/**
 * Class to handle double click and single clicks in the same object.
 */
public abstract class ClickListeners implements OnClickListener {

    // Maximum amount of time to count as a double click.
    private Long maxTimeForDoubleClick;

    // Variable to keep track when previous click took place.
    private long firsClickTime;

    public ClickListeners(@Nullable Long maxTimeForDoubleClick) {
        // If null, default to 300
        this.maxTimeForDoubleClick = maxTimeForDoubleClick != null ? maxTimeForDoubleClick : 300;
        firsClickTime = 0;
    }

    /**
     * Split the action taken depending on the time previous click took place.
     */
    @Override
    public void onClick(View v) {
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - firsClickTime) < maxTimeForDoubleClick)
            onDoubleClick();
        else
            onSingleClick();
        // Rest last click
        firsClickTime = System.currentTimeMillis();
    }

    /**
     * Method to override with actions to do when one click occurs.
     */
    public abstract void onSingleClick();

    /**
     * Method to override with actions to do when one two clicks occur within the
     * max span of time to qualify as a double click.
     */
    public abstract void onDoubleClick();
}