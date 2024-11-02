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

package dev.vlxd.datasetservice.service.files;

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.exception.DataFileNotFoundException;
import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.repository.DataFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
public class DataFileService implements IDataFileService {

    private final DataFileRepository dataFileRepository;
    private final String storageServiceUrl;

    @Autowired
    public DataFileService(DataFileRepository dataFileRepository,
                           @Value("${storage.service.url}") String storageServiceUrl) {
        this.dataFileRepository = dataFileRepository;
        this.storageServiceUrl = storageServiceUrl;
    }

    @Override
    public DataFile getDataFile(long datasetId, long groupId, long dataFileId, long userId) {
        return dataFileRepository.findDataFile(datasetId, groupId, dataFileId, userId, PermissionType.READ)
                .orElseThrow(() ->
                        new DataFileNotFoundException(String.format("Data file with id=%d groupId=%d datasetId=%d not found or you don't have READ permission", dataFileId, groupId, datasetId)));
    }

    @Override
    public ResponseEntity<Resource> getResource(long datasetId, long groupId, long dataFileId, long userId) {
        DataFile dataFile = getDataFile(datasetId, groupId, dataFileId, userId);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                UriComponentsBuilder.fromHttpUrl(storageServiceUrl)
                        .pathSegment("resource")
                        .queryParam("fileId", dataFile.getFileId())
                        .toUriString(),
                HttpMethod.GET,
                null,
                Resource.class
        );
    }
}
