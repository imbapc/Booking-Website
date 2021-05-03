package asg.concert.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import asg.concert.service.domain.Performer;
import asg.concert.service.domain.Genre;

public class PerformerDeserializer extends StdDeserializer<Performer> {

    private static Performer performer;

    public PerformerDeserializer() {
        this(null);
    }

    public PerformerDeserializer(Class<Performer> clazz) {
        super(clazz);
    }

    @Override
    public Performer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    	ArrayList<String> performerParameter = new ArrayList<>(Arrays.asList(jsonParser.getText().split(",")));
    	ArrayList<String> result = new ArrayList<> ();
    	for(int i = 2; i < 6; i++) {
    		String e = performerParameter.get(i);
    		int n = e.indexOf(": ");
    		e = e.substring(n + 2);
    		result.add(e);
    	}
    	Genre genre = Genre.valueOf(result.get(2));
        return new Performer(result.get(0), result.get(1), genre, result.get(3));
    }
}