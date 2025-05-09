package org.BSB.com.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /** Save the given file and return the generated filename. */
    String store(MultipartFile file);

    /** Load the file as a Spring Resource. */
    Resource loadAsResource(String filename);

    /** Delete the file with the given filename. */
    void delete(String filename);
}
