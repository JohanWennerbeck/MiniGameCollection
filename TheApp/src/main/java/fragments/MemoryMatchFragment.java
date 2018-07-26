package fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import gamecomponents.MemoryTurn;
import mini.game.collection.R;

public class MemoryMatchFragment extends Fragment   {
        Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16;
        Button t1, t2;
        public int first, second;
    private MemoryMatchFragment.Listener mListener = null;

    interface Listener {
        MemoryTurn getMemoryData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.memory_game_match, container, false);
        t1 = rootView.findViewById(R.id.tile_one);
        t2 = rootView.findViewById(R.id.tile_two);
        t1.setTransitionName("tileOne");
        t2.setTransitionName("tileTwo");
        b1= rootView.findViewById(R.id.bm1);
        b2= rootView.findViewById(R.id.bm2);
        b3= rootView.findViewById(R.id.bm3);
        b4= rootView.findViewById(R.id.bm4);
        b5= rootView.findViewById(R.id.bm5);
        b6= rootView.findViewById(R.id.bm6);
        b7= rootView.findViewById(R.id.bm7);
        b8= rootView.findViewById(R.id.bm8);
        b9= rootView.findViewById(R.id.bm9);
        b10= rootView.findViewById(R.id.bm10);
        b11= rootView.findViewById(R.id.bm11);
        b12= rootView.findViewById(R.id.bm12);
        b13= rootView.findViewById(R.id.bm13);
        b14= rootView.findViewById(R.id.bm14);
        b15= rootView.findViewById(R.id.bm15);
        b16= rootView.findViewById(R.id.bm16);

        System.out.println("DEETTTTTA HÄÄÄÄNDDEEEER");
        setTexts();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTexts(){

            Button[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16};

            for (int i = 0; i < 16; i++){
                    if(getData().data.getTiles().get(i).getChecked()){
                        buttons[i].setText(memoryTexts[getData().data.getTiles().get(i).getType()]);
                    } else {
                        System.out.println(i);
                        System.out.println(buttons[i]);
                        System.out.println(buttons[i].toString());
                        buttons[i].setText("O");
                    }
            }
            t1.setText(memoryTexts[getData().data.getTiles().get(first).getType()]);
            t2.setText(memoryTexts[getData().data.getTiles().get(second).getType()]);
            buttons[first].setVisibility(View.INVISIBLE);
            buttons[second].setVisibility(View.INVISIBLE);

    }
    public String[] memoryTexts = {"Police", "Horse", "Cow", "Ambulance", "Tractor", "Airplane","Helicopter","Bike"};

    public MemoryTurn getData(){
        return mListener.getMemoryData();
    }
    public void setListener(MemoryMatchFragment.Listener listener) {
        this.mListener = listener;
    }
}
