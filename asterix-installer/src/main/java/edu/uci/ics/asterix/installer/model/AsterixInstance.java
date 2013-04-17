/*
 * Copyright 2009-2012 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.installer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uci.ics.asterix.event.schema.cluster.Cluster;
import edu.uci.ics.asterix.event.schema.cluster.Node;
import edu.uci.ics.asterix.installer.schema.asterixconf.AsterixConfiguration;
import edu.uci.ics.asterix.installer.schema.asterixconf.Property;

public class AsterixInstance implements Serializable {

    private static final long serialVersionUID = 2874439550187520449L;

    public static final String CC_JAVA_OPTS = "cc_java_opts";
    public static final String NC_JAVA_OPTS = "nc_java_opts";

    public enum State {
        ACTIVE,
        INACTIVE,
        UNUSABLE
    }

    private final Cluster cluster;
    private final String name;
    private final Date createdTimestamp;
    private Date stateChangeTimestamp;
    private Date modifiedTimestamp;
    private AsterixConfiguration asterixConfiguration;
    private State state;
    private final String metadataNodeId;
    private final String asterixVersion;
    private final List<BackupInfo> backupInfo;
    private final String webInterfaceUrl;
    private AsterixRuntimeState runtimeState;
    private State previousState;

    public AsterixInstance(String name, Cluster cluster, AsterixConfiguration asterixConfiguration,
            String metadataNodeId, String asterixVersion) {
        this.name = name;
        this.cluster = cluster;
        this.asterixConfiguration = asterixConfiguration;
        this.metadataNodeId = metadataNodeId;
        this.state = State.ACTIVE;
        this.previousState = State.UNUSABLE;
        this.asterixVersion = asterixVersion;
        this.createdTimestamp = new Date();
        this.backupInfo = new ArrayList<BackupInfo>();
        this.webInterfaceUrl = "http://" + cluster.getMasterNode().getClientIp() + ":" + 19001;
    }

    public Date getModifiedTimestamp() {
        return stateChangeTimestamp;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.previousState = this.state;
        this.state = state;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Date getStateChangeTimestamp() {
        return stateChangeTimestamp;
    }

    public void setStateChangeTimestamp(Date stateChangeTimestamp) {
        this.stateChangeTimestamp = stateChangeTimestamp;
    }

    public void setModifiedTimestamp(Date modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public String getMetadataNodeId() {
        return metadataNodeId;
    }

    public String getAsterixVersion() {
        return asterixVersion;
    }

    public String getDescription(boolean detailed) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Name:" + name + "\n");
        buffer.append("Created:" + createdTimestamp + "\n");
        buffer.append("Web-Url:" + webInterfaceUrl + "\n");
        buffer.append("State:" + state);
        if (!state.equals(State.UNUSABLE) && stateChangeTimestamp != null) {
            buffer.append(" (" + stateChangeTimestamp + ")" + "\n");
        } else {
            buffer.append("\n");
        }
        if (modifiedTimestamp != null) {
            buffer.append("Last modified timestamp:" + modifiedTimestamp + "\n");
        }

        if (runtimeState.getSummary() != null && runtimeState.getSummary().length() > 0) {
            buffer.append("\nWARNING!:" + runtimeState.getSummary() + "\n");
        }
        if (detailed) {
            addDetailedInformation(buffer);
        }
        return buffer.toString();
    }

    public List<BackupInfo> getBackupInfo() {
        return backupInfo;
    }

    public String getWebInterfaceUrl() {
        return webInterfaceUrl;
    }

    public AsterixRuntimeState getAsterixRuntimeState() {
        return runtimeState;
    }

    public void setAsterixRuntimeStates(AsterixRuntimeState runtimeState) {
        this.runtimeState = runtimeState;
    }

    private void addDetailedInformation(StringBuffer buffer) {
        buffer.append("Master node:" + cluster.getMasterNode().getId() + ":" + cluster.getMasterNode().getClusterIp()
                + "\n");
        for (Node node : cluster.getNode()) {
            buffer.append(node.getId() + ":" + node.getClusterIp() + "\n");
        }

        if (backupInfo != null && backupInfo.size() > 0) {
            for (BackupInfo info : backupInfo) {
                buffer.append(info + "\n");
            }
        }
        buffer.append("\n");
        buffer.append("Asterix version:" + asterixVersion + "\n");
        buffer.append("Metadata Node:" + metadataNodeId + "\n");
        buffer.append("Processes" + "\n");
        for (ProcessInfo pInfo : runtimeState.getProcesses()) {
            buffer.append(pInfo + "\n");
        }

        buffer.append("\n");
        buffer.append("Asterix Configuration\n");
        for (Property property : asterixConfiguration.getProperty()) {
            buffer.append(property.getName() + ":" + property.getValue() + "\n");
        }

    }

    public State getPreviousState() {
        return previousState;
    }

    public AsterixConfiguration getAsterixConfiguration() {
        return asterixConfiguration;
    }

    public void setAsterixConfiguration(AsterixConfiguration asterixConfiguration) {
        this.asterixConfiguration = asterixConfiguration;
    }
}
