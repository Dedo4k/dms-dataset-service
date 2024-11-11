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

package dev.vlxd.datasetservice.service.file;

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.exception.DataFileNotFoundException;
import dev.vlxd.datasetservice.exception.PermissionDeniedException;
import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.repository.DataFileRepository;
import dev.vlxd.datasetservice.service.storage.IStorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class DataFileService implements IDataFileService {

    private final DataFileRepository dataFileRepository;
    private final IStorageService storageService;

    private final List<PermissionType> createPermissions = List.of(PermissionType.CREATE);
    private final List<PermissionType> readPermissions = List.of(PermissionType.READ);
    private final List<PermissionType> updatePermissions = List.of(PermissionType.READ, PermissionType.UPDATE);
    private final List<PermissionType> deletePermissions = List.of(PermissionType.READ, PermissionType.DELETE);

    @Autowired
    public DataFileService(DataFileRepository dataFileRepository,
                           IStorageService storageService) {
        this.dataFileRepository = dataFileRepository;
        this.storageService = storageService;
    }

    @Override
    public DataFile getDataFile(long datasetId, long groupId, long dataFileId, long userId) {
        return dataFileRepository.findDataFile(
                        datasetId,
                        groupId,
                        dataFileId,
                        userId,
                        readPermissions,
                        readPermissions.size())
                .orElseThrow(() ->
                        new DataFileNotFoundException(String.format("Data file with id = %d groupId = %d datasetId = %d not found or you don't have %s permission", dataFileId, groupId, datasetId, readPermissions)));
    }

    @Override
    public ResponseEntity<Resource> getResource(long datasetId, long groupId, long dataFileId, long userId) {
        DataFile dataFile = getDataFile(datasetId, groupId, dataFileId, userId);

        return storageService.getResource(dataFile.getFileId());
    }

    @Override
    public DataFile updateDataFile(long datasetId, long groupId, long dataFileId, InputStream inputStream, long userId) {
        DataFile dataFile = dataFileRepository.findDataFile(
                        datasetId,
                        groupId,
                        dataFileId,
                        userId,
                        updatePermissions,
                        updatePermissions.size())
                .orElseThrow(() ->
                        new PermissionDeniedException(String.format("User with id = %d hasn't got %s permissions to data file", userId, updatePermissions)));

        ResponseEntity<String> response = storageService.upload(inputStream, dataFile.getFileId());

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            dataFile.setModificationDate(Instant.now());

            dataFileRepository.save(dataFile);

            return dataFile;
        } else {
            throw new RuntimeException(String.format("Failed to update data file. Status code = %s", response.getStatusCode()));
        }
    }
}
