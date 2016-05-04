package org.commonjava.indy.ftest.core.content;

import org.commonjava.indy.model.core.RemoteRepository;
import org.commonjava.indy.test.fixture.core.CoreServerFixture;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MetadataPassthroughTimeoutWorkingTest
        extends AbstractMetadataTimeoutWorkingTest
{
    private static final int CACHE_TIMEOUT_SECONDS = 2;

    private static final int CACHE_TIMEOUT_WAITING_MILLISECONDS = 3000;

    private static final int METADATA_TIMEOUT_SECONDS = 7;

    private static final int METADATA_TIMEOUT_WAITING_MILLISECONDS = 5000;

    private static final int PASSTHROUGH_TIMEOUT_SECONDS = 9;

    private static final int PASSTHROUGH_TIMEOUT_WAITING_MILLISECONDS = 2000;

    @Override
    protected void initTestConfig( CoreServerFixture fixture )
            throws IOException
    {
        writeConfigFile( "main.conf", "passthrough.timeout=" + PASSTHROUGH_TIMEOUT_SECONDS + "\n" + readTestResource(
                "default-test-main.conf" ) );
    }

    @Test
    public void timeout()
            throws Exception
    {
        Thread.sleep( CACHE_TIMEOUT_WAITING_MILLISECONDS );
        logger.debug( "Verifying content timeouts at: {} (timeout: {}s)", new Date(), CACHE_TIMEOUT_SECONDS );
        assertThat( "artifact should not be removed because of passthrough", pomFile.exists(), equalTo( true ) );
        assertThat( "metadata should not be removed because of passthrough", metadataFile.exists(), equalTo( true ) );
        assertThat( "archetype should not be removed because of passthrough", archetypeFile.exists(), equalTo( true ) );

        Thread.sleep( METADATA_TIMEOUT_WAITING_MILLISECONDS );
        logger.debug( "Verifying metadata timeouts at: {} (timeout: {}s)", new Date(), METADATA_TIMEOUT_SECONDS );
        assertThat( "artifact should not be removed because of passthrough", pomFile.exists(), equalTo( true ) );
        assertThat( "metadata should not be removed because of passthrough", metadataFile.exists(), equalTo( true ) );
        assertThat( "archetype should not be removed because of passthrough", archetypeFile.exists(), equalTo( true ) );

        Thread.sleep( PASSTHROUGH_TIMEOUT_WAITING_MILLISECONDS );
        logger.debug( "Verifying passthrough timeouts at: {} (timeout: {}s)", new Date(), PASSTHROUGH_TIMEOUT_SECONDS );
        assertThat( "artifact should be removed because of passthrough timeout", pomFile.exists(), equalTo( false ) );
        assertThat( "metadata should not be removed because of passthrough timeout", metadataFile.exists(),
                    equalTo( false ) );
        assertThat( "archetype should not be removed because of passthrough timeout", archetypeFile.exists(),
                    equalTo( false ) );
    }

    @Override
    protected RemoteRepository createRemoteRepository()
    {
        final RemoteRepository repository = new RemoteRepository( repoId, server.formatUrl( repoId ) );
        repository.setPassthrough( true );
        repository.setCacheTimeoutSeconds( CACHE_TIMEOUT_SECONDS );
        repository.setMetadataTimeoutSeconds( METADATA_TIMEOUT_SECONDS );
        return repository;
    }
}
