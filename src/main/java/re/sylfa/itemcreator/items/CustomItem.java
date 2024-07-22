package re.sylfa.itemcreator.items;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.JukeboxSong;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.persistence.PersistentDataType;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponents;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

public class CustomItem {
    private static final String ID = "id";

    private Key key;
    private boolean hasCustomModelData = false;
    private int customModelData;
    private Component itemName;
    private Material material;
    private List<Component> lore;
    private int maxDamage;
    private boolean hasMaxDamage = false;
    private int maxStackSize;
    private JukeboxPlayableComponent jukeboxPlayableComponent;
    private boolean hasEnchantmentGlintOverride;
    private boolean enchantmentGlintOverride;

    private boolean isTool = false;
    private float toolDefaultMiningSpeed;
    private int toolDamagePerBlock;
    public List<ToolRule> toolRules;

    public Key key() {
        return this.key;
    }

    public int customModelData() {
        return customModelData;
    }

    public Component itemName() {
        return itemName;
    }

    public Material material() {
        return material;
    }

    public List<Component> lore() {
        return lore;
    }

    public int maxDamage() {
        return maxDamage;
    }

    public int maxStackSize() {
        return maxStackSize;
    }

    public JukeboxPlayableComponent jukeboxPlayableComponent() {
        return jukeboxPlayableComponent;
    }

    public ItemStack asItemStack() {
        var itemNms = CraftItemStack.asNMSCopy(new ItemStack(material));
        // setMaxDamage(null) only resets the *patched* value, not the actual one
        itemNms.remove(DataComponents.MAX_DAMAGE);
        itemNms.remove(DataComponents.TOOL);
        itemNms.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        itemNms.remove(DataComponents.FOOD);

        ItemStack item = itemNms.asBukkitCopy();
        item.editMeta(Damageable.class, meta -> {
            meta.getPersistentDataContainer().set(NamespacedKey.fromString(ID, ItemCreator.getInstance()), PersistentDataType.STRING, key.asString());
            meta.itemName(itemName);
            meta.setCustomModelData(this.hasCustomModelData ? customModelData : null);
            meta.lore(lore);
            if(hasMaxDamage) {
                meta.setMaxDamage(maxDamage);
            }
            meta.setMaxStackSize(maxStackSize);
            meta.setJukeboxPlayable(jukeboxPlayableComponent);
            if(hasEnchantmentGlintOverride) {
                meta.setEnchantmentGlintOverride(enchantmentGlintOverride);
            }
            if(isTool) {
                ToolComponent tool = meta.getTool();
                tool.setDamagePerBlock(toolDamagePerBlock);
                tool.setDefaultMiningSpeed(toolDefaultMiningSpeed);
                toolRules.forEach(toolRule -> tool.addRule(toolRule.material(), toolRule.miningSpeed(), toolRule.correctForDrops()));
                meta.setTool(tool);
            }
        });

        return item;
    }

    public static class Builder {
        CustomItem item = new CustomItem();
        private MiniMessage mm = MiniMessage.miniMessage();

        public static Builder builder(Key key) {
            return new CustomItem.Builder().key(key);
        }
        
        private Builder key(Key key) {
            item.key = key;
            return this;
        }

        Builder customModelData(int customModelData) {
            if(customModelData != 0) {
                item.customModelData = customModelData;
                item.hasCustomModelData = true;
            }
            return this;
        }

        Builder itemName(String rawItemName) {
            return itemName(mm.deserialize(rawItemName));
        }

        Builder itemName(Component itemName) {
            item.itemName = itemName;
            return this;
        }

        Builder material(Material material) {
            item.material = material;
            return this;
        }

        Builder lore(List<String> lore) {
            if(!lore.isEmpty()) {
                item.lore = lore.stream().map(line -> mm.deserialize("<!italic><grey>"+line+"</grey></!italic>")).toList();
            }
            return this;
        }

