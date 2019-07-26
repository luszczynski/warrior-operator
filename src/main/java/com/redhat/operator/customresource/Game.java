package com.redhat.operator.customresource;

import io.fabric8.kubernetes.client.CustomResource;

public class Game extends CustomResource {

	private static final long serialVersionUID = 1L;
	
	private GameSpec spec;

	public GameSpec getSpec() {
		return spec;
	}

	public void setSpec(GameSpec spec) {
		this.spec = spec;
	}
	

}
