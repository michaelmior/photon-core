package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.annotation.Timed;
import com.yammer.metrics.core.Timer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;


@Path("/{path}/{name:.*}")
public class MappedPhotoResource extends PhotoResource {
    private static final Log LOG = Log.forClass(MappedPhotoResource.class);
    private static final Timer readTimer = Metrics.newTimer(MappedPhotoResource.class, "read", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);


    private final String classPath;

    public MappedPhotoResource(String classPath) {
        this.classPath = classPath;
    }

    @GET
    @Timed
    @CacheControl(immutable = true)
    public Response getMappedPhoto(@PathParam("path") String path,
                             @PathParam("name") String name,
                             @MatrixParam("w") Integer width,
                             @MatrixParam("r") RotationParam rotateAngle,
                             @MatrixParam("c") RectangleParam crop) throws Exception {
        String className = classPath + "." + Character.toUpperCase(path.charAt(0)) + path.substring(1) + "PhotoProvider";

        ClassLoader loader = ClassLoader.getSystemClassLoader();

        PhotoProvider provider = null;
        try {
            provider = (PhotoProvider) loader.loadClass(className).newInstance();
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(404);
        }

        return super.getPhoto(provider, this.readTimer, name, width, rotateAngle, crop);
    }
}
