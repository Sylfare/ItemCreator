package re.sylfa.itemcreator.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import re.sylfa.itemcreator.ItemCreator;

public class CustomItem {
    private static final String ID = "id";

    private Key key;
    private boolean hasCustomModelData = false;
    private int customModelData;


    public Key key() {
        return this.key;
    }

    public int customModelData() {
        return customModelData;
    }
    
    public ItemStack asItemStack() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        item.editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(null);
            meta.getPersistentDataContainer().set(NamespacedKey.fromString(ID, ItemCreator.getInstance()), PersistentDataType.STRING, key.asString());
            meta.itemName(Component.text(this.key.asString()));
            meta.setCustomModelData(this.hasCustomModelData ? customModelData : null);
        });

        return item;
    }

    public static class Builder {
        CustomItem item = new CustomItem();
        public static Builder builder() {
            return new CustomItem.Builder();
        }
        
        Builder key(Key key) {
            item.key = key;
            return this;
        }

        Builder customModelData(int customModelData) {
            if(customModelData != 0) {
                item.customModelData = customModelData;
                item.hasCustomModelData = true;
            }
            return this;
        }

        CustomItem build() {
            return item;
        }
    }
}
