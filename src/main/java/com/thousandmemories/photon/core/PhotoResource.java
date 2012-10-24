package com.thousandmemories.photon.core;

import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class PhotoResource {
    public Response getPhoto(PhotoProvider photoProvider,
                             Timer readTimer,
                             String name,
                             Integer width,
                             RotationParam rotateAngle,
                             RectangleParam crop) throws Exception {
        InputStream resultStream;

        InputStream imageStream;
        try {
            imageStream = new BufferedInputStream(photoProvider.getPhotoInputStream(name));
        } catch (FileNotFoundException fnfe) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        String mimeType = URLConnection.guessContentTypeFromStream(imageStream);
        if (mimeType == null) {
            throw new WebApplicationException(501); // Not implemented
        }

        if (width != null || rotateAngle != null || crop != null) {
            BufferedImage image;
            TimerContext readContext = readTimer.time();
            try {
                image = ImageIO.read(imageStream);
            } finally {
                imageStream.close();
                readContext.stop();
            }

            if (crop != null) {
                image = com.thousandmemories.photon.core.Processor.crop(image, crop.get());
            }

            if (rotateAngle != null) {
                image = com.thousandmemories.photon.core.Processor.rotate(image, rotateAngle.get());
            }

            if (width != null) {
                image = com.thousandmemories.photon.core.Processor.fitToWidth(image, width);
            }

            Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(mimeType);
            if (!i.hasNext()) {
                mimeType = "image/jpeg";
                i = ImageIO.getImageWritersByMIMEType(mimeType);
            }

            ImageWriter writer = i.next();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writer.setOutput(new MemoryCacheImageOutputStream(os));
            writer.write(image);
            image.flush();
            image = null;
            resultStream = new ByteArrayInputStream(os.toByteArray());
        } else {
            resultStream = imageStream;
        }

        return Response.
                ok(resultStream, mimeType).
                build();
    }
}
