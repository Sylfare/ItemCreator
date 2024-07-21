package re.sylfa.itemcreator.recipes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.util.Log;

public class CustomRecipe {
    String keyValue;
    RecipeType type;
    Map<Character, RecipeChoice> shapedIngredients;
    List<RecipeChoice> ingredients;
    RecipeChoice ingredient;
    ItemStack result;
    String[] shape;
    int cookingTime;
    float cookingExperience;
    
    public Recipe asRecipe() {
        NamespacedKey key = NamespacedKey.fromString(keyValue, ItemCreator.getInstance());

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
                ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);

                shapedRecipe.shape(shape);
                shapedIngredients.forEach((ingredientKey, ingredient) -> shapedRecipe.setIngredient(ingredientKey, ingredient));
                return shapedRecipe;

            case SHAPELESS:
                if(ingredients == null || ingredients.size() == 0) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
                ingredients.forEach(ingredient -> shapelessRecipe.addIngredient(ingredient));
                return shapelessRecipe;
        
            case STONECUTTING:
                if(ingredient == null) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                return new StonecuttingRecipe(key, result, ingredient);

            case FURNACE:
                if(ingredient == null) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                return new FurnaceRecipe(key, result, ingredient, cookingExperience, cookingTime);

            case CAMPFIRE:
                if(ingredient == null) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                return new CampfireRecipe(key, result, ingredient, cookingExperience, cookingTime);

            case BLASTING:
                if(ingredient == null) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                return new BlastingRecipe(key, result, ingredient, cookingExperience, cookingTime);

            case SMOKING:
                if(ingredient == null) {
                    Log.warn("Recipe %s has no ingredients.", keyValue);
                    return null;
                }
                return new SmokingRecipe(key, result, ingredient, cookingExperience, cookingTime);

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
            RecipeType type = RecipeType.valueOf(rawType.toUpperCase());
            if(type != null) {
                recipe.type = type;
            } else {
                Log.warn("Unknown recipe type for %s: %s", recipe.keyValue, rawType);
            }

            return this;
        }

        Builder shapedIngredients(Map<Character, String> shapedIngredients) {
            if(shapedIngredients == null || shapedIngredients.entrySet().size() == 0) {
                return this;
            }

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

        Builder ingredients(List<String> ingredients) {
            if(ingredients.size() > 9) {
                Log.warn("Too many ingredients for recipe %s", recipe.keyValue);
                return this;
            }
            
            List<ItemStack> parsedIngredients = ingredients.stream().map(ingredient -> ItemCreator.getItemRegistry().parse(Key.key(ingredient))).toList();
            var unknownIngredient = ingredients.stream().filter(ingredient -> ingredient == null).findAny();
            if(unknownIngredient.isPresent()) {
                Log.warn("Unknown ingredient found for recipe %s", recipe.keyValue);
            }
            // HACK why .toList() is not working here??
            List<RecipeChoice> recipeChoices = parsedIngredients.stream().map(RecipeChoice.ExactChoice::new).collect(Collectors.toList());
            recipe.ingredients = recipeChoices;
            return this;
        }

        Builder ingredient(String ingredient) {
            if(ingredient == null || ingredient.isBlank()) {
                return this;
            }

            ItemStack item = itemRegistry.parse(Key.key(ingredient));
            if(item == null) {
                Log.warn("Unknown ingredient found for recipe %s", recipe.keyValue);
                return this;
            }

            recipe.ingredient = new RecipeChoice.ExactChoice(item);
            return this;
        }

        Builder result(String result, int amount) {
            if(amount < 1 || amount > 99) {
                Log.warn("Bad result amount for %s: %d", recipe.keyValue, amount);
                return this;
            }
            ItemStack item = itemRegistry.parse(Key.key(result));
            if(item != null) {
                item.setAmount(amount);
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

        Builder cooking(float experience, int cookingTime) {
            if(experience < 0) {
                Log.warn("Furnace experience is negative in recipe %s", recipe.keyValue);
                return this;
            }
            if(cookingTime < 1) {
                Log.warn("Cooking time is negative in recipe %s", recipe.keyValue);
                return this;
            }
            recipe.cookingExperience = experience;
            recipe.cookingTime = cookingTime;
            return this;
        }

        CustomRecipe build() {
            return recipe;
        }

    }
}
