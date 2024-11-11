/*
 * Copyright (c) 2024 Uladzislau Lailo.
 *
 * All rights reserved.
 *
 * This source code, and any associated documentation, is the intellectual property of Uladzislau Lailo.
 * Unauthorized copying, modification, distribution, or any form of reuse of this code, in whole or in part,
 * without explicit permission from the copyright holder is strictly prohibited, except where explicitly permitted
 * under applicable open-source licenses (if any).
 *
 * Licensed use:
 * If the code is provided under an open-source license, you must follow the terms of that license, which can be found in the LICENSE file.
 * For any permissions not covered by the license or any inquiries about usage, please contact: [lailo.vlad@gmail.com]
 */

package dev.vlxd.datasetservice.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;

@Service
public class StorageService implements IStorageService {

    private final String storageServiceUrl;

    @Autowired
    public StorageService(@Value("${storage.service.url}")  String storageServiceUrl) {
        this.storageServiceUrl = storageServiceUrl;
    }

    @Override
    public ResponseEntity<String> upload(InputStream inputStream, String filename) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.set("X-Filename", filename);

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpEntity<InputStreamResource> httpEntity = new HttpEntity<>(inputStreamResource, httpHeaders);

        return restTemplate.postForEntity(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("upload")
                        .toUriString(),
                httpEntity,
                String.class);
    }

    @Override
    public ResponseEntity<Resource> getResource(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("resource")
                        .queryParam("fileId", fileId)
                        .toUriString(),
                HttpMethod.GET,
                null,
                Resource.class
        );
    }

    @Override
    public ResponseEntity<Boolean> delete(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("delete")
                        .queryParam("fileId", fileId)
                        .toUriString(),
                HttpMethod.DELETE,
                null,
                Boolean.class
        );
    }
}
