package org.mule.munit.runner.mule.context;

import java.util.List;
import java.util.Properties;

/**
 * <p>
 * The configuration of the mocking elements for Munit
 * </p>
 *
 * @author Federico, Fernando
 * @since 3.3.2
 */
public class MockingConfiguration
{

    private boolean mockInbounds;
    private List<String> mockingExcludedFlows;
    private boolean mockConnectors;
    private Properties startUpProperties;

    public MockingConfiguration(boolean mockInbounds, List<String> mockingExcludedFlows, boolean mockConnectors, Properties startUpProperties)
    {
        this.mockInbounds = mockInbounds;
        this.mockingExcludedFlows = mockingExcludedFlows;
        this.mockConnectors = mockConnectors;
        this.startUpProperties = startUpProperties;
    }

    public List<String> getMockingExcludedFlows()
    {
        return mockingExcludedFlows;
    }

    public boolean isMockInbounds()
    {
        return mockInbounds;
    }

    public boolean isMockConnectors()
    {
        return mockConnectors;
    }

    public Properties getStartUpProperties()
    {
        return startUpProperties;
    }
}
