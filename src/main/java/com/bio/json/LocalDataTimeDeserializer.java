package com.bio.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDataTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        String[] dt = text.split(" +");
        String[] ds = dt[0].split("\\.");
        String[] ts = dt[1].split(":");
        LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(ds[2]), Integer.parseInt(ds[1]), Integer.parseInt(ds[0]), Integer.parseInt(ts[0]), Integer.parseInt(ts[1]));
        return localDateTime;
    }
}
