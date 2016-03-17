package com.ctrip.apollo.client.util;

import com.ctrip.apollo.client.constants.Constants;
import com.ctrip.apollo.client.model.ApolloRegistry;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigUtilTest {
    private ConfigUtil configUtil;
    @Mock
    private ConfigurableApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        configUtil = spy(ConfigUtil.getInstance());

        configUtil.setApplicationContext(applicationContext);
    }

    @Test
    public void testLoadApolloRegistriesSuccessfully() throws Exception {
        Properties someProperties = mock(Properties.class);
        preparePropertiesFromLocalResource(someProperties);

        String someAppId = "someApp";
        String someVersionId = "someVersion";

        when(someProperties.containsKey(Constants.APP_ID)).thenReturn(true);
        when(someProperties.getProperty(Constants.APP_ID)).thenReturn(someAppId);
        when(someProperties.getProperty(eq(Constants.VERSION), anyString())).thenReturn(someVersionId);

        List<ApolloRegistry> apolloRegistries =  configUtil.loadApolloRegistries();

        ApolloRegistry apolloRegistry = apolloRegistries.get(0);
        assertEquals(1, apolloRegistries.size());
        assertEquals(someAppId, apolloRegistry.getAppId());
        assertEquals(someVersionId, apolloRegistry.getVersion());
    }

    @Test
    public void testLoadApolloRegistriesError() throws Exception {
        preparePropertiesFromLocalResource(null);

        List<ApolloRegistry> apolloRegistries =  configUtil.loadApolloRegistries();

        assertTrue(apolloRegistries.isEmpty());
    }

    private void preparePropertiesFromLocalResource(Properties someProperties) throws IOException {
        ClassLoader someClassLoader = mock(ClassLoader.class);
        Thread.currentThread().setContextClassLoader(someClassLoader);
        URL someUrl = new URL("http", "somepath/", "someFile");
        Enumeration<URL> someResourceUrls = Collections.enumeration(Lists.newArrayList(someUrl));

        when(someClassLoader.getResources(anyString())).thenReturn(someResourceUrls);
        doReturn(someProperties).when(configUtil).loadPropertiesFromResourceURL(someUrl);
    }
}
