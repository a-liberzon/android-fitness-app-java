package com.workoutbuddy.app.model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.databinding.FragmentSettingsBinding;

import java.util.List;

/**
 * Class to handle themes.
 */
public class ThemeManager {

    private final Context context;
    private final boolean nightModeOn;
    private final Resources resources;
    private final Resources.Theme theme;
    private final int contrastColor;
    private final int primaryColor;

    // Define contrast color here for day theme
    private static final int DAY_CONTRAST_COLOR = R.color.black;

    // Define contrast color here for night theme
    private static final int NIGHT_CONTRAST_COLOR = R.color.white;

    public ThemeManager(Context context) {
        this.context = context;
        nightModeOn = isNightModeOn(context);
        resources = context.getResources();
        theme = context.getTheme();
        contrastColor = ContextCompat.getColor(
                context, nightModeOn ? NIGHT_CONTRAST_COLOR : DAY_CONTRAST_COLOR);
        primaryColor = ContextCompat.getColor(context, nightModeOn ?
                R.color.my_dark_primary : R.color.my_light_primary);

    }

    /**
     * Check if night mode is on or not.
     */
    public boolean isNightModeOn(Context context) {
        return (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Get contrast color.
     */
    public int getContrastColor() {
        return contrastColor;
    }

    /**
     * Static retrieval of contrast color.
     */
    public static int getContrastColor(Context context) {
        boolean nightModeOn = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        return ContextCompat.getColor(
                context, nightModeOn ? NIGHT_CONTRAST_COLOR : DAY_CONTRAST_COLOR);
    }

    /**
     * Sets toolbar's images and text to contrast day/night theme for MenuItems.
     * @param toolbar The toolbar for which to set the theme
     */
    public void setToolbarTheme(Toolbar toolbar, int navIconId) {
        // Set icon to contrast
        VectorDrawableCompat drawable = VectorDrawableCompat.create(resources, navIconId, theme);
        drawable.setColorFilter(contrastColor, PorterDuff.Mode.SRC_IN);

        // Configure text color and size
        toolbar.setTitleTextAppearance(context, R.style.Toolbar_TitleText);
        toolbar.setTitleTextColor(contrastColor);
        toolbar.setNavigationIcon(drawable);

        // Set the overflow icon
        int id = R.drawable.ic_menu_options;
        VectorDrawableCompat overflowDrawable = VectorDrawableCompat.create(resources, id, theme);
        overflowDrawable.setColorFilter(contrastColor, PorterDuff.Mode.SRC_IN);
        toolbar.setOverflowIcon(overflowDrawable);
    }

    /**
     * Sets the contrast color theme for the drawer header.
     */
    public void setDrawerHeaderTheme(View drawerHeaderView) {
        // Get items
        TextView userTextView = drawerHeaderView.findViewById(R.id.userTextView);
        TextView emailTextView = drawerHeaderView.findViewById(R.id.emailTextView);

        // Set contrast colors for email and user
        userTextView.setTextColor(contrastColor);
        emailTextView.setTextColor(contrastColor);
    }

    /**
     * Set the color theme for all buttons in settings.
     */
    public void setThemeForSettingsButtons(FragmentSettingsBinding binding) {
        binding.signOutButton.setBackgroundColor(contrastColor);
        binding.radioDarkTheme.setButtonTintList(ColorStateList.valueOf(contrastColor));
        binding.radioLightTheme.setButtonTintList(ColorStateList.valueOf(contrastColor));
        binding.radioAutoTheme.setButtonTintList(ColorStateList.valueOf(contrastColor));
        binding.radioImperial.setButtonTintList(ColorStateList.valueOf(contrastColor));
        binding.radioMetric.setButtonTintList(ColorStateList.valueOf(contrastColor));
    }

    /**
     * Set FriendAdapter holder theme.
     */
    public void setFriendTheme(FriendsAdapter.FriendViewHolder holder, View itemView) {
        // Set up button
        holder.viewWorkoutsButton.setBackgroundColor(contrastColor);

        // Set up border
        int border = R.drawable.friend_border;
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, border);
        drawable.setStroke(3, contrastColor);
        itemView.findViewById(R.id.friend_box).setBackground(drawable);

        // Set up friend border
        int drawableId = R.drawable.circle_background;
        Drawable image = ContextCompat.getDrawable(context, drawableId);
        image.setColorFilter(contrastColor, PorterDuff.Mode.SRC_IN);
        (itemView.findViewById(R.id.friend_image)).setBackground(image);
    }

    /**
     * Method to set the color of a floating button to contrast theme.
     */
    public void setFloatingButtonTheme(FloatingActionButton button, int drawableId) {
        // Set background tint color
        button.setBackgroundTintList(ColorStateList.valueOf(contrastColor));

        // Set drawable color
        VectorDrawableCompat drawable = VectorDrawableCompat.create(resources, drawableId, theme);
        drawable.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        button.setImageDrawable(drawable);
    }

    /**
     * Method to select color for alert dialog buttons.
     */
    public void configureDialogButtons(Button positiveButton, Button negativeButton) {
        positiveButton.setTextColor(contrastColor);
        negativeButton.setTextColor(contrastColor);
    }

    /**
     * Set the color theme for all buttons in settings.
     */
    public void setThemeForRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                radioButton.setButtonTintList(ColorStateList.valueOf(contrastColor));
            }
        }
    }

    /**
     * Set the color theme for all checkboxes.
     */
    public void setThemeForCheckBoxContainer(List<CheckBox> checkBoxes) {
        checkBoxes.forEach(checkBox -> checkBox.setButtonTintList(ColorStateList.valueOf(contrastColor)));
    }

    /**
     * Configure text for overflow menu.
     */
    public void configureOverFlowText(Menu menu, int item) {
        MenuItem settingsItem = menu.findItem(item);
        boolean nightModeOn = ((context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        int color = nightModeOn ? R.color.white : R.color.black;
        SpannableString spannableString = new SpannableString(settingsItem.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)),
                0, spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        settingsItem.setTitle(spannableString);
    }
}
