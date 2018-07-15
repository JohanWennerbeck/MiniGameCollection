package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;

import gamecomponents.MemoryTurn;
import gamecomponents.memory.IMemory;
import gamecomponents.memory.MemoryFactory;
import mini.game.collection.R;

public class MemoryGameFragment extends Fragment implements View.OnClickListener{

    private AlertDialog mAlertDialog;
    // Should I be showing the turn API?
    public boolean isDoingTurn = false;


    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // Client used to interact with the TurnBasedMultiplayer system.
    private TurnBasedMultiplayerClient mTurnBasedMultiplayerClient = null;


    private String mDisplayName;
    private String mPlayerId;

    // tag for debug logging
    private static final String TAG = "MGC";

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    Button b6;
    Button b7;
    Button b8;
    Button b9;
    Button b10;
    Button b11;
    Button b12;
    Button b13;
    Button b14;
    Button b15;
    Button b16;
    Button doneButton;

    private int firstTry;
    private int secondTry;
    private boolean done;
    private boolean correct;
    private boolean canClick;
    private int turn;

    interface Listener {
        void memoryDoneClicked();
        void memoryGiveUpClicked();
        MemoryTurn getMemoryData();
    }

    private MemoryGameFragment.Listener mListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.memory_game, container, false);

        final int[] clickableIds = new int[]{
                R.id.done_button,
                R.id.give_up_button,
                R.id.b1,
                R.id.b2,
                R.id.b3,
                R.id.b4,
                R.id.b5,
                R.id.b6,
                R.id.b7,
                R.id.b8,
                R.id.b9,
                R.id.b10,
                R.id.b11,
                R.id.b12,
                R.id.b13,
                R.id.b14,
                R.id.b15,
                R.id.b16};

        for (int clickableId : clickableIds) {
            view.findViewById(clickableId).setOnClickListener(this);
        }

        b1 = view.findViewById(R.id.b1);
        b2 = view.findViewById(R.id.b2);
        b3 = view.findViewById(R.id.b3);
        b4 = view.findViewById(R.id.b4);
        b5 = view.findViewById(R.id.b5);
        b6 = view.findViewById(R.id.b6);
        b7 = view.findViewById(R.id.b7);
        b8 = view.findViewById(R.id.b8);
        b9 = view.findViewById(R.id.b9);
        b10 = view.findViewById(R.id.b10);
        b11 = view.findViewById(R.id.b11);
        b12 = view.findViewById(R.id.b12);
        b13 = view.findViewById(R.id.b13);
        b14 = view.findViewById(R.id.b14);
        b15 = view.findViewById(R.id.b15);
        b16 = view.findViewById(R.id.b16);
        doneButton = view.findViewById(R.id.done_button);

        firstTry = -1;
        secondTry = -1;
        done = false;
        canClick = true;
        doneButton.setVisibility(View.GONE);
        return view;

    }

    public void setListener(MemoryGameFragment.Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if(canClick) {
            switch (view.getId()) {
                case R.id.b1:
                    onMemoryTileTappedEvent(0);
                    break;
                case R.id.b2:
                    onMemoryTileTappedEvent(1);
                    break;
                case R.id.b3:
                    onMemoryTileTappedEvent(2);
                    break;
                case R.id.b4:
                    onMemoryTileTappedEvent(3);
                    break;
                case R.id.b5:
                    onMemoryTileTappedEvent(4);
                    break;
                case R.id.b6:
                    onMemoryTileTappedEvent(5);
                    break;
                case R.id.b7:
                    onMemoryTileTappedEvent(6);
                    break;
                case R.id.b8:
                    onMemoryTileTappedEvent(7);
                    break;
                case R.id.b9:
                    onMemoryTileTappedEvent(8);
                    break;
                case R.id.b10:
                    onMemoryTileTappedEvent(9);
                    break;
                case R.id.b11:
                    onMemoryTileTappedEvent(10);
                    break;
                case R.id.b12:
                    onMemoryTileTappedEvent(11);
                    break;
                case R.id.b13:
                    onMemoryTileTappedEvent(12);
                    break;
                case R.id.b14:
                    onMemoryTileTappedEvent(13);
                    break;
                case R.id.b15:
                    onMemoryTileTappedEvent(14);
                    break;
                case R.id.b16:
                    onMemoryTileTappedEvent(15);
                    break;
                case R.id.give_up_button:
                    mListener.memoryGiveUpClicked();
                    break;
                default:
                    break;

            }
        }

        switch (view.getId()) {
            case R.id.done_button:
                mListener.memoryDoneClicked();
                break;
            case R.id.give_up_button:
                mListener.memoryGiveUpClicked();
                break;
                default:
                    break;
        }
    }

    public void onMemoryTileTappedEvent(int i) {
        System.out.println(firstTry + " är firsttry värden");
        if(firstTry != -1 && correct == false && done == true) {
            turnAroundWrongGuesses();
        } else if (firstTry == -1){
            performFirst(i);
        } else if (!done){
            performSecond(i);
        }

            //TODO win statement
    }

    private void turnAroundWrongGuesses() {
        System.out.println("Inne i turnAroundWrongGuesses");

        getData().data.getTiles().get(secondTry).toggleChecked();
        getData().data.getTiles().get(firstTry).toggleChecked();

        changeToUnFlipped(firstTry);
        changeToUnFlipped(secondTry);
        firstTry = -1;
        doneButton.setVisibility(View.VISIBLE);
        canClick = false;
    }

    private void performSecond(int i) {
        System.out.println("Inne i perform second");

        if(!getData().data.getTiles().get(i).getChecked()) {
            System.out.println("Inne i perform second2");

            getData().data.getTiles().get(i).toggleChecked();
            secondTry = i;
            changeToFlipped(i);
            if (getData().data.getTiles().get(firstTry).getType() == getData().data.getTiles().get(i).getType()) {
                correct = true;
                firstTry = -1;
                increaseScore();
            } else {
                correct = false;
                done = true;
            }
        }
    }

    private void increaseScore() {
        //TODO increase score
    }

    private void performFirst(int i) {
        System.out.println("Inne i perform first");

        if(!getData().data.getTiles().get(i).getChecked()) {
            System.out.println("Inne i perform first2");
            System.out.println("2");
            getData().data.getTiles().get(i).toggleChecked();
            firstTry = i;
            changeToFlipped(i);

        }
    }

    public void changeToFlipped(int i){
        switch (i){
            case 0:
                b1.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 1:
                b2.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 2:
                b3.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 3:
                b4.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 4:
                b5.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 5:
                b6.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 6:
                b7.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 7:
                b8.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 8:
                b9.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 9:
                b10.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 10:
                b11.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 11:
                b12.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 12:
                b13.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 13:
                b14.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 14:
                b15.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            case 15:
                b16.setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                break;
            default:
                break;
        }
    }

    public void changeToUnFlipped(int i){
        switch (i){
            case 0:
                b1.setText("O");
                break;
            case 1:
                b2.setText("O");
                break;
            case 2:
                b3.setText("O");
                break;
            case 3:
                b4.setText("O");
                break;
            case 4:
                b5.setText("O");
                break;
            case 5:
                b6.setText("O");
                break;
            case 6:
                b7.setText("O");
                break;
            case 7:
                b8.setText("O");
                break;
            case 8:
                b9.setText("O");
                break;
            case 9:
                b10.setText("O");
                break;
            case 10:
                b11.setText("O");
                break;
            case 11:
                b12.setText("O");
                break;
            case 12:
                b13.setText("O");
                break;
            case 13:
                b14.setText("O");
                break;
            case 14:
                b15.setText("O");
                break;
            case 15:
                b16.setText("O");
                break;
            default:
                break;
        }
    }

    public MemoryTurn getData(){
       return mListener.getMemoryData();
    }

    public String[] memoryTexts = {"Police", "Horse", "Cow", "Ambulance", "Tractor", "Airplane","Helicopter","Bike"};













    @Override
    public void onPause(){
        super.onPause();
        // Unregister the invitation callbacks; they will be re-registered via
        // onResume->signInSilently->onConnected.
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


    // Switch to gameplay view.
    public void setGameplayUI() {
        Log.d(TAG, "in setGameplayUI");
        isDoingTurn = true;
        //mDataView.setText(mTurnData.data);
        //mTurnTextView.setText(getString(R.string.turn_label, mTurnData.turnCounter));
    }

    // Helpful dialogs

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        Log.d(TAG, "in showWarning");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());

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

        if (exception instanceof TurnBasedMultiplayerClient.MatchOutOfDateApiException) {
            TurnBasedMultiplayerClient.MatchOutOfDateApiException matchOutOfDateApiException =
                    (TurnBasedMultiplayerClient.MatchOutOfDateApiException) exception;

            new AlertDialog.Builder(this.getContext())
                    .setMessage("Match was out of date, updating with latest match data...")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();

            TurnBasedMatch match = matchOutOfDateApiException.getMatch();
            try {
                updateMatch(match);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        if (!checkStatusCode(status)) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(this.getContext())
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }


    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) throws JSONException {
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
                /*mTurnData = new MemoryTurn();
                mTurnData.data = MemoryFactory.getInstance().createMemory() ;
                mTurnData.data.setTiles(MemoryTurn.unpersist(mMatch.getData()));
                System.out.println("JAg ar har" + mTurnData.data.getTiles().get(0).getType());
*/
                System.out.println("OJOJOJOJ PROBLEM PREOBLEM");
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

        //TODO mTurnData = null;

        //setViewVisibility();
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
    public void setmTurnBasedMultiplayerClient(TurnBasedMultiplayerClient turnBasedMultiplayerClient){
        this.mTurnBasedMultiplayerClient = turnBasedMultiplayerClient;
    }
    public void setDoingTurn(boolean bool){
        this.isDoingTurn = bool;
    }
}
