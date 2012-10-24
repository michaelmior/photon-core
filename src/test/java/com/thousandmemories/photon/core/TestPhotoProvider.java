package com.thousandmemories.photon.core;

import com.thousandmemories.photon.core.PhotoProvider;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;

public class TestPhotoProvider implements PhotoProvider {
    @Override
    public InputStream getPhotoInputStream(String path) throws IOException {
        return Resources.newInputStreamSupplier(Resources.getResource("images/" + path)).getInput();
    }
}

