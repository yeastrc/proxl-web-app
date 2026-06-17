package org.yeastrc.xlink.www.webservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * JAX-RS provider that supplies the Jackson {@link ObjectMapper} used for JSON
 * (de)serialization of REST webservice responses, with the Joda-Time module registered.
 *
 * <p>Jackson 2.x (pulled to 2.18.x transitively by Jersey 3.1.x) does not handle
 * {@code org.joda.time} types by default. Several DTOs returned by the webservices expose
 * {@code org.joda.time.DateTime} (e.g. {@code PDBFileDTO.uploadDate}), which previously
 * failed with: "Joda date/time type ... not supported by default: add Module
 * jackson-datatype-joda to enable handling".
 *
 * <p>This class is in package {@code org.yeastrc.xlink.www.webservices}, which is already
 * listed in {@code jersey.config.server.provider.packages} in web.xml, so Jersey discovers
 * it automatically. The {@code @Provider} {@link ContextResolver} is consulted by the
 * Jackson JAX-RS provider to locate the {@code ObjectMapper}.
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper objectMapper;

	public ObjectMapperContextResolver() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule( new JodaModule() );
	}

	@Override
	public ObjectMapper getContext( Class<?> type ) {
		return objectMapper;
	}
}
