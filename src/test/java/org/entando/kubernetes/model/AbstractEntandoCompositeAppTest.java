/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.compositeapp.DoneableEntandoCompositeApp;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeApp;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeAppBuilder;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureBuilder;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerBuilder;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoCompositeAppTest implements CustomResourceTestUtil {

    public static final String MY_COMPOSITE_APP = "my-comnposite-app";
    public static final String MY_KEYCLOAK = "my-keycloak";
    public static final String MY_CLUSTER_INFRASTRUCTURE = "my-cluster-infrastructure";
    public static final String MY_APP = "my-app";
    public static final String MY_PLUGIN = "my-plugin";
    public static final String MY_APP_PLUGIN_LINK = "my-app-plugin-link";
    public static final String MY_DATABASE_SERVICE = "my-database-service";
    private static final String MY_NAMESPACE = "my-namespace";
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoCompositeApps() {
        registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(entandoCompositeApps(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoCompositeApp() {
        //Given
        EntandoCompositeApp entandoCompositeApp = new EntandoCompositeAppBuilder()
                .withNewMetadata().withName(MY_COMPOSITE_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .addNewEntandoKeycloakServer().withNewMetadata().withName(MY_KEYCLOAK).endMetadata().endEntandoKeycloakServer()
                .addNewEntandoClusterInfrastructure().withNewMetadata().withName(MY_CLUSTER_INFRASTRUCTURE).endMetadata()
                .endEntandoClusterInfrastructure()
                .addNewEntandoApp().withNewMetadata().withName(MY_APP).endMetadata().endEntandoApp()
                .addNewEntandoPlugin().withNewMetadata().withName(MY_PLUGIN).endMetadata().endEntandoPlugin()
                .addNewEntandoAppPluginLink().withNewMetadata().withName(MY_APP_PLUGIN_LINK).endMetadata().endEntandoAppPluginLink()
                .addNewEntandoDatabaseService().withNewMetadata().withName(MY_DATABASE_SERVICE).endMetadata().endEntandoDatabaseService()
                .endSpec()
                .build();
        entandoCompositeApps().inNamespace(MY_NAMESPACE).createNew().withMetadata(entandoCompositeApp.getMetadata())
                .withSpec(entandoCompositeApp.getSpec())
                .done();
        //When
        EntandoCompositeApp actual = entandoCompositeApps().inNamespace(MY_NAMESPACE).withName(MY_COMPOSITE_APP).get();
        //Then
        assertThat(actual.getSpec().getComponents().get(0).getMetadata().getName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getComponents().get(1).getMetadata().getName(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getSpec().getComponents().get(2).getMetadata().getName(), is(MY_APP));
        assertThat(actual.getSpec().getComponents().get(3).getMetadata().getName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getComponents().get(4).getMetadata().getName(), is(MY_APP_PLUGIN_LINK));
        assertThat(actual.getSpec().getComponents().get(5).getMetadata().getName(), is(MY_DATABASE_SERVICE));
        assertThat(actual.getMetadata().getName(), is(MY_COMPOSITE_APP));
    }

    @Test
    public void testEditEntandoCompositeApp() {
        //Given
        EntandoCompositeApp entandoCompositeApp = new EntandoCompositeAppBuilder()
                .withNewMetadata()
                .withName(MY_COMPOSITE_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .addNewEntandoKeycloakServer().withNewMetadata().withName("some-keycloak").endMetadata().endEntandoKeycloakServer()
                .addNewEntandoClusterInfrastructure().withNewMetadata().withName("some-cluster-infrastructure").endMetadata()
                .endEntandoClusterInfrastructure()
                .addNewEntandoApp().withNewMetadata().withName("some-app").endMetadata().endEntandoApp()
                .addNewEntandoPlugin().withNewMetadata().withName("some-plugin").endMetadata().endEntandoPlugin()
                .addNewEntandoAppPluginLink().withNewMetadata().withName("some-link").endMetadata().endEntandoAppPluginLink()
                .addNewEntandoDatabaseService().withNewMetadata().withName("some-database-service").endMetadata()
                .endEntandoDatabaseService()
                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug
        entandoCompositeApps().inNamespace(MY_NAMESPACE).create(entandoCompositeApp);
        EntandoCompositeApp actual = entandoCompositeApps().inNamespace(MY_NAMESPACE).withName(MY_COMPOSITE_APP).edit()
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withComponents(
                        new EntandoKeycloakServerBuilder().withNewMetadata().withName(MY_KEYCLOAK).endMetadata().build(),
                        new EntandoClusterInfrastructureBuilder().withNewMetadata().withName(MY_CLUSTER_INFRASTRUCTURE).endMetadata()
                                .build(),
                        new EntandoAppBuilder().withNewMetadata().withName(MY_APP).endMetadata().build(),
                        new EntandoPluginBuilder().withNewMetadata().withName(MY_PLUGIN).endMetadata().build(),
                        new EntandoAppPluginLinkBuilder().withNewMetadata().withName(MY_APP_PLUGIN_LINK).endMetadata().build(),
                        new EntandoDatabaseServiceBuilder().withNewMetadata().withName(MY_DATABASE_SERVICE).endMetadata().build()
                )
                .endSpec()
                .withStatus(new WebServerStatus("my-keycloak"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getMetadata().getName(), is(MY_COMPOSITE_APP));
        assertThat(actual.getSpec().getComponents().get(0).getMetadata().getName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getComponents().get(1).getMetadata().getName(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getSpec().getComponents().get(2).getMetadata().getName(), is(MY_APP));
        assertThat(actual.getSpec().getComponents().get(3).getMetadata().getName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getComponents().get(4).getMetadata().getName(), is(MY_APP_PLUGIN_LINK));
        assertThat(actual.getSpec().getComponents().get(5).getMetadata().getName(), is(MY_DATABASE_SERVICE));
        assertThat(actual.getStatus(), is(notNullValue()));
    }

    protected CustomResourceOperationsImpl<EntandoCompositeApp, CustomResourceList<EntandoCompositeApp>,
            DoneableEntandoCompositeApp> entandoCompositeApps() {
        return registry.getOperations(EntandoCompositeApp.class);

    }
}