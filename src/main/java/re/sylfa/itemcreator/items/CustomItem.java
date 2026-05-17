package re.sylfa.itemcreator.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter @AllArgsConstructor @NoArgsConstructor
public class CustomItem {
    private Key key;
    private ItemStack item;
    private ItemKey useRemainder;

    public Key key() {
        return this.key;
    }

    public CustomItem(ItemStack item) {
        this.item = item;
    }

    CustomItem withKey(Key key) { return CustomItem.builder(key).item(getItem()).useRemainder(getUseRemainder()).build(); }

    public void key(Key key) {
        this.key = key;
    }

    public ItemStack asItemStack() {
        ItemStack finalItem = this.withItemId(key);
        if(useRemainder != null) {
            Optional.ofNullable(useRemainder.asItemStack())
                .ifPresent(remainder -> finalItem.editMeta(itemMeta -> itemMeta.setUseRemainder(remainder)));
        }
        return finalItem;
    }

    public static Builder builder(Key key) {
        return new Builder().key(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        CustomItem customItem = new CustomItem();
        
        private Builder key(Key key) {
            customItem.key = key;
            return this;
        }


        public Builder item(ItemStack item) {
            this.customItem.item = item;
            return this;
        }

        public Builder useRemainder(ItemKey remainderKey) {
            this.customItem.useRemainder = remainderKey;
            return this;
        }

        public CustomItem build() {
            return this.customItem;
        }
    }

    private ItemStack withItemId(Key key) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);

        CustomData customData = nmsCopy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .update(compoundTag -> compoundTag.put("itemcreator:id", StringTag.valueOf(key.asString())));

        nmsCopy.set(DataComponents.CUSTOM_DATA, customData);
        return nmsCopy.asBukkitCopy().asOne();
    }
}
