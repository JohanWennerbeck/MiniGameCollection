package fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;

import mini.game.collection.R;

public class MemoryMenuFragment extends Fragment implements OnClickListener{

    private PlayersClient mPlayersClient;
    // Client used to interact with the TurnBasedMultiplayer system.
    private TurnBasedMultiplayerClient mTurnBasedMultiplayerClient = null;

    private AlertDialog mAlertDialog;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;
    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // tag for debug logging
    private static final String TAG = "MGC";

    interface Listener {
        void startMemoryGame();
        void startMemoryQuickMatch();
        void checkMemoryGames();
    }

    private Listener mListener = null;

    private String mDisplayName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                         ViewGroup container,
                         Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.memory_menu, container, false);

        final int[] clickableIds = new int[]{
                R.id.startMatchButton,
            R.id.checkGamesButton,
            R.id.quickMatchButon};

        for (int clickableId : clickableIds) {
            view.findViewById(clickableId).setOnClickListener(this);
        }
        return view;


    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(View view) {
        System.out.println("In OnCheckGamesClicked");
        mListener.checkMemoryGames();
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(View view) {
        System.out.println("In onStartMatchClicked");
        mListener.startMemoryGame();
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked(View view) {
        mListener.startMemoryQuickMatch();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startMatchButton:
                onStartMatchClicked(this.getView());
                break;
            case R.id.checkGamesButton:
                onCheckGamesClicked(this.getView());
                break;
            case R.id.quickMatchButon:
                onQuickMatchClicked(this.getView());
                break;
            default:
                break;
        }
    }

    public void setDisplayName(String name){
        this.mDisplayName = name;
    }
    public void setmTurnBasedMultiplayerClient(TurnBasedMultiplayerClient turnBasedMultiplayerClient){
        this.mTurnBasedMultiplayerClient = turnBasedMultiplayerClient;
    }
}
