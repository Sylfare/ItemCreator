package re.sylfa.itemcreator.items;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemRegistry {
    final Map<Key, CustomItem> itemList = new HashMap<>();

    public void add(CustomItem... item) {
        Arrays.stream(item).forEach(i -> itemList.put(i.key(), i));
    }

    public Optional<CustomItem> get(Key key) {
        return Optional.ofNullable(itemList.getOrDefault(key, null));
    }

    public boolean has(Key key) {
        return itemList.containsKey(key);
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
    
    public ItemStack parse(Key key) {
        // first, try to get Minecraft material

        if(key.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            Material material = Material.matchMaterial(key.value());
            if (material != null) {
                return new ItemStack(material);
            }
        }

        // it was not a Minecraft item, check if it's a custom one
        var itemRegistry = ItemCreator.getItemRegistry();
        Optional<CustomItem> item = itemRegistry.get(key);
        if(item.isPresent()) {
            return item.get().asItemStack();
        } else {
            Log.warn("Material not found: %s", key.asString());
            return null;
        }
    }
}
