package fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gamecomponents.MemoryTurn;
import gamecomponents.memory.IMemoryTile;
import mini.game.collection.R;

public class MemoryGameFragment extends Fragment implements View.OnClickListener, MemoryMatchFragment.Listener{

    public boolean isDoingTurn = false;

    // tag for debug logging
    private static final String TAG = "MGF";

    //Handler
    Handler handler;

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
    TextView showTurnText;
    RelativeLayout relativeLayout;
    TextView playerOneName;
    TextView playerTwoName;
    TextView playerOneScore;
    TextView playerTwoScore;

    private int firstTry;
    private int secondTry;
    private boolean done;
    private boolean correct;
    private boolean canClick;
    private int clickedNumber;
    private boolean updateTapDone;

    @Override
    public MemoryTurn getMemoryData() {
        return getData();
    }

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
        showTurnText = view.findViewById(R.id.load_game_text);
        relativeLayout = view.findViewById(R.id.memory_game_relative_layout);
        relativeLayout.setOnClickListener(this);
        playerOneName = view.findViewById(R.id.playerOneName);
        playerTwoName = view.findViewById(R.id.playerTwoName);
        playerOneScore = view.findViewById(R.id.playerOneScore);
        playerTwoScore = view.findViewById(R.id.playerTwoScore);

