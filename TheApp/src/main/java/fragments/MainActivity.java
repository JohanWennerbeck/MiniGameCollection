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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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
    MainMenuFragment.Listener, NavigationView.OnNavigationItemSelectedListener {

  // Fragments
  private MainMenuFragment mMainMenuFragment;

  // Client used to sign in with Google APIs
  private GoogleSignInClient mGoogleSignInClient;

  // Client variables
  private AchievementsClient mAchievementsClient;
  private LeaderboardsClient mLeaderboardsClient;
  private EventsClient mEventsClient;
  private PlayersClient mPlayersClient;

  // request codes we use when invoking an external activity
  private static final int RC_UNUSED = 5001;
  private static final int RC_SIGN_IN = 9001;

  // tag for debug logging
  private static final String TAG = "TanC";


  // achievements and scores we're pending to push to the cloud
  // (waiting for the user to sign in, for instance)
  private final AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
              this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      drawer.addDrawerListener(toggle);
      toggle.syncState();

      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
      navigationView.setNavigationItemSelectedListener(this);

    // Create the client used to sign in to Google services.
    mGoogleSignInClient = GoogleSignIn.getClient(this,
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

    // Create the fragments used by the UI.
    mMainMenuFragment = new MainMenuFragment();

    // Set the listeners and callbacks of fragment events.
    mMainMenuFragment.setListener(this);

    // Add initial Main Menu fragment.
    // IMPORTANT: if this Activity supported rotation, we'd have to be
    // more careful about adding the fragment, since the fragment would
    // already be there after rotation and trying to add it again would
    // result in overlapping fragments. But since we don't support rotation,
    // we don't deal with that for code simplicity.
    getSupportFragmentManager().beginTransaction().add(R.id.drawer_layout,
        mMainMenuFragment).commit();

    checkPlaceholderIds();
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
    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, newFrag)
        .commit();
  }

  private boolean isSignedIn() {
    return GoogleSignIn.getLastSignedInAccount(this) != null;
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

  private void startSignInIntent() {
    startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume()");

    // Since the state of the signed in user can change when the activity is not active
    // it is recommended to try and sign in silently from when the app resumes.
    signInSilently();
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

  private void handleException(Exception e, String details) {
    int status = 0;

    if (e instanceof ApiException) {
      ApiException apiException = (ApiException) e;
      status = apiException.getStatusCode();
    }

    String message = getString(R.string.status_exception_error, details, status, e);

    new AlertDialog.Builder(MainActivity.this)
        .setMessage(message)
        .setNeutralButton(android.R.string.ok, null)
        .show();
  }

  /**
   * Start gameplay. This means updating some status variables and switching
   * to the "gameplay" screen (the screen where the user types the score they want).
   *
   * @param hardMode whether to start gameplay in "hard mode".
   */
  private void startGame(boolean hardMode) {
    mEventsClient.increment(getString(R.string.event_start), 1);
  }


  // Checks if n is prime. We don't consider 0 and 1 to be prime.
  // This is not an implementation we are mathematically proud of, but it gets the job done.
  private boolean isPrime(int n) {
    int i;
    if (n == 0 || n == 1) {
      return false;
    }
    for (i = 2; i <= n / 2; i++) {
      if (n % i == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check for achievements and unlock the appropriate ones.
   *
   * @param requestedScore the score the user requested.
   * @param finalScore     the score the user got.
   */
  private void checkForAchievements(int requestedScore, int finalScore) {
    // Check if each condition is met; if so, unlock the corresponding
    // achievement.
    if (isPrime(finalScore)) {
      mOutbox.mPrimeAchievement = true;
      achievementToast(getString(R.string.achievement_prime_toast_text));
    }
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
    // Only show toast if not signed in. If signed in, the standard Google Play
    // toasts will appear, so we don't need to show our own.
    if (!isSignedIn()) {
      Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
          Toast.LENGTH_LONG).show();
    }
  }

  private void pushAccomplishments() {
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



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
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


    // Set the greeting appropriately on main menu
    mPlayersClient.getCurrentPlayer()
        .addOnCompleteListener(new OnCompleteListener<Player>() {
          @Override
          public void onComplete(@NonNull Task<Player> task) {
            String displayName;
            if (task.isSuccessful()) {
              displayName = task.getResult().getDisplayName();
            } else {
              Exception e = task.getException();
              handleException(e, getString(R.string.players_exception));
              displayName = "???";
            }
            mMainMenuFragment.setGreeting("Hello, " + displayName);
          }
        });


    // if we have accomplishments to push, push them
    if (!mOutbox.isEmpty()) {
      pushAccomplishments();
      Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
          Toast.LENGTH_LONG).show();
    }

    loadAndPrintEvents();
  }

  private void onDisconnected() {
    Log.d(TAG, "onDisconnected()");

    mAchievementsClient = null;
    mLeaderboardsClient = null;
    mPlayersClient = null;

    // Show sign-in button on main menu
    mMainMenuFragment.setShowSignInButton(true);

    mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
  }

  @Override
  public void onSignInButtonClicked() {
    startSignInIntent();
  }

  @Override
  public void onSignOutButtonClicked() {
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Memory) {
            // Handle the camera action
        } else if (id == R.id.FourInARow) {

        } else if (id == R.id.CarBingo) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
