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

package dev.vlxd.datasetservice.service.dataset;

import dev.vlxd.datasetservice.constant.ArchiveType;
import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.exception.DatasetNameIsTakenException;
import dev.vlxd.datasetservice.exception.DatasetNotFoundException;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.Permission;
import dev.vlxd.datasetservice.model.dto.DatasetUpdateDto;
import dev.vlxd.datasetservice.repository.DatasetRepository;
import dev.vlxd.datasetservice.service.archive.ArchiveManagerService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@Service
@Transactional
public class DatasetService implements IDatasetService {

    private final ArchiveManagerService archiveService;
    private final DatasetRepository datasetRepository;

    public DatasetService(ArchiveManagerService archiveService,
                          DatasetRepository datasetRepository) {
        this.archiveService = archiveService;
        this.datasetRepository = datasetRepository;
    }

    @Override
    public Page<Dataset> listDatasets(long userId, Pageable pageable) {
        return datasetRepository.findDatasets(userId, PermissionType.READ, pageable);
    }

    @Override
    public Dataset findById(long datasetId, long userId) {
        return datasetRepository.findDataset(datasetId, userId, PermissionType.READ)
                .orElseThrow(() ->
                        new DatasetNotFoundException(String.format("Dataset with id = %d not found or you don't have READ permission", datasetId)));
    }

    @Override
    public Dataset findByIdAndOwnerId(long datasetId, long ownerId) {
        return datasetRepository.findDatasetsByIdAndOwnerId(datasetId, ownerId)
                .orElseThrow(() ->
                        new DatasetNotFoundException(String.format("Dataset with id = %d not found or you aren't an owner of the dataset", datasetId)));
    }

    @Override
    public Dataset update(long datasetId, DatasetUpdateDto dataset, long userId) {
        Dataset datasetToUpdate = datasetRepository
                .findDatasetsByIdAndOwnerId(datasetId, userId)
                .orElseThrow(() ->
                        new DatasetNotFoundException(String.format("Dataset with id = %d not found or you aren't an owner of the dataset", datasetId)));

        datasetToUpdate.setAlias(dataset.name);
        datasetToUpdate.setDescription(dataset.description);
        datasetToUpdate.setModificationDate(Instant.now());

        datasetRepository.save(datasetToUpdate);

        return datasetToUpdate;
    }

    @Override
    public Dataset uploadDataset(ArchiveType archiveType, InputStream inputStream, String datasetName, long userId) {

        if (datasetRepository.existsByNameAndOwnerId(datasetName, userId)) {
            throw new DatasetNameIsTakenException("Dataset name is taken");
        }

        Dataset dataset = archiveService.extractAndUpload(archiveType, inputStream, datasetName, userId);

        dataset.setName(datasetName);
        dataset.setAlias(datasetName);
        dataset.setOwnerId(userId);
        dataset.setCreationDate(Instant.now());
        dataset.setModificationDate(Instant.now());
        dataset.setPermissions(
                Arrays.stream(PermissionType.values())
                        .map(type -> new Permission(type, dataset, Collections.singletonList(userId)))
                        .toList());

        datasetRepository.save(dataset);

        return dataset;
    }
}
