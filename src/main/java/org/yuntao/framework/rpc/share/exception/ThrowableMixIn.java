package org.yuntao.framework.rpc.share.exception;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Mix-in class for {@link Throwable} to ignore those fields while serializing
 * and deserializing.
 * 
 * <pre>
 * mapper.getSerializationConfig().addMixInAnnotations(Throwable.class, ThrowableMixIn.class);
 * mapper.getDeserializationConfig().addMixInAnnotations(Throwable.class, ThrowableMixIn.class);
 * </pre>
 * 
 * @see ObjectMapper#getSerializationConfig()
 * @see ObjectMapper#getDeserializationConfig()
 * @see SerializationConfig#addMixInAnnotations(Class, Class)
 * @see DeserializationConfig#addMixInAnnotations(Class, Class)
 * 
 * @author Karel Hovorka
 * 
 */
public abstract class ThrowableMixIn {
	@JsonIgnore
	StackTraceElement[] stackTrace;

	@JsonIgnore
	abstract StackTraceElement[] getStackTrace();

	@JsonIgnore
	abstract String getLocalizedMessage();

	@JsonIgnore
	abstract Throwable getCause();

}
