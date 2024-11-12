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

import dev.vlxd.datasetservice.constant.ArchiveType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.io.OutputStream;

@Service
public class StorageService implements IStorageService {

    private final String storageServiceUrl;

    @Autowired
    public StorageService(@Value("${storage.service.url}") String storageServiceUrl) {
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

    @Override
    public void download(String fileId, ArchiveType archiveType, HttpServletResponse response) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {

            response.setStatus(clientHttpResponse.getStatusCode().value());
            MediaType contentType = clientHttpResponse.getHeaders().getContentType();
            if (contentType != null) {
                response.setContentType(contentType.toString());
            }
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, clientHttpResponse.getHeaders().getContentDisposition().toString());

            try (InputStream inputStream = clientHttpResponse.getBody();
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.flush();

                return null;
            } catch (Exception e) {
                throw new RuntimeException("Error proxying the file stream", e);
            }
        };

        restTemplate.execute(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("archive")
                        .queryParam("fileId", fileId)
                        .queryParam("archiveType", archiveType.name())
                        .toUriString(),
                HttpMethod.GET,
                null,
                responseExtractor
        );
    }
}
