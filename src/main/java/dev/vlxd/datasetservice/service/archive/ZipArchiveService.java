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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipArchiveService implements IArchiveService {

    @Override
    public Dataset extractDataset(InputStream inputStream) {
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            Dataset dataset = new Dataset();

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String fileName = entry.getName();
                    String groupName = fileName.replaceAll(".xml|.json|.jpg|.png|.jpeg", "");

                    DataGroup dataGroup = dataset.getDataGroups().get(groupName);

                    if (dataGroup == null) {
                        dataGroup = new DataGroup(groupName, dataset);
                        dataset.getDataGroups().put(groupName, dataGroup);
                    }

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[32 * 1024];
                    int len;

                    while ((len = zis.read(data)) != -1) {
                        buffer.write(data, 0, len);
                    }

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.toByteArray());

                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    httpHeaders.set("X-Filename", fileName);
                    InputStreamResource inputStreamResource = new InputStreamResource(byteArrayInputStream);
                    HttpEntity<InputStreamResource> httpEntity = new HttpEntity<>(inputStreamResource, httpHeaders);
                    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9002/v1/storage/upload", httpEntity, String.class);

                    DataFile dataFile = new DataFile(
                            response.getBody(),
                            fileName,
                            entry.getLastModifiedTime().toInstant(),
                            entry.getLastModifiedTime().toInstant(),
                            dataGroup);

                    dataGroup.addDataFile(dataFile);
                }
            }

            return dataset;
        } catch (IOException e) {
            throw new RuntimeException("Error with processing ZIP archive", e);
        }
    }

    @Override
    public InputStream archiveDataset(Dataset dataset) {
        return null;
    }
}
