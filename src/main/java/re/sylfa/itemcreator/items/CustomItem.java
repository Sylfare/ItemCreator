package re.sylfa.itemcreator.items;

import java.util.List;

import org.bukkit.JukeboxSong;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataType;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponents;
import re.sylfa.itemcreator.ItemCreator;

public class CustomItem {
    private static final String ID = "id";

    private Key key;
    private boolean hasCustomModelData = false;
    private int customModelData;
    private Component itemName;
    private Material material = Material.DIAMOND_PICKAXE;
    private List<Component> lore;
    private int maxDamage;
    private boolean hasMaxDamage = false;
    private int maxStackSize;
    private JukeboxPlayableComponent jukeboxPlayableComponent;

    public Key key() {
        return this.key;
    }

    public int customModelData() {
        return customModelData;
    }

    public Component itemName() {
        return itemName;
    }

    public Material material() {
        return material;
    }

    public List<Component> lore() {
        return lore;
    }

    public int maxDamage() {
        return maxDamage;
    }

    public int maxStackSize() {
        return maxStackSize;
    }

    public JukeboxPlayableComponent jukeboxPlayableComponent() {
        return jukeboxPlayableComponent;
    }
    
    public ItemStack asItemStack() {
        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(material));
        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        itemNms.remove(DataComponents.FOOD);

        ItemStack item = itemNms.asBukkitCopy();
        item.editMeta(Damageable.class, meta -> {
            meta.getPersistentDataContainer().set(NamespacedKey.fromString(ID, ItemCreator.getInstance()), PersistentDataType.STRING, key.asString());
            meta.itemName(itemName);
            meta.setCustomModelData(this.hasCustomModelData ? customModelData : null);
            meta.lore(lore);
            if(hasMaxDamage) meta.setMaxDamage(maxDamage);
            meta.setMaxStackSize(maxStackSize);
            meta.setJukeboxPlayable(jukeboxPlayableComponent);
        });

        return item;
    }

    public static class Builder {
        CustomItem item = new CustomItem();
        private MiniMessage mm = MiniMessage.miniMessage();

        public static Builder builder(Key key) {
            return new CustomItem.Builder().key(key);
        }
        
        private Builder key(Key key) {
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

        Builder itemName(String rawItemName) {
            return itemName(mm.deserialize(rawItemName));
        }

        Builder itemName(Component itemName) {
            item.itemName = itemName;
            return this;
        }

        Builder material(Material material) {
            item.material = material;
            return this;
        }

        Builder lore(List<String> lore) {
            if(!lore.isEmpty()) {
                item.lore = lore.stream().map(line -> mm.deserialize(line)).toList();
            }
            return this;
        }

        Builder maxDamage(int maxDamage) {
            if(maxDamage > 0) {
                item.maxDamage = maxDamage;
                item.hasMaxDamage = true;
            }

            return this;
        }

        Builder maxStackSize(int maxStackSize) {
            if(maxStackSize < 1 || maxStackSize > 99) {
                maxStackSize = 1;
            }
            
            item.maxStackSize = maxStackSize;
            return this;
        }

        Builder jukeboxPlayableComponent(String rawJukeboxSong, boolean showInTooltip) {
            if(rawJukeboxSong == null) {
                return this;
            }
            JukeboxSong jukeboxSong = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).get(Key.key(rawJukeboxSong));
            if(jukeboxSong != null) {
                // HACK there's no JukeboxPlayableComponent builder
                JukeboxPlayableComponent jukeboxPlayableComponent = new ItemStack(Material.STONE).getItemMeta().getJukeboxPlayable();
                jukeboxPlayableComponent.setSong(jukeboxSong);
                jukeboxPlayableComponent.setShowInTooltip(showInTooltip);
                return jukeboxPlayableComponent(jukeboxPlayableComponent);
            }
            return this;
        }
        
        Builder jukeboxPlayableComponent(JukeboxPlayableComponent jukeboxPlayableComponent) {
            item.jukeboxPlayableComponent = jukeboxPlayableComponent;
            return this;
        }

        CustomItem build() {
            return item;
        }
    }
}
