package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.annotation.Timed;
import com.yammer.metrics.core.Timer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;


@Path("/{name}")
public class UnmappedPhotoResource extends PhotoResource {
    private static final Log LOG = Log.forClass(UnmappedPhotoResource.class);
    private static final Timer readTimer = Metrics.newTimer(UnmappedPhotoResource.class, "read", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);


    private final PhotoProvider photoProvider;

    public UnmappedPhotoResource(PhotoProvider photoProvider) {
        this.photoProvider = photoProvider;
    }

    @GET
    @Timed
    @CacheControl(immutable = true)
    public Response getPhoto(@PathParam("name") String name,
                             @MatrixParam("w") Integer width,
                             @MatrixParam("r") RotationParam rotateAngle,
                             @MatrixParam("c") RectangleParam crop) throws Exception {
        return this.getPhoto(this.photoProvider, this.readTimer, name, width, rotateAngle, crop);
    }
}
