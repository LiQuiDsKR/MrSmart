package com.care4u.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.care4u.common.GlobalConstants;

@Service
public class FileDownloadService {

    private final Path fileLocation = Paths.get("C:\\Care4U\\manual");

    public Resource loadFileAsResource(String fileName) throws Exception {
        Path filePath = fileLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return resource;
        } else {
            throw new Exception("File not found " + fileName);
        }
    }
}
