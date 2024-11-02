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

package dev.vlxd.datasetservice.service.archive;

import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.DataGroup;
import dev.vlxd.datasetservice.model.Dataset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipArchiveUploaderService implements IArchiveUploaderService {

    private final String storageServiceUrl;

    public ZipArchiveUploaderService(@Value("${storage.service.url}") String storageServiceUrl) {
        this.storageServiceUrl = storageServiceUrl;
    }

    @Override
    public Dataset extractAndUpload(InputStream inputStream, String datasetName, long userId) {
        NonClosableZipInputStream zis = new NonClosableZipInputStream(inputStream);

        Dataset dataset = new Dataset();

        try {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String fileName = entry.getName();
                    String groupName = fileName.replaceAll(".xml|.json|.jpg|.png|.jpeg", "");
                    String storingName = String.join("/", String.valueOf(userId), datasetName, fileName);

                    DataGroup dataGroup = dataset.getDataGroups().get(groupName);

                    if (dataGroup == null) {
                        dataGroup = new DataGroup(groupName, dataset);
                        dataset.getDataGroups().put(groupName, dataGroup);
                    }

                    ResponseEntity<String> response = upload(zis, storingName);

                    DataFile dataFile = new DataFile(
                            response.getBody(),
                            fileName,
                            entry.getLastModifiedTime().toInstant(),
                            entry.getLastModifiedTime().toInstant(),
                            dataGroup);

                    dataGroup.addDataFile(dataFile);
                }
            }

            zis.forceClose();
        } catch (IOException e) {
            throw new RuntimeException("Failed to process ZIP archive", e);
        }

        return dataset;
    }

    @Override
    public InputStream archiveAndDownload(Dataset dataset) {
        return null;
    }

    @Override
    public ResponseEntity<String> upload(InputStream is, String filename) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.set("X-Filename", filename);

        InputStreamResource inputStreamResource = new InputStreamResource(is);
        HttpEntity<InputStreamResource> httpEntity = new HttpEntity<>(inputStreamResource, httpHeaders);

        return restTemplate.postForEntity(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("upload")
                        .toUriString(),
                httpEntity,
                String.class);
    }

    @Override
    public InputStream download() {
        return null;
    }

    protected static class NonClosableZipInputStream extends ZipInputStream {

        public NonClosableZipInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() {
            // close() is disabled due to RestTemplate closes stream after finishes the request
        }

        public void forceClose() throws IOException {
            super.close();
        }
    }
}
