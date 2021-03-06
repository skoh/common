package com.nemustech.adapter.soap;

import javax.xml.soap.SOAPMessage;

import com.nemustech.adapter.SOAPAdapter;
import com.nemustech.adapter.aspect.AuditRequired;
import com.nemustech.adapter.exception.AdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SOAPAdapterImpl implements SOAPAdapter {
	@Autowired
	private SaajWsClient soapClient;

	@AuditRequired
	public <T1, T2> T2 sendAndReceive(String url, T1 params, ISOAPMessageMapper<T1, T2> mapper) throws AdapterException {
		SOAPMessage message = mapper.mappingRequestParamToSOAPMessage(params);

		try {
			SOAPMessage response = soapClient.sendAndReceive(url, message);

			T2 result = mapper.mappingSOAPMessageToResponse(response);
			return result;
		} catch (AdapterException e) {
			throw e;
		} catch (Exception e) {
			throw new AdapterException("SOAP001", "Connect soap url \"" + url + "\" error", e);
		}
	}
}
