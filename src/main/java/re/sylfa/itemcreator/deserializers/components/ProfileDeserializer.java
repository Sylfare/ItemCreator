package re.sylfa.itemcreator.deserializers.components;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.JavaUtils;
import re.sylfa.itemcreator.util.Parsers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ProfileDeserializer extends StdDeserializer<ResolvableProfile> {
    public ProfileDeserializer() {
        super(ResolvableProfile.class);
    }
    @Override
    public ResolvableProfile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.readValueAsTree();
        if(node == null) {
            return null;
        }

        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

        Parsers.getStringNodeValue(node, "name").ifPresent(builder::name);
        Parsers.getUUIDNodeValue(node, "uuid").ifPresent(builder::uuid);

        if(node.has("property")) {
            Parsers.getNodeValue(node, "property", JsonNode::isObject, ProfileProperty.class)
                .ifPresent(builder::addProperty);
        } else if (node.has("properties")) {
            JsonNode properties = node.get("properties");
            JavaUtils.iteratorStream(properties.elements())
                .map(currentProperty -> ItemCreator.getMapper().convertValue(currentProperty, ProfileProperty.class))
                .filter(Objects::nonNull)
                .forEach(builder::addProperty);
        }

        return builder.build();
    }

    public static class ProfilePropertyDeserializer extends StdDeserializer<ProfileProperty> {
        public ProfilePropertyDeserializer() {
            super(ProfileProperty.class);
        }

        @Override
        public ProfileProperty deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if(node == null) {
                return null;
            }

            Optional<String> name = Parsers.getStringNodeValue(node, "name");
            if(name.isEmpty()) {
                return null;
            }
            Optional<String> value = Parsers.getStringNodeValue(node, "value");
            if(value.isEmpty()) {
                return null;
            }

            Optional<String> signature = Parsers.getStringNodeValue(node, "signature");

            return new ProfileProperty(name.get(), value.get(), signature.orElse(null));
        }
    }
}
