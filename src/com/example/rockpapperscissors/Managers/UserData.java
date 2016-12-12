package com.example.rockpapperscissors.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {

	private static UserData INSTANCE = new UserData();

	// Include a 'filename' for our shared preferences
	private static final String PREFS_NAME = "GAME_USERDATA";

	/*
	 * These keys will tell the shared preferences editor which data we're
	 * trying to access
	 */


	/*
	 * Create our shared preferences object & editor which will be used to save
	 * and load data
	 */
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;



	UserData() {
		// The constructor is of no use to us
	}

	public synchronized static UserData getInstance() {
		return INSTANCE;
	}

	public synchronized void init(Context pContext) {
		if (mSettings == null) {
			/*
			 * Retrieve our shared preference file, or if it's not yet created
			 * (first application execution) then create it now
			 */
			mSettings = pContext.getSharedPreferences(PREFS_NAME,
					Context.MODE_PRIVATE);

			/*
			 * Define the editor, used to store data to our preference file
			 */
			mEditor = mSettings.edit();

			/*
			 * Retrieve our current unlocked levels. if the UNLOCKED_LEVEL_KEY
			 * does not currently exist in our shared preferences, we'll create
			 * the data to unlock level 1 by default
			 */

			/*
			 * Same idea as above, except we'll set the sound boolean to true if
			 * the setting does not currently exist
			 */
		}
	}


}