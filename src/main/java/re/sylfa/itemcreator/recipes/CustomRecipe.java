package re.sylfa.itemcreator.recipes;

import net.kyori.adventure.key.Key;

public class CustomRecipe {
    Key key;

    public static class Builder {
        CustomRecipe recipe = new CustomRecipe();

        public static Builder builder(Key key) {
            return new Builder().key(key);
        }

        private Builder key(Key key) {
            recipe.key = key;
            return this;
        }

        CustomRecipe build() {
            return recipe;
        }
    }
}
