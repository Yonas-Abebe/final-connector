package com.mycompany;

import java.util.Map;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class CustomParameters {
	
	

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@Optional(defaultValue = "#[{}]")
	private Map<String, String> postPayload;
	
	public Map<String, String> getPostPayload() {
		return postPayload;
	}

	public void setPostPayload(Map<String, String> postPayload) {
		this.postPayload = postPayload;
	}

	

}
