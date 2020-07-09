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

package org.entando.kubernetes.model.bundle;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoComponentBundle extends EntandoComponentBundleFluent<DoneableEntandoComponentBundle> implements
        DoneableEntandoCustomResource<DoneableEntandoComponentBundle, EntandoComponentBundle> {

    private final EntandoCustomResourceStatus status;
    private final Function<EntandoComponentBundle, EntandoComponentBundle> function;

    public DoneableEntandoComponentBundle(Function<EntandoComponentBundle, EntandoComponentBundle> function) {
        this.function = function;
        this.status = new EntandoCustomResourceStatus();
    }

    public DoneableEntandoComponentBundle(EntandoComponentBundle resource, Function<EntandoComponentBundle, EntandoComponentBundle> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableEntandoComponentBundle withStatus(AbstractServerStatus serverStatus) {
        this.status.putServerStatus(serverStatus);
        return this;
    }

    @Override
    public DoneableEntandoComponentBundle withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoComponentBundle done() {
        return function.apply(new EntandoComponentBundle(metadata.build(), spec.build(), status));
    }
}
