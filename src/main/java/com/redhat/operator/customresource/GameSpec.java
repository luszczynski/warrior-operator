package com.redhat.operator.customresource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@RegisterForReflection
public class GameSpec {
	@JsonProperty("numberOfPlayers")
    private String numberOfPlayers;

	public String getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(String numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	
	
}
