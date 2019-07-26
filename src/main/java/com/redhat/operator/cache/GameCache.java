package com.redhat.operator.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.operator.customresource.Game;
import com.redhat.operator.customresource.GameDoneable;
import com.redhat.operator.customresource.GameList;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

@ApplicationScoped
public class GameCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCache.class);

    // Cache for SEI object. String is the uid for the object. 
    private final Map<String, Game> cache = new ConcurrentHashMap<>();

    // Custom Resource client
    @Inject
    NonNamespaceOperation<Game, GameList, GameDoneable, Resource<Game, GameDoneable>> crClient;

    Executor executor = Executors.newSingleThreadExecutor();

    public Game get(String uid) {
        return cache.get(uid);
    }

    public void listThenWatch(BiConsumer<Watcher.Action, String> callback) {

        try {

            // List all SEI resource and put uid into cache
            crClient
                .list()
                .getItems()
                .forEach(resource -> {
                    cache.put(resource.getMetadata().getUid(), resource);
                    String uid = resource.getMetadata().getUid();
                    executor.execute(() -> callback.accept(Watcher.Action.ADDED, uid));
                    
                });

            // watch

            crClient.watch(new Watcher<Game>() {
                @Override
                public void eventReceived(Action action, Game resource) {
                    try {
                        String uid = resource.getMetadata().getUid();
                        
                        if (cache.containsKey(uid)) {
                            int knownResourceVersion = Integer
                                    .parseInt(cache.get(uid).getMetadata().getResourceVersion());
                            int receivedResourceVersion = Integer.parseInt(resource.getMetadata().getResourceVersion());
                            if (knownResourceVersion > receivedResourceVersion) {
                                return;
                            }
                        }
                        System.out.println("received " + action + " for resource " + resource);
                        if (action == Action.ADDED || action == Action.MODIFIED) {
                            cache.put(uid, resource);
                        } else if (action == Action.DELETED) {
                            cache.remove(uid);
                        } else {
                            System.err.println("Received unexpected " + action + " event for " + resource);
                            System.exit(-1);
                        }
                        executor.execute(() -> callback.accept(action, uid));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }

                @Override
                public void onClose(KubernetesClientException cause) {
                    cause.printStackTrace();
                    System.exit(-1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}