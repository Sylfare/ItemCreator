package re.sylfa.itemcreator.deserializers.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Color;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ColorDeserializer extends StdDeserializer<Color> {
    private Color getColorByName(String name) {
        return switch (name.toUpperCase()) {
            case "AQUA" -> Color.AQUA;
            case "BLACK" -> Color.BLACK;
            case "BLUE" -> Color.BLUE;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "GRAY" -> Color.GRAY;
            case "GREEN" -> Color.GREEN;
            case "LIME" -> Color.LIME;
            case "MAROON" -> Color.MAROON;
            case "NAVY" -> Color.NAVY;
            case "OLIVE" -> Color.OLIVE;
            case "ORANGE" -> Color.ORANGE;
            case "PURPLE" -> Color.PURPLE;
            case "RED" -> Color.RED;
            case "SILVER" -> Color.SILVER;
            case "TEAL" -> Color.TEAL;
            case "WHITE" -> Color.WHITE;
            case "YELLOW" -> Color.YELLOW;
            default -> null;
        };
    }
    public ColorDeserializer() { super(Color.class); }

    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        // as an RGB int value
        if(node.isIntegralNumber()) {
            int colorNumber = node.asInt();
            if(colorNumber < 0 || colorNumber > 0xFFFFFF) {
                Log.error("%s is not a valid color int.", colorNumber);
                return null;
            }

            return Color.fromRGB(colorNumber);
        }

        if(node.isTextual()) {
            String textValue = node.asText();
            // as an hex value #RRGGBB or #AARRGGBB
            if(textValue.startsWith("#") && (textValue.length() == 7 || textValue.length() == 9)) {
                int rgbValue = Integer.valueOf(textValue.substring(1), 16);
                if(textValue.length() == 7) {
                    return Color.fromRGB(rgbValue);
                } else {
                    return Color.fromARGB(rgbValue);
                }
            } else {
                // with color name
                return getColorByName(node.asText());
            }
        }

        // as an array of 3 or 4 ints for (alpha), red, green, blue [255, 2, 140, 255]
        if(node.isArray()) {
            List<Integer> numbers = new ArrayList<>();
            for(JsonNode value : node) {
                if(!value.isIntegralNumber() || value.asInt() < 0 || value.asInt() > 255 ) {
                    Log.error("\"%s\" is not a valid number");
                    return null;
                }
                numbers.add(value.intValue());
            }
            if(numbers.size() < 3 || numbers.size() > 4) {
                Log.error("%s is not a valid color");
            }
            int[] intValues = numbers.stream().mapToInt(i -> i).toArray();
            if(intValues.length == 3) {
                return Color.fromRGB(intValues[0], intValues[1], intValues[2]);
            } else {
                return Color.fromARGB(intValues[0], intValues[1], intValues[2], intValues[3]);
            }
        }

        // as an object {"red": 255, "blue": 120, "green": 41, "alpha": 255}
        if(node.isObject()) {
            Map<String, Object> map = ItemCreator.getMapper().convertValue(node, Map.class);
            Optional<Integer> red = Parsers.getIntNodeValue(node, "red");
            Optional<Integer> green = Parsers.getIntNodeValue(node, "green");
            Optional<Integer> blue = Parsers.getIntNodeValue(node, "blue");
            Optional<Integer> alpha = Parsers.getIntNodeValue(node, "alpha");
            if(red.isEmpty() || blue.isEmpty() || green.isEmpty()) {
                return null;
            }
            return Color.fromARGB(
                alpha.orElse(255),
                red.get(),
                green.get(),
                blue.get()
            );
        }

        return null;
    }
}
