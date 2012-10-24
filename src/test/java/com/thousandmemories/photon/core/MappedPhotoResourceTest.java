package com.thousandmemories.photon.core;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MappedPhotoResourceTest extends ResourceTest {
    @Before
    public void setUp() throws Exception {
        Logger.getLogger("com.sun.jersey").setLevel(Level.WARNING);
    }

    private InputStream getImage(String imageName) throws IOException {
        return Resources.newInputStreamSupplier(Resources.getResource("images/" + imageName)).getInput();
    }

    @Override
    protected void setUpResources() throws Exception {
        addResource(new MappedPhotoResource("com.thousandmemories.photon.core"));
    }

    @Test
    public void testNoModificationsMapped() throws Exception {
        ClientResponse response = client().resource("/test/mf.jpg").get(ClientResponse.class);
        assertThat(response.getEntity(byte[].class), is(ByteStreams.toByteArray(getImage("mf.jpg"))));
        assertThat(response.getType().toString(), is("image/jpeg"));
    }
}
