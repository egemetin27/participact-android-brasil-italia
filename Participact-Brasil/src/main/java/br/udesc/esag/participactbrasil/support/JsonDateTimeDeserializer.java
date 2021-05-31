package br.udesc.esag.participactbrasil.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;


public class JsonDateTimeDeserializer extends JsonDeserializer<DateTime> {

    private final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return formatter.parseDateTime(jp.getText());
    }
}
