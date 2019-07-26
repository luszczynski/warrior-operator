package com.redhat.operator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.operator.cache.GameCache;
import com.redhat.operator.customresource.Game;
import com.redhat.operator.event.GameEvent;

import io.fabric8.kubernetes.client.Watcher;
import io.quarkus.runtime.StartupEvent;
import io.reactivex.plugins.RxJavaPlugins;

@ApplicationScoped
public class WarriorOperator {
	private static final Logger LOGGER = LoggerFactory.getLogger(WarriorOperator.class);

	@Inject
    GameCache cache;
	
	@Inject
    Event<GameEvent> gameEvent;
	
	void onStartup(@Observes StartupEvent _ev) {
        LOGGER.info("Starting the SEI Operator...");
        new Thread(this::runWatch).start();
        RxJavaPlugins.setErrorHandler(t -> onError(new GlobalErrorEvent(t)));
    }
	
	// This is a synchronous event handler, i.e. calls to globalErrorEvent.fire()
    // will never return but trigger System.exit() immediately.
    void onError(@Observes GlobalErrorEvent ev) {
        LOGGER.error(ev.getMessage(), ev.getCause());
        Runtime.getRuntime().halt(1);
    }
	
	private void runWatch() {
        cache.listThenWatch(this::handleEvent);
    }
	
	private void handleEvent(Watcher.Action action, String uid) {
		Game resource = cache.get(uid);
        
        if (resource == null) {
            LOGGER.info("Resource is null");
            return;
        }
        
        
        this.gameEvent.fireAsync(new GameEvent(uid, action, resource));
    }
}
