package re.sylfa.itemcreator;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.permissions.CommandPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import re.sylfa.itemcreator.commands.ItemCreatorCommand;
import re.sylfa.itemcreator.deserializers.components.ConsumableDeserializer;
import re.sylfa.itemcreator.deserializers.components.ConsumeEffectDeserializer;
import re.sylfa.itemcreator.deserializers.components.DeathProtectionDeserializer;
import re.sylfa.itemcreator.deserializers.components.EnchantableDeserializer;
import re.sylfa.itemcreator.deserializers.components.EquippableDeserializer;
import re.sylfa.itemcreator.deserializers.components.FoodDeserializer;
import re.sylfa.itemcreator.deserializers.components.JukeboxPlayableDeserializer;
import re.sylfa.itemcreator.deserializers.components.MusicInstrumentDeserializer;
import re.sylfa.itemcreator.deserializers.components.PotionEffectDeserializer;
import re.sylfa.itemcreator.deserializers.components.PotionEffectTypeDeserializer;
import re.sylfa.itemcreator.deserializers.components.ProfileDeserializer;
import re.sylfa.itemcreator.deserializers.components.ToolDeserializer;
import re.sylfa.itemcreator.deserializers.components.ToolRuleDeserializer;
import re.sylfa.itemcreator.deserializers.types.ColorDeserializer;
import re.sylfa.itemcreator.deserializers.types.CustomItemDeserializer;
import re.sylfa.itemcreator.deserializers.types.ItemDeserializer;
import re.sylfa.itemcreator.deserializers.types.ItemKeyDeserializer;
import re.sylfa.itemcreator.deserializers.types.KeyDeserializer;
import re.sylfa.itemcreator.deserializers.types.MaterialDeserializer;
import re.sylfa.itemcreator.deserializers.types.NamespacedKeyDeserializer;
import re.sylfa.itemcreator.deserializers.types.TextComponentDeserializer;
import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.items.ItemKey;
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
        recipeRegistry.getAll().keySet()
            .forEach(key -> {
                boolean recipeRemoved = Bukkit.removeRecipe(key);
                // TODO toggleable in configuration
//                Log.log("Removing %s: %b", key.asString(), recipeRemoved);
                }
            );
        recipeRegistry.removeAll();

        readRecipes.forEach(recipeRegistry::add);
        // FIXME resendRecipes = false does not work for now
        readRecipesMap.values().forEach(r -> Bukkit.addRecipe(r, false));
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
        module.addDeserializer(CustomItem.class, new CustomItemDeserializer())
            .addDeserializer(ItemStack.class, new ItemDeserializer())
            .addDeserializer(Material.class, new MaterialDeserializer())
            .addDeserializer(Component.class, new TextComponentDeserializer())
            .addDeserializer(NamespacedKey.class, new NamespacedKeyDeserializer())
            .addDeserializer(EquippableComponent.class, new EquippableDeserializer())
            .addDeserializer(Key.class, new KeyDeserializer())
            .addDeserializer(ToolComponent.class, new ToolDeserializer())
            .addDeserializer(ToolRuleDeserializer.RuleValues.class, new ToolRuleDeserializer())
            .addDeserializer(JukeboxPlayableComponent.class, new JukeboxPlayableDeserializer())
            .addDeserializer(Consumable.class, new ConsumableDeserializer())
            .addDeserializer(FoodComponent.class, new FoodDeserializer())
            .addDeserializer(ItemKey.class, new ItemKeyDeserializer())
            .addDeserializer(PotionEffect.class, new PotionEffectDeserializer())
            .addDeserializer(DeathProtection.class, new DeathProtectionDeserializer())
            .addDeserializer(ConsumeEffect.class, new ConsumeEffectDeserializer())
            .addDeserializer(PotionEffectType.class, new PotionEffectTypeDeserializer())
            .addDeserializer(Color.class, new ColorDeserializer())
            .addDeserializer(MusicInstrument.class, new MusicInstrumentDeserializer())
            .addDeserializer(Enchantable.class, new EnchantableDeserializer())
            .addDeserializer(ResolvableProfile.class, new ProfileDeserializer())
            .addDeserializer(ProfileProperty.class, new ProfileDeserializer.ProfilePropertyDeserializer())
        ;

        mapper.registerModule(module);

    }
}
