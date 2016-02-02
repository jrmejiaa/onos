/*
 * Copyright 2016 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.cpman.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.onlab.osgi.ServiceDirectory;
import org.onlab.osgi.TestServiceDirectory;
import org.onlab.rest.BaseResource;
import org.onosproject.cpman.ControlPlaneMonitorService;
import org.onosproject.net.DeviceId;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.util.Optional;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Unit test for ControlMetricsCollector.
 */
public class ControlMetricsCollectorResourceTest extends JerseyTest {

    final ControlPlaneMonitorService mockControlPlaneMonitorService =
                                     createMock(ControlPlaneMonitorService.class);

    private static final String PREFIX = "collector";

    /**
     * Sets up the global values for all the tests.
     */
    @Before
    public void setUpTest() {
        ServiceDirectory testDirectory =
                new TestServiceDirectory()
                        .add(ControlPlaneMonitorService.class, mockControlPlaneMonitorService);
        BaseResource.setServiceDirectory(testDirectory);
    }

    @Test
    public void testCpuMetricsPost() {
        mockControlPlaneMonitorService.updateMetric(anyObject(), anyObject(),
                (Optional<DeviceId>) anyObject());
        expectLastCall().times(5);
        replay(mockControlPlaneMonitorService);
        basePostTest("cpu-metrics-post.json", PREFIX + "/cpu_metrics");
    }

    @Test
    public void testMemoryMetricsPost() {
        mockControlPlaneMonitorService.updateMetric(anyObject(), anyObject(),
                (Optional<DeviceId>) anyObject());
        expectLastCall().times(4);
        replay(mockControlPlaneMonitorService);
        basePostTest("memory-metrics-post.json", PREFIX + "/memory_metrics");
    }

    @Test
    public void testDiskMetricsPost() {
        mockControlPlaneMonitorService.updateMetric(anyObject(), anyObject(),
                (Optional<DeviceId>) anyObject());
        expectLastCall().times(2);
        replay(mockControlPlaneMonitorService);
        basePostTest("disk-metrics-post.json", PREFIX + "/disk_metrics");
    }

    @Test
    public void testNetworkMetricsPost() {
        mockControlPlaneMonitorService.updateMetric(anyObject(), anyObject(),
                (Optional<DeviceId>) anyObject());
        expectLastCall().times(4);
        replay(mockControlPlaneMonitorService);
        basePostTest("network-metrics-post.json", PREFIX + "/network_metrics");
    }

    @Test
    public void testSystemSpecsPost() {
        basePostTest("system-spec-post.json", PREFIX + "/system_specs");
    }

    private void basePostTest(String jsonFile, String path) {
        final WebResource rs = resource();
        InputStream jsonStream = ControlMetricsCollectorResourceTest.class
                .getResourceAsStream(jsonFile);

        assertThat(jsonStream, notNullValue());

        ClientResponse response = rs.path(path)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, jsonStream);
        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
    }

    public ControlMetricsCollectorResourceTest() {
        super(new WebAppDescriptor.Builder("javax.ws.rs.Application",
                CPManWebApplication.class.getCanonicalName())
                .servletClass(ServletContainer.class).build());
    }

    /**
     * Assigns an available port for the test.
     *
     * @param defaultPort If a port cannot be determined, this one is used.
     * @return free port
     */
    @Override
    public int getPort(int defaultPort) {
        try {
            ServerSocket socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException ioe) {
            return defaultPort;
        }
    }

    @Override
    public AppDescriptor configure() {
        return new WebAppDescriptor.Builder("org.onosproject.cpman.rest").build();
    }
}
