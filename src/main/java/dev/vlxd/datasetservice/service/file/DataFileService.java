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

import dev.vlxd.datasetservice.exception.DataFileAlreadyExistsException;
import dev.vlxd.datasetservice.exception.DataFileNotFoundException;
import dev.vlxd.datasetservice.exception.FilenameWithoutExtensionException;
import dev.vlxd.datasetservice.exception.PermissionDeniedException;
import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.DataGroup;
import dev.vlxd.datasetservice.repository.DataFileRepository;
import dev.vlxd.datasetservice.service.dataset.IDatasetService;
import dev.vlxd.datasetservice.service.group.IDataGroupService;
import dev.vlxd.datasetservice.service.storage.IStorageService;
import dev.vlxd.datasetservice.util.FileUtils;
import dev.vlxd.datasetservice.util.Permissions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;

@Service
@Transactional
public class DataFileService implements IDataFileService {

    private final DataFileRepository dataFileRepository;
    private final IDatasetService datasetService;
    private final IStorageService storageService;
    private final IDataGroupService dataGroupService;

    @Autowired
    public DataFileService(
            DataFileRepository dataFileRepository,
            IDatasetService datasetService,
            IStorageService storageService,
            IDataGroupService dataGroupService
    ) {
        this.dataFileRepository = dataFileRepository;
        this.datasetService = datasetService;
        this.storageService = storageService;
        this.dataGroupService = dataGroupService;
    }

    @Override
    public DataFile createDataFile(long datasetId, long groupId, String filename, long userId) {
        return createDataFile(datasetId, groupId, filename, InputStream.nullInputStream(), userId);
    }

    @Override
    public DataFile createDataFile(
            long datasetId,
            long groupId,
            String filename,
            InputStream inputStream,
            long userId
    ) {
        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.CREATE)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to create data file in dataset with id = %d",
                    userId,
                    Permissions.CREATE,
                    datasetId
            ));
        }

        DataGroup group = dataGroupService.getGroup(datasetId, groupId, userId);

        try {
            if (!group.getName().equals(FileUtils.removeFileExtension(filename))) {
                throw new IllegalArgumentException("Filename without extension should be equals to data group name");
            }
        } catch (FilenameWithoutExtensionException e) {
            throw new IllegalArgumentException(e);
        }

        String fullName = String.join(
                "/",
                String.valueOf(userId),
                group.getDataset().getName(),
                filename
        );

        if (dataFileRepository.existsByFileNameAndDataGroupId(filename, group.getId())) {
            throw new DataFileAlreadyExistsException(String.format(
                    "Data file with name = %s already exists in dataset with id = %d",
                    filename,
                    datasetId
            ));
        }

        ResponseEntity<String> response = storageService.upload(inputStream, fullName);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format(
                    "Failed to update data file. Status code = %s",
                    response.getStatusCode()
            ));
        }

        DataFile dataFile = new DataFile(response.getBody(), filename, Instant.now(), Instant.now(), group);

        dataFileRepository.save(dataFile);

        return dataFile;
    }

    @Override
    public DataFile getDataFile(long datasetId, long dataFileId, long userId) {
        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.READ)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to get data file with id = %d",
                    userId,
                    Permissions.READ,
                    dataFileId
            ));
        }

        return dataFileRepository.findById(dataFileId)
                .orElseThrow(() ->
                                     new DataFileNotFoundException(String.format(
                                             "Data file with id = %d not found",
                                             dataFileId
                                     )));
    }

    @Override
    public ResponseEntity<Resource> getResource(long datasetId, long dataFileId, long userId) {
        DataFile dataFile = getDataFile(datasetId, dataFileId, userId);

        return storageService.getResource(dataFile.getFileId());
    }

    @Override
    public DataFile updateDataFile(
            long datasetId,
            long dataFileId,
            InputStream inputStream,
            long userId
    ) {
        DataFile dataFile = getDataFile(datasetId, dataFileId, userId);

        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.UPDATE)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to update data file with id = %d",
                    userId,
                    Permissions.UPDATE,
                    dataFileId
            ));
        }

        ResponseEntity<String> response = storageService.upload(inputStream, dataFile.getFileId());

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            dataFile.setModificationDate(Instant.now());

            dataFileRepository.save(dataFile);

            return dataFile;
        } else {
            throw new RuntimeException(String.format(
                    "Failed to update data file. Status code = %s",
                    response.getStatusCode()
            ));
        }
    }
}