        Builder maxDamage(int maxDamage) {
            if(maxDamage > 0) {
                item.maxDamage = maxDamage;
                item.hasMaxDamage = true;
            } else if (maxDamage < 0) {
                Log.warn("Negative max damage for %s", item.key.asString());
            }

            return this;
        }

        Builder maxStackSize(int maxStackSize) {
            if(maxStackSize < 1 || maxStackSize > 99) {
                maxStackSize = 1;
                Log.warn(String.format("Max stack size for %s is not valid", item.key.asString()));
            }
            
            item.maxStackSize = maxStackSize;
            return this;
        }

        Builder jukeboxPlayableComponent(String rawJukeboxSong, boolean showInTooltip) {
            if(rawJukeboxSong == null) {
                return this;
            }
            JukeboxSong jukeboxSong = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).get(Key.key(rawJukeboxSong));
            if(jukeboxSong != null) {
                // HACK there's no JukeboxPlayableComponent constructor
                JukeboxPlayableComponent jukeboxPlayableComponent = new ItemStack(Material.STONE).getItemMeta().getJukeboxPlayable();
                jukeboxPlayableComponent.setSong(jukeboxSong);
                jukeboxPlayableComponent.setShowInTooltip(showInTooltip);
                return jukeboxPlayableComponent(jukeboxPlayableComponent);
            }
            return this;
        }
        
        Builder jukeboxPlayableComponent(JukeboxPlayableComponent jukeboxPlayableComponent) {
            item.jukeboxPlayableComponent = jukeboxPlayableComponent;
            return this;
        }

        Builder enchantmentGlintOverride(boolean set, boolean override) {
            item.hasEnchantmentGlintOverride = set;
            item.enchantmentGlintOverride = override;
            return this;
        }

        Builder tool(ConfigurationSection tool) {
            if(tool == null) {
                return this;
            }
            
            item.isTool = true;
            item.toolDamagePerBlock = tool.getInt("damagePerBlock", 1);
            item.toolDefaultMiningSpeed = (float) tool.getDouble("defaultMiningSpeed", 1);
            
            // verify if each rule is well formed
            List<ToolRule> toolRules = tool.getList("rules").stream().map(rule -> {
                if(rule instanceof Map map) {
                    return map;
                } else {
                    Log.warn("A tool rule for item %s is invalid.", item.key.asString());
                    return null;
                }
            })
            .filter(rule -> rule != null)
            // parse each rule
            .map(rule -> {
                Object rawMaterials = rule.get("materials");
                List<Material> materials = new ArrayList<Material>();
                if(rawMaterials instanceof List materialList) {
                    for(Object str : materialList) {
                        if(str instanceof String rawMaterial) {
                            Material material = Material.matchMaterial(rawMaterial);
                            if(material != null) {
                                materials.add(material);
                            } else {
                                Log.warn("Material for tool item %s not found: %s", item.key.asString(), rawMaterial);
                                return null;
                            }
                        } else {
                            Log.warn("Malformed material for tool item %s", item.key.asString());
                            return null;
                        }
                    }
                } else {
                    Log.warn("A rule's material list for tool item %s is malformed", item.key.asString());
                    return null;
                }

                Float miningSpeed = null;
                Object rawMiningSpeed = rule.get("miningSpeed");
                if(rawMiningSpeed != null) {
                    try {
                        miningSpeed = Float.valueOf(rawMiningSpeed.toString());
                    } catch (Exception e) {
                        Log.warn("Error while parsing raw mining speed for tool %s", item.key.asString());
                    }
                }

                Boolean correctForDrops = null;
                Object rawCorrectForDrops = rule.get("correctForDrops");
                if(rawCorrectForDrops != null) {
                    correctForDrops = rawCorrectForDrops.toString().toLowerCase().trim().equals("true");
                }
                

                return new ToolRule(materials, miningSpeed, correctForDrops);
            })
            .toList()
            ;
            item.toolRules = toolRules;
            return this;
        }

        CustomItem build() {
            return item;
        }
    }

    record ToolRule(Collection<Material> material, Float miningSpeed, Boolean correctForDrops) { }
}
