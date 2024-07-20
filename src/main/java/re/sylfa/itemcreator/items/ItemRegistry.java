package re.sylfa.itemcreator.items;

import java.util.Map;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Arrays;
import net.kyori.adventure.key.Key;

public class ItemRegistry {
    final Map<Key, CustomItem> itemList = new HashMap<>();

    public void add(CustomItem... item) {
        Arrays.stream(item).forEach(i -> itemList.put(i.key(), i));;
    }

    @Nullable
    public CustomItem get(Key key) {
        return itemList.getOrDefault(key, null);
    }

    public Map<Key, CustomItem> getAll() {
        return itemList;
    }

    public boolean remove(Key key) {
        return itemList.remove(key) != null;
    }

    public void removeAll() {
        itemList.clear();
    }
    
}
