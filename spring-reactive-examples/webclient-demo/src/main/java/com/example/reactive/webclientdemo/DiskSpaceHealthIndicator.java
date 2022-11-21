package com.example.reactive.webclientdemo;

import org.springframework.util.unit.DataSize;

import java.io.File;

public class DiskSpaceHealthIndicator {
    private File file;
    private DataSize dataSize;

    public DiskSpaceHealthIndicator(File file, DataSize dataSize) {
        this.file = file;
        this.dataSize = dataSize;
    }
}
