package edu.baylor.ecs.jparser.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;

import java.io.IOException;
import java.util.Map;

public class ModulePackageMapSerializer extends StdSerializer<Map<ModuleComponent, String>> {

    public ModulePackageMapSerializer() {
        this(null);
    }

    public ModulePackageMapSerializer(Class<Map<ModuleComponent, String>> item) {
        super(item);
    }

    @Override
    public void serialize(Map<ModuleComponent, String> moduleComponentStringMap,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("module_path_map");
        jsonGenerator.writeStartArray();
        for (ModuleComponent key : moduleComponentStringMap.keySet()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("module_name", key.getInstanceName());
            jsonGenerator.writeStringField("path", moduleComponentStringMap.get(key));
             jsonGenerator.writeNumberField("id", key.getId());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
