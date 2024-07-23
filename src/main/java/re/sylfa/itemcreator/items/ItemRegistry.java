package re.sylfa.itemcreator.items;

import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Arrays;
import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

public class ItemRegistry {
    final Map<Key, CustomItem> itemList = new HashMap<>();

    public void add(CustomItem... item) {
        Arrays.stream(item).forEach(i -> itemList.put(i.key(), i));;
    }

    @Nullable
    public CustomItem get(Key key) {
        return itemList.getOrDefault(key, null);
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

        if(key.namespace() == Key.MINECRAFT_NAMESPACE) {
            Material material = Material.matchMaterial(key.value());
            if (material != null) return new ItemStack(material);
        }

        // it was not a Minecraft item, check if it's a custom one
        var itemRegistry = ItemCreator.getItemRegistry();
        if(itemRegistry.has(key)) {
            return itemRegistry.get(key).asItemStack();
        };

        Log.warn("Material not found: %s", key.asString());
        return null;
    }

    public CustomItem matchCustomItem(ItemStack itemStack) {
        if(itemStack.getPersistentDataContainer().has(CustomItem.ID)) {
            NamespacedKey key = NamespacedKey.fromString(itemStack.getPersistentDataContainer().get(CustomItem.ID, PersistentDataType.STRING));
            return itemList.get(key);
        }
        return null;
    }
}
