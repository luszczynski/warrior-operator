package com.redhat.operator.customresource;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class GameDoneable extends CustomResourceDoneable<Game> {
	public GameDoneable(Game resource, Function<Game, Game> function) {
        super(resource, function);
    }
}
