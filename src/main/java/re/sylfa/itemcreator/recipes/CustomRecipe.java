package re.sylfa.itemcreator.recipes;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.util.Log;

public class CustomRecipe {
    String keyValue;
    RecipeType type;
    Map<Character, RecipeChoice> shapedIngredients;
    ItemStack result;
    String[] shape;
    
    public Recipe asRecipe() {
        if(result == null || result.getType() == Material.AIR) {
            Log.warn("Recipe %s has no result", keyValue);
            return null;
        }
        switch (type) {
            case SHAPED:
                if(shape.length == 0) {
                    Log.warn("Shape for %s is not set", keyValue);
                    return null;
                }
                if(shapedIngredients == null || shapedIngredients.entrySet().size() == 0) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.fromString(keyValue, ItemCreator.getInstance()), result);

                recipe.shape(shape);
                shapedIngredients.forEach((key,ingredient) -> recipe.setIngredient(key, ingredient));
                return recipe;
        
            default:
                return null;
        }
    }

    public static class Builder {
        CustomRecipe recipe = new CustomRecipe();
        private ItemRegistry itemRegistry = ItemCreator.getItemRegistry();

        public static Builder builder(String keyValue, String type) {
            return new Builder().keyValue(keyValue).type(type);
        }

        private Builder keyValue(String keyValue) {
            recipe.keyValue = keyValue;
            return this;
        }

        private Builder type(String rawType) {
            RecipeType type = RecipeType.valueOf(rawType);
            if(type != null) {
                recipe.type = type;
            } else {
                Log.warn("Unknown recipe type for %s: %s", recipe.keyValue, rawType);
            }

            return this;
        }

        Builder shapedIngredients(Map<Character, String> shapedIngredients) {
            Map<Character, ItemStack> parsedIngredients = shapedIngredients.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> itemRegistry.parse(Key.key(entry.getValue()))
                ));
            var unknownIngredient = parsedIngredients.entrySet().stream()
                .filter(ingredient -> ingredient.getValue() == null).findAny();
            if(unknownIngredient.isPresent()) {
                Log.warn("Unknown ingredient for %s: %s", recipe.keyValue, shapedIngredients.get(unknownIngredient.get().getKey()));
                return this;
            }

            recipe.shapedIngredients = parsedIngredients.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new RecipeChoice.ExactChoice(entry.getValue())
            ));
            
            return this;
        }

        Builder result(String result, int amount) {
            if(amount < 1 || amount > 99) {
                Log.warn("Bad result amount for %s: %d", recipe.keyValue, amount);
                return this;
            }
            ItemStack item = itemRegistry.parse(Key.key(result));
            if(item != null) {
                recipe.result = item;
            } else {
                Log.warn("Bad result material for %s: %s", recipe.keyValue, result);
            }
            return this;
        }

        Builder shape(String[] shape) {
            if(shape.length > 3) {
                Log.warn("Shape for %s has too many lines", recipe.keyValue);
                return this;
            }

            var lineTooLong = Arrays.stream(shape).filter(line -> line.length() > 3).findAny();
            if(lineTooLong.isPresent()) {
                Log.warn("Shape for %s has a line that is too long", recipe.keyValue);
                return this;
            }

            recipe.shape = shape;
            return this;
        }

        CustomRecipe build() {
            return recipe;
        }

    }
}
