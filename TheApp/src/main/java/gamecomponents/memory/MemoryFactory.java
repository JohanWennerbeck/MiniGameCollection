package gamecomponents.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan 2018-06-15
 */


public class MemoryFactory implements IMemoryFactory {
    private static final IMemoryFactory memoryFactory = new MemoryFactory();

    public static IMemoryFactory getInstance() { return memoryFactory; }

    @Override
    public List<IMemoryTile> createMemoryTiles(int [] type) {
        List<IMemoryTile> memoryTiles = new ArrayList<>();
        for (int i : type){
            memoryTiles.add(new MemoryTile(i));
        }
        return memoryTiles;
    }

    public IMemory createMemory() {
        return new Memory();
    }
}
