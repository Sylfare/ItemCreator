package re.sylfa.itemcreator.items;

import net.kyori.adventure.key.Key;

public class CustomItem {
    Key key;
    
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
