/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.config;

import com.google.common.base.Joiner;
import org.jboss.hal.config.rebind.EnvironmentGenerator;
import org.jboss.hal.config.semver.Version;

import java.util.List;

import static org.jboss.hal.config.OperationMode.DOMAIN;
import static org.jboss.hal.config.OperationMode.STANDALONE;
import static org.jboss.hal.config.InstanceInfo.WILDFLY;

/**
 * A base implementation for the environment.
 *
 * @see EnvironmentGenerator
 * @author Harald Pehl
 */
@SuppressWarnings("unused")
public abstract class AbstractEnvironment implements Environment {

    private final Version halVersion;
    private final List<String> locales;
    private final InstanceInfo instanceInfo;
    private OperationMode operationMode;
    private Version managementVersion;

    protected AbstractEnvironment(final String halVersion, final List<String> locales) {
        this.halVersion = Version.valueOf(halVersion);
        this.locales = locales;
        this.instanceInfo = WILDFLY;
        this.operationMode = STANDALONE;
        this.managementVersion = Version.forIntegers(0, 0, 0);
    }

    @Override
    public Version getHalVersion() {
        return halVersion;
    }

    @Override
    public List<String> getLocales() {
        return locales;
    }

    @Override
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    @Override
    public void setInstanceInfo(final String productName, final String productVersion,
            final String releaseName, final String releaseVersion,
            final String serverName) {
        instanceInfo.update(productName, productVersion, releaseName, releaseVersion, serverName);
    }

    @Override
    public OperationMode getOperationMode() {
        return operationMode;
    }

    @Override
    public boolean isStandalone() {
        return operationMode == STANDALONE;
    }

    @Override
    public void setOperationMode(final String launchType) {
        operationMode = (STANDALONE.name().equals(launchType)) ? STANDALONE : DOMAIN;
    }

    @Override
    public Version getManagementVersion() {
        return managementVersion;
    }

    @Override
    public void setManagementVersion(final String major, final String micro, final String minor) {
        managementVersion = Version.valueOf(Joiner.on('.').join(major, micro, minor));
    }

    @Override
    public String toString() {
        return "Environment{HAL " + halVersion + ", " + instanceInfo + ", management version " + managementVersion + '}';
    }
}
