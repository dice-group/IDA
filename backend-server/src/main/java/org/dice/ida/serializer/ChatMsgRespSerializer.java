package org.dice.ida.serializer;

import java.io.IOException;

import org.dice.ida.model.ChatMessageResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
/**
 * Class to help serialize {@link ChatMessageResponse} into JSON
 * @author Nikit
 *
 */
public class ChatMsgRespSerializer extends StdSerializer<ChatMessageResponse>{

	private static final long serialVersionUID = 1L;

	protected ChatMsgRespSerializer() {
		this(null);
	}
	protected ChatMsgRespSerializer(Class<ChatMessageResponse> t) {
		super(t);
	}

	@Override
	public void serialize(ChatMessageResponse value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeStringField("message", value.getMessage());
		gen.writeNumberField("uiAction", value.getUiAction());
		gen.writeObjectField("predefinedActions", value.getPredefinedActions());
		gen.writeObjectField("payload", value.getPayload());
		gen.writeObjectField("timestamp", value.getTimestamp());
		gen.writeObjectField("errorCode", value.getErrCode());
		gen.writeObjectField("activeContexts", value.getActiveContexts());
		gen.writeEndObject();

	}

}