        firstTry = -1;
        secondTry = -1;
        done = false;
        canClick = true;
        doneButton.setBackgroundColor(Color.GRAY);
        doneButton.setClickable(false);
        clickedNumber = 1;
        updateTapDone = false;
        showMemoryWithoutUpdate();
        handler = new Handler(getActivity().getApplicationContext().getMainLooper());
        showTurnText.setVisibility(View.VISIBLE);
        showTurnText.setTextColor(Color.BLACK);
        playerOneName.setText(getData().playerOneName);
        playerTwoName.setText(getData().playerTwoName);
        playerOneScore.setText(String.valueOf(getData().playerOneScore));
        playerTwoScore.setText(String.valueOf(getData().playerTwoScore));
        return view;

    }

    public void setListener(MemoryGameFragment.Listener listener) {
        mListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        if(canClick) {
            if(updateTapDone) {
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
            } else {
                    updateTapDone = true;
                    updateTap();
                    showTurnText.setVisibility(View.GONE);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onMemoryTileTappedEvent(int i) {
        if (firstTry == -1){
            performFirst(i);
        } else if (!done){
            performSecond(i);
        }
        if(firstTry != -1 && !correct && done) {
            turnAroundWrongGuesses();
        }
            //TODO win statement
    }

    private void turnAroundWrongGuesses() {

        getData().data.getTiles().get(secondTry).toggleChecked();
        getData().data.getTiles().get(firstTry).toggleChecked();

        doneButton.setBackgroundColor(Color.GREEN);
        doneButton.setClickable(true);
        canClick = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void performSecond(int i) {

        if(!getData().data.getTiles().get(i).getChecked()) {

            getData().data.getTiles().get(i).toggleChecked();
            secondTry = i;
            changeToFlipped(i);
            if (getData().data.getTiles().get(firstTry).getType() == getData().data.getTiles().get(i).getType()) {
                correct = true;

                setTransitionNameFunc(firstTry, i);
                firstTry = -1;
                increaseScore();
            } else {
                correct = false;
                done = true;
                if (getData().playerturn == 1){
                    getData().playerturn = 2;
                } else {
                    getData().playerturn=1;
                }

            }
        }
    }

    private void increaseScore() {
        if(getData().playerturn == 1){
            System.out.println("Increase player one score");
            getData().playerOneScore++;
            playerOneScore.setText(String.valueOf(getData().playerOneScore));
        } else {
            System.out.println("Increase player two score");
            getData().playerTwoScore++;
            playerTwoScore.setText(String.valueOf(getData().playerTwoScore));
        }
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
        getData().data.getTiles().get(i).setClickedNumber(clickedNumber);
        clickedNumber++;
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

    public void updateMemory(){


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Button[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16};
                final List<IMemoryTile> clickedList = new ArrayList<>();
                final List<Integer> clickedListInt = new ArrayList<>();

                for (int i = 0; i < 16; i++){
                    final int iTemp = i;
                    if(getData().data.getTiles().get(i).getClickedNumber() != 0){
                        clickedList.add(getData().data.getTiles().get(i));
                        clickedListInt.add(i);
                    } else {
                        if(getData().data.getTiles().get(i).getChecked()){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[iTemp].setText(memoryTexts[getData().data.getTiles().get(iTemp).getType()]);
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[iTemp].setText("O");
                                }
                            });
                        }
                    }
                }
                int nextToLast = -1;
                for (int i = 1; i <= clickedList.size(); i++){
                    for (int j = 0; j < clickedList.size(); j++) {
                        final int jTemp = j;
                        if (i == clickedList.get(jTemp).getClickedNumber() && i <= clickedList.size() - 2) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[clickedListInt.get(jTemp)].setText(memoryTexts[getData().data.getTiles().get(clickedListInt.get(jTemp)).getType()]);
                                }
                            });
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getData().data.getTiles().get(clickedListInt.get(j)).setClickedNumber(0);
                        } else if (i == clickedList.get(j).getClickedNumber() && nextToLast == -1) {
                            nextToLast = j;
                            getData().data.getTiles().get(clickedListInt.get(j)).setClickedNumber(0);
                        } else if (i == clickedList.get(j).getClickedNumber()) {
                            final int nextToLastTemp = nextToLast;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[clickedListInt.get(nextToLastTemp)].setText(memoryTexts[getData().data.getTiles().get(clickedListInt.get(nextToLastTemp)).getType()]);
                                }
                            });
                            getData().data.getTiles().get(clickedListInt.get(j)).setClickedNumber(0);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[clickedListInt.get(jTemp)].setText(memoryTexts[getData().data.getTiles().get(clickedListInt.get(jTemp)).getType()]);
                                }
                            });

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    buttons[clickedListInt.get(jTemp)].setText("O");
                                    buttons[clickedListInt.get(nextToLastTemp)].setText("O");
                                }
                            });



                        }
                    }
                }
            }
        }).start();


    }

    public void updateTap(){
        System.out.println("BOOM UpdateTap!");
        updateMemory();
    }

    public void showMemoryWithoutUpdate(){
        Button[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16};

        for (int i = 0; i < 16; i++){
            if(getData().data.getTiles().get(i).getClickedNumber() != 0){
                buttons[i].setText("O");
            } else {
                if(getData().data.getTiles().get(i).getChecked()){
                    buttons[i].setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                } else {
                    buttons[i].setText("O");
                }
            }
        }
    }

    public String[] memoryTexts = {"Police", "Horse", "Cow", "Ambulance", "Tractor", "Airplane","Helicopter","Bike"};


    @Override
    public void onPause(){
        super.onPause();
        // Unregister the invitation callbacks; they will be re-registered via
        // onResume->signInSilently->onConnected.
    }


    public void setDoingTurn(boolean bool){
        this.isDoingTurn = bool;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTransitionNameFunc(int i, int j){
        // Set shared and scene transitions
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move).setDuration(5000));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move).setDuration(5000));

        MemoryMatchFragment secondFragment = new MemoryMatchFragment();
        secondFragment.setListener(this);
        // Set shared and scene transitions on 2nd fragment
        secondFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move).setDuration(5000));
        secondFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move).setDuration(5000));
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        switch (i) {
            case 0:
                b1.setTransitionName("tileOne");
                trans.addSharedElement(b1, "tileOne");
                break;
            case 1:
                b2.setTransitionName("tileOne");
                trans.addSharedElement(b2, "tileOne");
                break;
            case 2:
                b3.setTransitionName("tileOne");
                trans.addSharedElement(b3, "tileOne");
                break;
            case 3:
                b4.setTransitionName("tileOne");
                trans.addSharedElement(b4, "tileOne");
                break;
            case 4:
                b5.setTransitionName("tileOne");
                trans.addSharedElement(b5, "tileOne");
                break;
            case 5:
                b6.setTransitionName("tileOne");
                trans.addSharedElement(b6, "tileOne");
                break;
            case 6:
                b7.setTransitionName("tileOne");
                trans.addSharedElement(b7, "tileOne");
                break;
            case 7:
                b8.setTransitionName("tileOne");
                trans.addSharedElement(b8, "tileOne");
                break;
            case 8:
                b9.setTransitionName("tileOne");
                trans.addSharedElement(b9, "tileOne");
                break;
            case 9:
                b10.setTransitionName("tileOne");
                trans.addSharedElement(b10, "tileOne");
                break;
            case 10:
                b11.setTransitionName("tileOne");
                trans.addSharedElement(b11, "tileOne");
                break;
            case 11:
                b12.setTransitionName("tileOne");
                trans.addSharedElement(b12, "tileOne");
                break;
            case 12:
                b13.setTransitionName("tileOne");
                trans.addSharedElement(b13, "tileOne");
                break;
            case 13:
                b14.setTransitionName("tileOne");
                trans.addSharedElement(b14, "tileOne");
                break;
            case 14:
                b15.setTransitionName("tileOne");
                trans.addSharedElement(b15, "tileOne");
                break;
            case 15:
                b16.setTransitionName("tileOne");
                trans.addSharedElement(b16, "tileOne");
                break;
            default:
                break;

        }
        switch (j) {
            case 0:
                b1.setTransitionName("tileTwo");
                trans.addSharedElement(b1, "tileTwo");
                break;
            case 1:
                b2.setTransitionName("tileTwo");
                trans.addSharedElement(b2, "tileTwo");
                break;
            case 2:
                b3.setTransitionName("tileTwo");
                trans.addSharedElement(b3, "tileTwo");
                break;
            case 3:
                b4.setTransitionName("tileTwo");
                trans.addSharedElement(b4, "tileTwo");
                break;
            case 4:
                b5.setTransitionName("tileTwo");
                trans.addSharedElement(b5, "tileTwo");
                break;
            case 5:
                b6.setTransitionName("tileTwo");
                trans.addSharedElement(b6, "tileTwo");
                break;
            case 6:
                b7.setTransitionName("tileTwo");
                trans.addSharedElement(b7, "tileTwo");
                break;
            case 7:
                b8.setTransitionName("tileTwo");
                trans.addSharedElement(b8, "tileTwo");
                break;
            case 8:
                b9.setTransitionName("tileTwo");
                trans.addSharedElement(b9, "tileTwo");
                break;
            case 9:
                b10.setTransitionName("tileTwo");
                trans.addSharedElement(b10, "tileTwo");
                break;
            case 10:
                b11.setTransitionName("tileTwo");
                trans.addSharedElement(b11, "tileTwo");
                break;
            case 11:
                b12.setTransitionName("tileTwo");
                trans.addSharedElement(b12, "tileTwo");
                break;
            case 12:
                b13.setTransitionName("tileTwo");
                trans.addSharedElement(b13, "tileTwo");
                break;
            case 13:
                b14.setTransitionName("tileTwo");
                trans.addSharedElement(b14, "tileTwo");
                break;
            case 14:
                b15.setTransitionName("tileTwo");
                trans.addSharedElement(b15, "tileTwo");
                break;
            case 15:
                b16.setTransitionName("tileTwo");
                trans.addSharedElement(b16, "tileTwo");
                break;
            default:
                break;

        }
        secondFragment.second = j;
        secondFragment.first = i;
        trans.replace(R.id.fragment_container,secondFragment).commit();
    }

}
