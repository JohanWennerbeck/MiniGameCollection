package gamecomponents.memory;

/**
 * Created by Johan 2018-06-15
 */


public class MemoryTile implements IMemoryTile {
    public static final int S_POLICE = 0;
    public static final int S_HORSE = 1;
    public static final int S_COW = 2;
    public static final int S_AMBULANCE = 3;
    public static final int S_TRACTOR = 4;
    public static final int S_AIRPLANE = 5;
    public static final int S_HELICOPTER = 6;
    public static final int S_BIKE = 7;

    private int type;
    private boolean checked;
    private int clickedNumber;


    public MemoryTile(int type){
        this.type = type;
        checked = false;
        this.clickedNumber = 0;
    }

    public MemoryTile(){}

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean getChecked() {
        return this.checked;
    }

    @Override
    public void toggleChecked() {
        this.checked = !this.checked;
    }

    @Override
    public void setChecked(boolean bool) {
        this.checked = bool;
    }

    @Override
    public void setClickedNumber(int i) {
        this.clickedNumber = i;
    }

    @Override
    public int getClickedNumber() {
        return this.clickedNumber;
    }


}
