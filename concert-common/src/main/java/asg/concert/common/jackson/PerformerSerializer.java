package asg.concert.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import lab.end2end.concert.domain.Performer;

public class PerformerSerializer extends StdSerializer<Performer>{
	private static Performer performer;
	
	public PerformerSerializer() {this(null);}
	
	public PerformerSerializer(Class<Performer> clazz) {
		super(clazz);
	}
	
	@Override
	public void serialize(Performer performer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(performer.toString());
    }
	
}