package gamecomponents.memory;

import java.util.List;

/**
 * Created by Johan 2018-06-15
 */


public interface IMemory {
    List<IMemoryTile> getTiles();
    void onMemoryTileTappedEvent(int i);
    void newGame();
    void setTiles(List<IMemoryTile> memoryTiles);
}
