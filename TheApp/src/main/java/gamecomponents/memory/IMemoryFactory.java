package gamecomponents.memory;

import java.util.List;

/**
 * Created by Johan 2018-06-15
 */


public interface IMemoryFactory {

    IMemory createMemory();
    List<IMemoryTile> createMemoryTiles(int[] type);
}
