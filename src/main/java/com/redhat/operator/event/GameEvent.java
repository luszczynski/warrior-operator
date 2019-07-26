package com.redhat.operator.event;

import com.redhat.operator.customresource.Game;

import io.fabric8.kubernetes.client.Watcher;

public class GameEvent {
	private String uid;
	private Watcher.Action action;
	private Game resource;
	
	public GameEvent(String uid, Watcher.Action action, Game resource) {
		this.uid = uid;
		this.action = action;
		this.resource = resource;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Watcher.Action getAction() {
		return action;
	}

	public void setAction(Watcher.Action action) {
		this.action = action;
	}

	public Game getResource() {
		return resource;
	}

	public void setResource(Game resource) {
		this.resource = resource;
	}
	
	
}
