package com.example.cite_ims;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Creates a login session for the user.
     *
     * @param username The username of the logged-in user.
     * @param role     The role of the logged-in user (e.g., "admin", "user").
     */
    public void createLoginSession(String username, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    /**
     * Checks if the user is logged in.
     *
     * @return True if logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Retrieves the logged-in user's username.
     *
     * @return The username, or null if no session exists.
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * Retrieves the logged-in user's role.
     *
     * @return The role, or null if no session exists.
     */
    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    /**
     * Checks if the logged-in user is an admin.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        String role = getRole();
        return role != null && role.equalsIgnoreCase("admin");
    }

    /**
     * Checks if the logged-in user is a regular user.
     *
     * @return True if the user is a regular user, false otherwise.
     */
    public boolean isUser() {
        String role = getRole();
        return role != null && role.equalsIgnoreCase("user");
    }

    /**
     * Logs out the current user and clears all session data.
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Retrieves all session details as a string (for debugging or display purposes).
     *
     * @return Session details in a formatted string.
     */
    public String getSessionDetails() {
        return "Username: " + getUsername() + "\nRole: " + getRole() +
                "\nLogged In: " + isUserLoggedIn();
    }
}
