package re.sylfa.itemcreator.recipes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public class RecipeRegistry {

    Map<NamespacedKey, CustomRecipe> recipes = new HashMap<NamespacedKey, CustomRecipe>();

    public void add(CustomRecipe... recipe) {
        Arrays.stream(recipe).forEach(r -> {
            if(r.asRecipe() instanceof Keyed keyed) { // only MerchantRecipe seems to be non-keyed, so it should be fine
                recipes.put(keyed.getKey(), r);
            }
        });
    }

    public CustomRecipe get(NamespacedKey key) {
        return recipes.get(key);
    }

    public Map<NamespacedKey, CustomRecipe> getAll() {
        return recipes;
    }

    public void removeAll() {
        recipes.clear();
    }

    public int size() {
        return recipes.size();
    }
}