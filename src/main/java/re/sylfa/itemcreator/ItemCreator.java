package re.sylfa.itemcreator;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.permissions.CommandPermissions;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import re.sylfa.itemcreator.commands.ItemCreatorCommand;
import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.items.ItemReader;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.recipes.RecipeReader;
import re.sylfa.itemcreator.util.Log;

public class ItemCreator extends JavaPlugin {
    
    private static ItemCreator instance;
    private static ItemRegistry itemRegistry = new ItemRegistry();

    @Override
    public void onEnable() {
        instance = this;
        Log.log("Enabled!");
        init();
        registerPermissions();
        registerCommands();
    }

    public static ItemCreator getInstance() {
        return instance;
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public void init(){
        itemRegistry.add(ItemReader.readAllItems().toArray(CustomItem[]::new));
        
        RecipeReader.readAllRecipes().forEach(recipe -> Bukkit.addRecipe(recipe.asRecipe()));;
    }

    public void registerPermissions() {
        CommandPermissions.registerPermissions(new Permission("itemcreator.give", PermissionDefault.OP));
    }

    public void registerCommands() {
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            List.of(
                new ItemCreatorCommand()
            ).forEach(command -> commands.register(command.command(), command.aliases()));            
        });
    }
}
