package re.sylfa.itemcreator.parsers;

import java.util.Map;
import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import re.sylfa.itemcreator.util.Log;

public class PotionEffectParser {
    public static PotionEffect parse(Map<?, ?> rawPotionEffect) {
        if(rawPotionEffect instanceof Map potionEffectMap && potionEffectMap.containsKey("type") && potionEffectMap.containsKey("duration") && potionEffectMap.containsKey("amplifier")) {
            var rawType = potionEffectMap.get("type");
            var rawDuration = potionEffectMap.get("duration");
            var rawAmplifier = potionEffectMap.get("amplifier");
            var rawAmbient = potionEffectMap.get("ambient");
            var rawShowParticles = potionEffectMap.get("showParticles");
            var rawShowIcon = potionEffectMap.get("showIcon");


            if(rawType instanceof String parsedType && rawDuration instanceof Integer duration && rawAmplifier instanceof Integer amplifier) {
                NamespacedKey key = NamespacedKey.minecraft(parsedType.toLowerCase());
                var type = Optional.ofNullable(RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(key));
                if(type.isEmpty()) {
                    Log.warn("Bad potion type: %s", parsedType);

                    return null;
                }

                PotionEffect potionEffect = new PotionEffect(type.get(), duration, amplifier);
                if(rawAmbient instanceof Boolean ambient) {
                    potionEffect = potionEffect.withAmbient(ambient);
                } else {
                    potionEffect = potionEffect.withAmbient(false);
                }

                if(rawShowParticles instanceof Boolean showParticles) {
                    potionEffect = potionEffect.withParticles(showParticles);
                }
                if(rawShowIcon instanceof Boolean showIcon) {
                    potionEffect = potionEffect.withIcon(showIcon);
                }

                return potionEffect;
                
            } else {
                Log.warn("A potion effect should have at least a valid type, duration and amplifier");
                return null;
            }
        } else {
            return null;
        }
    }
}
