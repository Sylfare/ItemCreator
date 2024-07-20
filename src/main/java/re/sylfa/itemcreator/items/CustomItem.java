package re.sylfa.itemcreator.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
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
        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_PICKAXE));
        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);

        ItemStack item = itemNms.asBukkitCopy();
        item.editMeta(Damageable.class, meta -> {
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
