package gamecomponents.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Johan 2018-06-15
 */

public class Memory implements IMemory {

    private List<IMemoryTile> memoryTiles;
    private int firstTry;
    private int secondTry;
    private boolean done;
    private boolean correct;
    Player playerOne;
    Player playerTwo;
    private int turn;


    public Memory(){
        this.memoryTiles = new ArrayList<>();
        initMemory();
    }

    private void initMemory(){
        int [] type = {MemoryTile.S_AMBULANCE,
                MemoryTile.S_COW,
                MemoryTile.S_HORSE,
                MemoryTile.S_POLICE,
                MemoryTile.S_AIRPLANE,
                MemoryTile.S_BIKE,
                MemoryTile.S_HELICOPTER,
                MemoryTile.S_TRACTOR,
                MemoryTile.S_AMBULANCE,
                MemoryTile.S_COW,
                MemoryTile.S_HORSE,
                MemoryTile.S_POLICE,
                MemoryTile.S_AIRPLANE,
                MemoryTile.S_BIKE,
                MemoryTile.S_HELICOPTER,
                MemoryTile.S_TRACTOR,
        };
        shuffle(type);


        memoryTiles = MemoryFactory.getInstance().createMemoryTiles(type);
        firstTry = -1;
        secondTry = -1;
        done = false;
        turn = 1;
    }
    void shuffle(int[] arr){
        Random rand = new Random();
        for (int i = arr.length-1; i > 0; i--){
            int j = rand.nextInt(i);
            int k = arr[i];
            arr[i] = arr[j];
            arr[j] = k;
        }
    }

    @Override
    public List<IMemoryTile> getTiles() {
        return this.memoryTiles;
    }

    public void onMemoryTileTappedEvent(int i) {

        if(firstTry != -1 && correct == false && done == true){
            turnAroundWrongGuesses();
            if(turn == playerOne.getPlayer()) {
                turn = 2;
            } else {
                turn = 1;
            }
        }
        if (firstTry == -1){
            performFirst(i);
        } else if (!done){
            performSecond(i);
        }
        System.out.println("The score is :" + playerOne.getScore() + " - " + playerTwo.getScore());
        if (playerTwo.getScore() + playerOne.getScore() == this.memoryTiles.size()/2){
            System.out.println("We have a winner!!! ");
        }
    }

    private void turnAroundWrongGuesses() {
        System.out.println("7");
        this.memoryTiles.get(secondTry).toggleChecked();
        this.memoryTiles.get(firstTry).toggleChecked();

        firstTry = -1;
        done = false;
    }

    private void performSecond(int i) {
        System.out.println("3");
        if(!this.memoryTiles.get(i).getChecked()) {
            System.out.println("4");
            this.memoryTiles.get(i).toggleChecked();
            secondTry = i;
            if (this.memoryTiles.get(firstTry).getType() == this.memoryTiles.get(i).getType()) {
                System.out.println("5");
                correct = true;
                firstTry = -1;
                increaseScore();
            } else {
                System.out.println("6");
                correct = false;
                done = true;
            }
        }
    }

    private void increaseScore() {
        if ( turn == playerOne.getPlayer()){
            playerOne.increaseScore();
        } else {
            playerTwo.increaseScore();
        }
    }

    private void performFirst(int i) {
        System.out.println("1");
        if(!this.memoryTiles.get(i).getChecked()) {
            System.out.println("2");
            this.memoryTiles.get(i).toggleChecked();
            firstTry = i;

        }
    }

    public void switchTurn(int tileColor){

    }

    public void checkForVictory(int tile){

    }


    private class Player {
        private int player;
        private int score;

        private Player(int player){
            this.player = player;
            score = 0;
        }

        public void increaseScore (){
            this.score++;
        }

        public int getScore () {
            return this.score;
        }

        public int getPlayer () {
            return player;
        }
    }

    public void newGame () {
        initMemory();
    }

    @Override
    public void setTiles(List<IMemoryTile> memoryTiles) {
        this.memoryTiles = memoryTiles;
    }
}
