package com.workoutbuddy.app.model;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.ConnectionsFragment;

import java.util.List;

/**
 * An adapter is a component that allows dynamically setting the contents of the RecyclerView.
 * It acts as a bridge between the data and the RecyclerView's GUI.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private List<Friend> friendsList;
    private ConnectionsFragment connectionsFragment;
    private ThemeManager themeManager;
    private View itemView;
    private Context context;

    public FriendsAdapter(List<Friend> friendsList, ConnectionsFragment connectionsFragment) {
        this.friendsList = friendsList;
        this.connectionsFragment = connectionsFragment;
    }
    /**
     * Method inflates the RecyclerView Adapter.
     */
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        context = parent.getContext();
        themeManager = new ThemeManager(context);
        itemView = inflater.inflate(R.layout.fragment_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    /**
     * Method inflates the RecyclerView Adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);

        // Bind the data to the views in fragment_friend.xml
        holder.usernameTextView.setText(friend.getUsername());
        holder.workoutCountTextView.setText("Workouts: " + friend.getNumWorkouts());
        holder.followerCountTextView.setText("Followers: "+friend.getNumFollowers());

        // Set image to friend's image or default if no image
        int friendImage = friend.getImageResourceId();
        holder.friendPic.setImageResource(friendImage != 0 ? friendImage : R.drawable.icon_friend);

        // Update pin icon based on pinned state
        holder.pinIcon.setImageResource(
                friend.isPinned() ? R.drawable.ic_pin_pinned : R.drawable.ic_pin_unpinned);

        // Set button to go to friend's workouts
        holder.viewWorkoutsButton.setOnClickListener(view -> {
            connectionsFragment.goToFriendWorkouts(friend, view.getContext());
        });

        // Set theme for holders
        themeManager.setFriendTheme(holder, itemView);

        // If clicking on recycler view hide the keyboard and search view
        holder.itemView.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                connectionsFragment.hideKeyboard();
                connectionsFragment.collapseSearchResults();
            }
        });
    }

    /**
     * Method to tell recycle how many views to create
     */
    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    /**
     * Create class that extends Recycler view
     */
    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView workoutCountTextView;
        TextView followerCountTextView;
        Button viewWorkoutsButton;
        ImageView pinIcon;
        ImageView friendPic;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.friend_name);
            workoutCountTextView = itemView.findViewById(R.id.friend_workout_count);
            followerCountTextView = itemView.findViewById(R.id.friend_follower_count);
            viewWorkoutsButton = itemView.findViewById(R.id.friend_workouts_button);
            pinIcon = itemView.findViewById(R.id.pin_icon);
            friendPic = itemView.findViewById(R.id.friend_image);

            // Set the pinIcon so that it moves the friend up if pinned or down if unpinned
            pinIcon.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Friend friend = friendsList.get(position);
                    friend.setPinned(!friend.isPinned());
                    notifyItemChanged(position);

                    connectionsFragment.rearrangeFriendsList();
                }
            });
        }
    }
}
