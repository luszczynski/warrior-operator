package com.redhat.operator.producer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import com.redhat.operator.customresource.Game;
import com.redhat.operator.customresource.GameDoneable;
import com.redhat.operator.customresource.GameList;

import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class ClientProducer {
	/**
	   * Produces privately current namespace
	   * @return String
	   * @throws IOException
	   */
	  @Produces
	  @Singleton
	  @Named("namespace")
	  public String findNamespace() throws IOException {
	    return new String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace")));
	  }

	  /**
	   * Produce kubernetes client
	   * @param namespace
	   * @return KubernetesClient
	   */
	  @Produces
	  @Singleton
	  OpenShiftClient newClient(@Named("namespace") String namespace) {
	    return new DefaultOpenShiftClient().inNamespace(namespace);
	    //return new DefaultKubernetesClient().inNamespace(namespace);
	  }

	  /**
	   * Close Kubernetes client
	   */
	  void closeClient(@Disposes KubernetesClient kc) {
	    kc.close();
	  }

	  /**
	   * Produces Custom Resource Client (NonNamespaceOperation)
	   */
	  @Produces
	  @Singleton
	  NonNamespaceOperation<Game, GameList, GameDoneable, Resource<Game, GameDoneable>> makeCustomResourceClient(KubernetesClient defaultClient, @Named("namespace") String namespace) {
	    KubernetesDeserializer.registerCustomKind("redhat.com/v1alpha1", "Game", Game.class);

	    // Filter only SEI CRD
	    CustomResourceDefinition crd = defaultClient.customResourceDefinitions()
	      .list()
	      .getItems()
	      .stream()
	      .filter(d -> "game.redhat.com".equals(d.getMetadata().getName()))
	      .findAny()
	      .orElseThrow(() -> new RuntimeException("Deployment error: Custom resource definition game.redhat.com not found."));

	    return defaultClient
	        .customResources(crd, Game.class, GameList.class, GameDoneable.class)
	        .inNamespace(namespace);
	  }
}
