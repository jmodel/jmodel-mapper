package com.github.jmodel.mapper.plugin.validation

import org.w3c.dom.DOMErrorHandler
import org.w3c.dom.DOMError
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class SchemaErrorHandler implements DOMErrorHandler {

	private Log __log = LogFactory.getLog(SchemaErrorHandler)

	override public boolean handleError(DOMError error) {
		val boolean isWarning = (error.getSeverity() == DOMError.SEVERITY_WARNING);
		if (isWarning) {
			__log.warn("Schema error", error.getRelatedException() as Exception);
			__log.warn(error.getLocation().getUri() + ":" + error.getLocation().getLineNumber());
			__log.warn(error.getRelatedData());
			__log.warn(error.getRelatedException());
		} else {
			__log.error("Schema error", error.getRelatedException() as Exception)
			__log.error(error.getLocation().getUri() + ":" + error.getLocation().getLineNumber());
			__log.error(error.getRelatedData());
			__log.error(error.getRelatedException());
		}
		return isWarning;
	}

}
