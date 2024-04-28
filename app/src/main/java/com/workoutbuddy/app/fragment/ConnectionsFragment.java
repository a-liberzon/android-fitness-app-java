package com.workoutbuddy.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.FriendActivity;
import com.workoutbuddy.app.databinding.FragmentConnectionsBinding;
import com.workoutbuddy.app.model.Friend;
import com.workoutbuddy.app.model.FriendsAdapter;
import com.workoutbuddy.app.model.Utilities;
import com.workoutbuddy.app.model.Workout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConnectionsFragment extends Fragment {
    private FragmentConnectionsBinding binding;
    private List<Friend> connectionsList;
    private List<Friend> allUserList;
    FriendsAdapter adapter;
    LinearLayout searchResultsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Show all friends
        populateScreenWithConnections();

        // TODO: Get this list from data base or user info?
        // Get list of all users
        allUserList = getAllExistingUsers();

        // Set up search bar functionality
        setUpSearch();

        return root;
    }

    /**
     * Method to populate the screen with all of the user's friends
     */
    private void populateScreenWithConnections() {
        // Get list of connections
        connectionsList = getFriendsList();

        // Set the recyclerView in the layout, and create and set the adapter
        RecyclerView recyclerView = binding.friendsRecyclerView;
        adapter = new FriendsAdapter(connectionsList, this);
        recyclerView.setAdapter(adapter);

        // Set the layout manager on the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * Method to re-arrange friends when a user is pinned/or un pinned.
     */
    public void rearrangeFriendsList() {
        // Sort friends by pinned first and then unpinned
        List<Friend> sortedFriends = connectionsList.stream()
                .sorted(Comparator.comparing(Friend::isPinned).reversed())
                .collect(Collectors.toList());

        // Update the friends list
        connectionsList.clear();
        connectionsList.addAll(sortedFriends);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
        // TODO: Save pinned friends in DB?
    }

    /**
     * Method to set up the search functionality
     */
    private void setUpSearch() {
        // Get the search bar and results view, and set up hing
        SearchView searchBar = binding.searchBar;
        searchResultsView = binding.searchResultsView;
        searchBar.setQueryHint(getString(R.string.search_buddies_hint));

        // Hide keyboard when clicking on search results view, or in the magnifier glass
        searchResultsView.setOnClickListener(view -> hideKeyboard());
        searchBar.findViewById(androidx.appcompat.R.id.search_mag_icon).setOnClickListener(V -> hideKeyboard());

        // Set up functionality when typing on search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Actions to take when enter is pressed
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                return false;
            }
            // Actions to take when user types on search bar
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())          // If text is empty, collapse view
                    collapseSearchResults();
                else                            // Else update the results with filtered matches
                    showMatchingUsers(newText);
                return true;
            }
        });
    }

    /**
     * Method to filter users that match the query and then show them on GUI.
     */
    private void showMatchingUsers(String query) {
        // Filter users whose username match the query, put the ones that start with query first
        // TODO: filter by email too?
        List<Friend> filteredList = allUserList.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(
                        u -> !u.getUsername().toLowerCase().startsWith(query.toLowerCase())))
                .limit(8) // Limit the number of results to first 8 (8 best)
                .collect(Collectors.toList());

        // Update the expanded view with the search results
        updateSearchResultsView(filteredList, query);
    }

    /**
     * Method to update the search results view in the GUI.
     */
    private void updateSearchResultsView(List<Friend> filteredList, String query) {
        // Clear the existing views in the expanded view
        searchResultsView.removeAllViews();

        // Add matching users
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (Friend friend : filteredList) {
            // Fill in layout with friend search result vies
            LinearLayout friendItemLayout = (LinearLayout) inflater.inflate(R.layout.friend_search_result, searchResultsView, false);

            // Get friend's image and set it
            ImageView friendImageView = friendItemLayout.findViewById(R.id.friend_image);
            friendImageView.setImageResource(friend.getImageResourceId());

            // Get friend's user name and set it
            TextView usernameTextView = friendItemLayout.findViewById(R.id.friend_username);
            usernameTextView.setText(friend.getUsername());

            // Set up action button (add or delete friend)
            ImageView actionButton = friendItemLayout.findViewById(R.id.friend_action_view);
            boolean isFriend = connectionsList.contains(friend);
            actionButton.setImageResource(isFriend ? R.drawable.ic_remove_friend : R.drawable.ic_add_friend);
            actionButton.setOnClickListener(view -> {
                if (isFriend)                   // Perform delete friend action
                    deleteFriend(friend);
                else                            // Perform add friend action
                    addFriend(friend);
            });

            // Set up so that on click of a friends name it goes to workouts
            usernameTextView.setOnClickListener(v -> {
                if (isFriend)
                    goToFriendWorkouts(friend, getContext());
                else
                    Toast.makeText(getContext(), "Can't view non-friends workouts!", Toast.LENGTH_SHORT).show();
            });

            // Add friend_search_result to searc results view
            searchResultsView.addView(friendItemLayout);
        }

        // If no matching users, show text saying so
        if (filteredList.isEmpty()) {
            TextView textView = new TextView(requireContext());
            textView.setText("No results for \"" + query + "\"");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            textView.setPadding(16, 8, 16, 8);
            searchResultsView.addView(textView);
        }

        // Show results
        searchResultsView.setVisibility(View.VISIBLE);
    }

    /**
     * Method to hide keyboard.
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
        }
    }

    /**
     * Method to hide the search results view.
     */
    public void collapseSearchResults() {
        if (searchResultsView != null) {
            searchResultsView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to delete a friend.
     */
    private void deleteFriend(Friend friend) {
        Toast.makeText(getContext(), "TODO: Delete friend", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to add a friend.
     */
    private void addFriend(Friend friend) {
        Toast.makeText(getContext(), "TODO: Add friend", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to go to friend's workouts.
     */
    public void goToFriendWorkouts(Friend friend, Context context) {
        // Send the friend whose workouts we want to view
        Bundle bundle = new Bundle();
        bundle.putString("friend", friend.getUsername());

        // Create an intent to start FriendActivity
        Intent intent = new Intent(context, FriendActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Temporary method to get friend list.
     * TODO: Replace with actual friends
     */
    private List<Friend> getFriendsList() {
        List<String> names = Arrays.asList("Elon", "Emma", "Barney", "Tony", "William", "Drake", "John");
        List<Integer> faces = Arrays.asList(R.drawable.face_elon, R.drawable.face_emma,
                R.drawable.face_barney, R.drawable.face_tony, R.drawable.face_will, R.drawable.face_drake,
                R.drawable.face_leonardo, R.drawable.face_messi);
        List<Workout> workoutList = Utilities.getDummyWorkouts();
        List<Friend> friendList = new ArrayList<>();
        IntStream.range(0, names.size()).forEach(i -> {
            int imageId = i < (faces.size()) ? faces.get(i) : 0;
            friendList.add(new Friend(names.get(i), workoutList.size(), 123-i, workoutList, imageId));
        });

        return friendList;
    }

    /**
     * Temporary method to get all existing users.
     * TODO: Replace with actual existing users
     */
    private List<Friend> getAllExistingUsers() {
        List<Integer> faces = Arrays.asList(R.drawable.face_elon, R.drawable.face_emma,
                R.drawable.face_barney, R.drawable.face_tony, R.drawable.face_will, R.drawable.face_drake,
                R.drawable.face_leonardo, R.drawable.face_messi);
        List<String> names = Arrays.asList("Elon", "Emma", "Barney", "Tony", "William", "Drake",
                "Leonardo", "Lionel", "Randy", "John", "Paul", "Mandy", "Ramon");
        List<Workout> workoutList = Utilities.getDummyWorkouts();
        List<Friend> userList = new ArrayList<>();
        IntStream.range(0, names.size()).forEach(i -> {
            int imageId = i < (faces.size()) ? faces.get(i) : 0;
            userList.add(new Friend(names.get(i), workoutList.size(), 123-i, workoutList, imageId));
        });
        return userList;
    }
}