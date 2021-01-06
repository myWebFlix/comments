package com.webflix.comments.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-config")
public class RestConfig {

	public boolean isDisableCommentsSubmit() {
		return disableCommentsSubmit;
	}

	public void setDisableCommentsSubmit(boolean disableCommentsSubmit) {
		this.disableCommentsSubmit = disableCommentsSubmit;
	}

	@ConfigValue(watch = true)
	private boolean disableCommentsSubmit;
}
