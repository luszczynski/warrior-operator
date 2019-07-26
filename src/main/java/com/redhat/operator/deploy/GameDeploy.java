package com.redhat.operator.deploy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.operator.event.GameEvent;

import io.fabric8.openshift.client.OpenShiftClient;

@ApplicationScoped
public class GameDeploy {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameDeploy.class);
	
	@Inject
    OpenShiftClient client;
	
	public void deployEvent(@ObservesAsync GameEvent event) {
		Integer numberOfPlayer = Integer.parseInt(event.getResource().getSpec().getNumberOfPlayers());
		
		for (int i = 0; i < numberOfPlayer; i++) {
			// Create replica set
		}
	}
}
