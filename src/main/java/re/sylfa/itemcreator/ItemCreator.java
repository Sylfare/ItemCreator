package re.sylfa.itemcreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.permissions.CommandPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import re.sylfa.itemcreator.commands.ItemCreatorCommand;
import re.sylfa.itemcreator.deserializers.components.EquippableDeserializer;
import re.sylfa.itemcreator.deserializers.components.ToolDeserializer;
import re.sylfa.itemcreator.deserializers.types.*;
import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.items.ItemReader;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.recipes.CustomRecipe;
import re.sylfa.itemcreator.recipes.RecipeReader;
import re.sylfa.itemcreator.recipes.RecipeRegistry;
import re.sylfa.itemcreator.util.Log;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemCreator extends JavaPlugin {

    @Getter
    private static ItemCreator instance;
    @Getter
    private static final ItemRegistry itemRegistry = new ItemRegistry();
    @Getter
    private static final RecipeRegistry recipeRegistry = new RecipeRegistry();

    @Getter
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void onEnable() {
        instance = this;
        Log.log("Enabled!");
        registerMappers();

        init();
        registerPermissions();
        registerCommands();
    }


    public void init(){
        itemRegistry.removeAll();
        itemRegistry.add(ItemReader.readAllItems().toArray(CustomItem[]::new));
        

        List<CustomRecipe> readRecipes = RecipeReader.readAllRecipes();
        Map<NamespacedKey, Recipe> readRecipesMap = readRecipes.stream()
            .collect(Collectors.toMap(
                CustomRecipe::key,
                CustomRecipe::asRecipe
            ));
        recipeRegistry.getAll().keySet().forEach(key -> Log.log("Removing %s: %b", key.asString(), Bukkit.removeRecipe(key)));
        recipeRegistry.removeAll();

        readRecipes.forEach(recipeRegistry::add);
        readRecipesMap.values().forEach(r -> Bukkit.addRecipe(r, true));
    }

    public void reload() {
        init();
    }

    private void registerPermissions() {
        CommandPermissions.registerPermissions(new Permission("itemcreator.give", PermissionDefault.OP));
    }

    private void registerCommands() {
        LifecycleEventManager<@NonNull Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            List.of(
                new ItemCreatorCommand()
            ).forEach(command -> commands.register(command.command(), command.aliases()));            
        });
    }

    private void registerMappers() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ItemStack.class, new ItemDeserializer())
            .addDeserializer(Material.class, new MaterialDeserializer())
            .addDeserializer(Component.class, new TextComponentDeserializer())
            .addDeserializer(NamespacedKey.class, new NamespacedKeyDeserializer())
            .addDeserializer(EquippableComponent.class, new EquippableDeserializer())
            .addDeserializer(Key.class, new KeyDeserializer())
            .addDeserializer(ToolComponent.class, new ToolDeserializer());

        mapper.registerModule(module);

    }
}
