package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import mini.game.collection.R;

public class MemoryMenuFragment extends Fragment implements OnClickListener{


    interface Listener {
        void startMemoryGame();
        void startMemoryQuickMatch();
        void checkMemoryGames();
    }

    private Listener mListener = null;

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
    public void onCheckGamesClicked() {
        System.out.println("In OnCheckGamesClicked");
        mListener.checkMemoryGames();
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked() {
        System.out.println("In onStartMatchClicked");
        mListener.startMemoryGame();
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked() {
        mListener.startMemoryQuickMatch();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startMatchButton:
                onStartMatchClicked();
                break;
            case R.id.checkGamesButton:
                onCheckGamesClicked();
                break;
            case R.id.quickMatchButon:
                onQuickMatchClicked();
                break;
            default:
                break;
        }
    }
}
