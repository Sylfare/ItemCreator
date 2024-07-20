package re.sylfa.itemcreator.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class CustomItem {
    private Key key;
    public Key key() {
        return this.key;
    }
    
    public ItemStack asItemStack() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        item.editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(null);
            meta.itemName(Component.text(this.key.asString()));
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

        CustomItem build() {
            return item;
        }
    }
}
