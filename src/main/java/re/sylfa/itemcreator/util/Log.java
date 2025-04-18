package re.sylfa.itemcreator.util;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Log {
    String PREFIX = "[ItemCreator] ";

    static void log(Component message){
        Bukkit.getConsoleSender().sendMessage(Component.text(PREFIX).append(message));
    }

    static void log(String message, Object... args){
        Log.log(Component.text(String.format(message, args)));
    }


    static void warn(String message, Object... args){
        Log.warn(Component.text(String.format(message, args)));
    }

    static void warn(Component message){
        log(message.color(NamedTextColor.RED));
    }

    static void error(Component message) { log(message.color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));}

    static void error(String message, Object... args) { Log.error(Component.text(String.format(message, args)));}
}
