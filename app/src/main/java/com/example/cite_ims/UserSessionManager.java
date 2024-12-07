package com.example.cite_ims;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    // Shared Preferences file name and keys
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
     * Create a new login session.
     *
     * @param username The username of the user.
     * @param role The role of the user (e.g., "admin" or "user").
     */
    public void createLoginSession(String username, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    /**
     * Check if the user is logged in.
     *
     * @return true if the user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get the username of the logged-in user.
     *
     * @return The username as a String, or null if not logged in.
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * Get the role of the logged-in user.
     *
     * @return The role as a String, or null if not logged in.
     */
    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    /**
     * Log out the user by clearing all session data.
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
