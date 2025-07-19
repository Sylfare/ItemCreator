package re.sylfa.itemcreator.items;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@Getter
public class CustomItem {
    private Key key;
    private ItemStack item;

    public Key key() {
        return this.key;
    }

    public void key(Key key) {
        this.key = key;
    }

    public ItemStack asItemStack() {
        return item;
    }

    public static Builder builder(Key key) {
        return new Builder().key(key);
    }

    public static class Builder {
        CustomItem customItem = new CustomItem();
        
        private Builder key(Key key) {
            customItem.key = key;
            return this;
        }


        public Builder item(ItemStack item) {
            // add custom item ID into the customData component
            net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);

            CustomData customData = nmsCopy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .update(compoundTag -> compoundTag.put("itemcreator:id", StringTag.valueOf(this.customItem.key.asString())));

            nmsCopy.set(DataComponents.CUSTOM_DATA, customData);
            this.customItem.item = nmsCopy.asBukkitCopy();
            return this;
        }

        CustomItem build() {
            return this.customItem;
        }
    }
}
