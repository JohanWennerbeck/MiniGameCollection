/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.ArrayList;

import gamecomponents.MemoryPlayers;
import gamecomponents.MemoryTurn;
import gamecomponents.memory.MemoryFactory;
import mini.game.collection.R;


/**
 * Our main activity for the game.
 * <p>
 * IMPORTANT: Before attempting to run this sample, please change
 * the package name to your own package name (not com.android.*) and
 * replace the IDs on res/values/ids.xml by your own IDs (you must
 * create a game in the developer console to get those IDs).
 * <p>
 * This is a very simple game where the user selects "easy mode" or
 * "hard mode" and then the "gameplay" consists of inputting the
 * desired score (0 to 9999). In easy mode, you get the score you
 * request; in hard mode, you get half.
 *
 * @author Bruno Oliveira
 */
public class MainActivity extends AppCompatActivity implements
        MainMenuFragment.Listener, NavigationView.OnNavigationItemSelectedListener, MemoryMenuFragment.Listener, MemoryGameFragment.Listener {
    //Everything needed for starting screen

    //Fragment
    private MainMenuFragment mMainMenuFragment;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient;

    // Client variables
    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;

    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    String displayName;

    //AlertDialog
    private AlertDialog mAlertDialog;

    // tag for debug logging
    private static final String TAG = "MGC";

    //Maybe not needed
    private PlayersClient mPlayersClient;

    // Client used to interact with the TurnBasedMultiplayer system.
    private TurnBasedMultiplayerClient mTurnBasedMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    private final AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d(TAG, "Skapar nav drawer listener");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        // Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        // Create the fragments used by the UI.
        mMainMenuFragment = new MainMenuFragment();
        mMemoryFragment = new MemoryMenuFragment();
        mMemoryGameFragment = new MemoryGameFragment();
        // Set the listeners and callbacks of fragment events.
        mMainMenuFragment.setListener(this);
        mMemoryFragment.setListener(this);
        mMemoryGameFragment.setListener(this);

        // Add initial Main Menu fragment.
        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container  ,
                mMainMenuFragment).commit();
        checkPlaceholderIds();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        System.out.println("Den är i onBackStackChanged");

                    }
                });
    }

    // Check the sample to ensure all placeholder ids are are updated with real-world values.
    // This is strictly for the purpose of the samples; you don't need this in a production
    // application.
    private void checkPlaceholderIds() {
        StringBuilder problems = new StringBuilder();

        if (getPackageName().startsWith("com.google.")) {
            problems.append("- Package name start with com.google.*\n");
        }

        for (Integer id : new Integer[]{
                R.string.app_id,
                R.string.achievement_a,
                R.string.achievement_b,
                R.string.achievement_c,
                R.string.achievement_d,
                R.string.achievement_e,
                R.string.achievement_f,
                R.string.leaderboard_a,
                R.string.leaderboard_b,
                R.string.event_start,
                R.string.event_number_chosen,}) {

            String value = getString(id);

            if (value.startsWith("YOUR_")) {
                // needs replacing
                problems.append("- Placeholders(YOUR_*) in ids.xml need updating\n");
                break;
            }
        }

        if (problems.length() > 0) {
            problems.insert(0, "The following problems were found:\n\n");

            problems.append("\nThese problems may prevent the app from working properly.");
            problems.append("\n\nSee the TODO window in Android Studio for more information");
            (new AlertDialog.Builder(this)).setMessage(problems.toString())
                    .setNeutralButton(android.R.string.ok, null).create().show();
        }
    }

    private void loadAndPrintEvents() {

        final MainActivity mainActivity = this;

        mEventsClient.load(true)
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<EventBuffer>>() {
                    @Override
                    public void onSuccess(AnnotatedData<EventBuffer> eventBufferAnnotatedData) {
                        EventBuffer eventBuffer = eventBufferAnnotatedData.get();

                        int count = 0;
                        if (eventBuffer != null) {
                            count = eventBuffer.getCount();
                        }

                        Log.i(TAG, "number of events: " + count);

                        for (int i = 0; i < count; i++) {
                            Event event = eventBuffer.get(i);
                            Log.i(TAG, "event: "
                                    + event.getName()
                                    + " -> "
                                    + event.getValue());
                        }
                        eventBuffer.release();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(e, getString(R.string.achievements_exception));
                    }
                });

    }

    // Switch UI to the given fragment
    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag).addToBackStack(null)
                .commit();

    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the invitation callbacks; they will be re-registered via
        // onResume->signInSilently->onConnected.
        if (mInvitationsClient != null) {
            mInvitationsClient.unregisterInvitationCallback(mInvitationCallback);
        }

        if (mTurnBasedMultiplayerClient != null) {
            mTurnBasedMultiplayerClient.unregisterTurnBasedMatchUpdateCallback(mMatchUpdateCallback);
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mAchievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        mLeaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
        mEventsClient = Games.getEventsClient(this, googleSignInAccount);
        mPlayersClient = Games.getPlayersClient(this, googleSignInAccount);


        // Show sign-out button on main menu
        mMainMenuFragment.setShowSignInButton(false);

        Log.d(TAG, "onConnected(): Connection successful");

        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer()
                .addOnCompleteListener(new OnCompleteListener<Player>() {
                    @Override
                    public void onComplete(@NonNull Task<Player> task) {
                        if (task.isSuccessful()) {
                            displayName = task.getResult().getDisplayName();
                        } else {
                            Exception e = task.getException();
                            handleException(e, getString(R.string.players_exception));
                            displayName = "???";
                        }
                        //TODO something
                    }
                });

        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }

        loadAndPrintEvents();

        mPlayersClient = Games.getPlayersClient(this, googleSignInAccount);

        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);

        mInvitationsClient = Games.getInvitationsClient(this, googleSignInAccount);

        Games.getPlayersClient(this, googleSignInAccount)
                .getCurrentPlayer()
                .addOnSuccessListener(
                        new OnSuccessListener<Player>() {
                            @Override
                            public void onSuccess(Player player) {
                                mPlayerId = player.getPlayerId();
                                }
                        }
                )
                .addOnFailureListener(createFailureListener("There was a problem getting the player!"));

        Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (match != null) {
                                updateMatch(match);
                            }
                        }
                    }
                })
                .addOnFailureListener(createFailureListener(
                        "There was a problem getting the activation hint!"));

        // mMemoryFragment.setViewVisibility();

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        mTurnBasedMultiplayerClient.registerTurnBasedMatchUpdateCallback(mMatchUpdateCallback);
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mAchievementsClient = null;
        mLeaderboardsClient = null;
        mInvitationsClient = null;
        mPlayersClient = null;
        mTurnBasedMultiplayerClient = null;

        // Show sign-in button on main menu
        mMainMenuFragment.setShowSignInButton(true);
    }

    private void startSignInIntent() {
        Log.d(TAG, "in startSignInIntent");
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    private void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    private void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean successful = task.isSuccessful();
                        Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));

                        onDisconnected();
                    }
                });
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for
     *                  the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for
     *                  why the exception happened
     */
    private void handleException(Exception exception, String details) {
        Log.d(TAG, "in handleException");

        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        if (!checkStatusCode(status)) {
            return;
        }



        if (exception instanceof TurnBasedMultiplayerClient.MatchOutOfDateApiException) {
            TurnBasedMultiplayerClient.MatchOutOfDateApiException matchOutOfDateApiException =
                    (TurnBasedMultiplayerClient.MatchOutOfDateApiException) exception;

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Match was out of date, updating with latest match data...")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();

            TurnBasedMatch match = matchOutOfDateApiException.getMatch();
            updateMatch(match);

            return;
        }
        String message = getString(R.string.status_exception_error, details, status, exception);
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(int statusCode) {
        Log.d(TAG, "in checkStatusCode");

        switch (statusCode) {
            case GamesCallbackStatusCodes.OK:
                return true;

            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                showErrorMessage(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    public void showErrorMessage(int stringId) {
        Log.d(TAG, "in showErrorMessage");

        showWarning("Warning", getResources().getString(stringId));
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        Log.d(TAG, "in showWarning");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    @Override
    public void onSignInButtonClicked() {
        Log.d(TAG, "in onSignInButtonClicked");
        startSignInIntent();
    }

    @Override
    public void onSignOutButtonClicked() {
        Log.d(TAG, "in onSignOutButtonClicked");
        signOut();
    }

    private class AccomplishmentsOutbox {
        boolean mPrimeAchievement = false;
        boolean mHumbleAchievement = false;
        boolean mLeetAchievement = false;
        boolean mArrogantAchievement = false;
        int mBoredSteps = 0;
        int mEasyModeScore = -1;
        int mHardModeScore = -1;

        boolean isEmpty() {
            return !mPrimeAchievement && !mHumbleAchievement && !mLeetAchievement &&
                    !mArrogantAchievement && mBoredSteps == 0 && mEasyModeScore < 0 &&
                    mHardModeScore < 0;
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "in onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "in onCreateOptionMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "in onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "in onNavigationItemSelected");

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.Memory) {
            Log.d(TAG, "Starta Memory");
            switchToMemoryMenu();
        } else if (id == R.id.FourInARow) {
            switchToFragment(mMainMenuFragment);
        } else if (id == R.id.CarBingo) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param requestedScore the score the user requested.
     * @param finalScore     the score the user got.
     */
    private void checkForAchievements(int requestedScore, int finalScore) {
        Log.d(TAG, "in checkForAchievements");

        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (requestedScore == 9999) {
            mOutbox.mArrogantAchievement = true;
            achievementToast(getString(R.string.achievement_arrogant_toast_text));
        }
        if (requestedScore == 0) {
            mOutbox.mHumbleAchievement = true;
            achievementToast(getString(R.string.achievement_humble_toast_text));
        }
        if (finalScore == 1337) {
            mOutbox.mLeetAchievement = true;
            achievementToast(getString(R.string.achievement_leet_toast_text));
        }
        mOutbox.mBoredSteps++;
    }

    private void achievementToast(String achievement) {
        Log.d(TAG, "in achievementToast");

        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void pushAccomplishments() {
        Log.d(TAG, "in pushAccomplishments");
        if (!isSignedIn()) {
            // can't push to the cloud, try again later
            return;
        }
        if (mOutbox.mPrimeAchievement) {
            mAchievementsClient.unlock(getString(R.string.achievement_a));
            mOutbox.mPrimeAchievement = false;
        }
        if (mOutbox.mArrogantAchievement) {
            mAchievementsClient.unlock(getString(R.string.achievement_b));
            mOutbox.mArrogantAchievement = false;
        }
        if (mOutbox.mHumbleAchievement) {
            mAchievementsClient.unlock(getString(R.string.achievement_c));
            mOutbox.mHumbleAchievement = false;
        }
        if (mOutbox.mLeetAchievement) {
            mAchievementsClient.unlock(getString(R.string.achievement_d));
            mOutbox.mLeetAchievement = false;
        }
        if (mOutbox.mBoredSteps > 0) {
            mAchievementsClient.increment(getString(R.string.achievement_e),
                    mOutbox.mBoredSteps);
            mAchievementsClient.increment(getString(R.string.achievement_f),
                    mOutbox.mBoredSteps);
            mOutbox.mBoredSteps = 0;
        }
        if (mOutbox.mEasyModeScore >= 0) {
            mLeaderboardsClient.submitScore(getString(R.string.leaderboard_a),
                    mOutbox.mEasyModeScore);
            mOutbox.mEasyModeScore = -1;
        }
        if (mOutbox.mHardModeScore >= 0) {
            mLeaderboardsClient.submitScore(getString(R.string.leaderboard_b),
                    mOutbox.mHardModeScore);
            mOutbox.mHardModeScore = -1;
        }
    }

    // This is a helper functio that will do all the setup to create a simple failure message.
    // Add it to any task and in the case of an failure, it will report the string in an alert
    // dialog.
    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }


    //Everything needed for Memory

    // Fragments
    private MemoryMenuFragment mMemoryFragment;
    private MemoryGameFragment mMemoryGameFragment;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public MemoryTurn mTurnData;

    private String mPlayerId;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    @Override
    public void startMemoryGame() {
        mTurnBasedMultiplayerClient.getSelectOpponentsIntent(1, 7, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                })
                .addOnFailureListener(createFailureListener(
                        getString(R.string.error_get_select_opponents)));
    }

    @Override
    public void startMemoryQuickMatch() {
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        TurnBasedMatchConfig turnBasedMatchConfig = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria).build();



        // Start the match
        mTurnBasedMultiplayerClient.createMatch(turnBasedMatchConfig)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onInitiateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem creating a match!"));
    }

    @Override
    public void checkMemoryGames() {
        mTurnBasedMultiplayerClient.getInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
                    }
                })
                .addOnFailureListener(createFailureListener(getString(R.string.error_get_inbox_intent)));
    }

    private void onInitiateMatch(TurnBasedMatch match) {
        Log.d(TAG, "in onInitiateMatch");


        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        Log.d(TAG, "in startMatch in MainActivity");
        mTurnData = new MemoryTurn();
        // Some basic turn data
        mTurnData.data = MemoryFactory.getInstance().createMemory();
        mMatch = match;
        if(mTurnData.playerOneName.equals("testtester")) {
            mTurnData.playerOneName = displayName;
        }
        mTurnData.playerturn = 1;
        String myParticipantId = mMatch.getParticipantId(mPlayerId);

        mTurnBasedMultiplayerClient.takeTurn(match.getMatchId(),
                mTurnData.persist(), myParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        updateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        Log.d(TAG, "in updateMatch");
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning("Complete!",
                            "This game is over; someone finished it, and so did you!  " +
                                    "There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                try {
                    mTurnData = new MemoryTurn();
                    mTurnData.data = MemoryFactory.getInstance().createMemory();
                    mTurnData.unpersist(mMatch.getData());
                    if(mTurnData.playerTwoName.equals("testtester") && !mTurnData.playerOneName.equals(displayName)){
                        mTurnData.playerTwoName = displayName;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;

        // mMemoryGameFragment.setViewVisibility();
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        switchToFragment(mMemoryGameFragment);
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Handle notification events.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            Log.d(TAG, "in onInvitationRecieved");

            Toast.makeText(
                    MainActivity.this,
                    "An invitation has arrived from "
                            + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {
            Log.d(TAG, "in onInvitationRemoved");
            Toast.makeText(MainActivity.this, "An invitation was removed.", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private TurnBasedMatchUpdateCallback mMatchUpdateCallback = new TurnBasedMatchUpdateCallback() {
        @Override
        public void onTurnBasedMatchReceived(@NonNull TurnBasedMatch turnBasedMatch) {
            Log.d(TAG, "in onTurnBasedMatchRecieved");
            Toast.makeText(MainActivity.this, "A match was updated.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTurnBasedMatchRemoved(@NonNull String matchId) {
            Log.d(TAG, "in onTurnBasedMatchRemoved");

            Toast.makeText(MainActivity.this, "A match was removed.", Toast.LENGTH_SHORT).show();
        }
    };

    // Cancel the game. Should possibly wait until the game is canceled before
    // giving up on the view.
    public void memoryCancelClicked() {


        mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String matchId) {
                        onCancelMatch(matchId);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));

        isDoingTurn = false;
        mMemoryGameFragment.setDoingTurn(isDoingTurn);
        switchToMemoryMenu();
    }

    // Leave the game during your turn. Note that there is a separate
    // mTurnBasedMultiplayerClient.leaveMatch() if you want to leave NOT on your turn.
    public void memoryLeaveClicked() {

        String nextParticipantId = getNextParticipantId();

        mTurnBasedMultiplayerClient.leaveMatchDuringTurn(mMatch.getMatchId(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onLeaveMatch();
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem leaving the match!"));
        switchToMemoryMenu();
    }

    // Finish the game. Sometimes, this is your only choice.
    public void memoryFinishClicked() {

        mTurnBasedMultiplayerClient.finishMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem finishing the match!"));

        isDoingTurn = false;
        mMemoryGameFragment.setDoingTurn(isDoingTurn);
        switchToMemoryMenu();
    }

    public void memoryGiveUpClicked() {

    }


    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void memoryDoneClicked() {
        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        //mTurnData.data ;

        mTurnBasedMultiplayerClient.takeTurn(mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
        mTurnData = null;
        switchToMemoryMenu();
    }

    public MemoryTurn getMemoryData(){
        return this.mTurnData;
    }

    private void onLeaveMatch() {
        Log.d(TAG, "in onLeaveMatch");


        isDoingTurn = false;
        showWarning("Left", "You've left this match.");
    }


    public void onUpdateMatch(TurnBasedMatch match) {
        Log.d(TAG, "in onUpdateMatch");



        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        //mMemoryGameFragment.setViewVisibility();
    }

    private void onCancelMatch(String matchId) {
        Log.d(TAG, "in onCancelMatch");


        isDoingTurn = false;

        showWarning("Match", "This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
    }

    public void switchToMemoryMenu(){
        switchToFragment(mMemoryFragment);
    }

    //Everything else





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "in onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }

        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != Activity.RESULT_OK) {
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from the 'Select Match' dialog.");
                return;
            }

            TurnBasedMatch match = intent
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from 'Select players to Invite' dialog");
                return;
            }

            // get the invitee list
            ArrayList<String> invitees = intent
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria;

            int minAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers,
                        maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            mTurnBasedMultiplayerClient.createMatch(tbmc)
                    .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                        @Override
                        public void onSuccess(TurnBasedMatch turnBasedMatch) {
                            onInitiateMatch(turnBasedMatch);
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem creating a match!"));

        }
    }

    private void logBadActivityResult(int requestCode, int resultCode, String message) {
        Log.i(TAG, "Bad activity result(" + resultCode + ") for request (" + requestCode + "): "
                + message);
    }




    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {
        Log.d(TAG, "in getNextParticipantId");
        String myParticipantId = mMatch.getParticipantId(mPlayerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // Rematch dialog
    public void askForRematch() {
        Log.d(TAG, "in askForRematch");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        Log.d(TAG, "in rematch");

        mTurnBasedMultiplayerClient.rematch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onInitiateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem starting a rematch!"));
        mMatch = null;
        isDoingTurn = false;
    }

}
