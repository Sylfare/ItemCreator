package re.sylfa.itemcreator.items;

import net.kyori.adventure.key.Key;

public class CustomItem {
    private Key key;
    public Key key() {
        return this.key;
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
