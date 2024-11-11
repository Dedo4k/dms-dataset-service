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

package dev.vlxd.datasetservice.service.config;

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.exception.DatasetConfigAlreadyExistsException;
import dev.vlxd.datasetservice.exception.DatasetConfigNotFoundException;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.DatasetConfig;
import dev.vlxd.datasetservice.model.dto.DatasetConfigCreateDto;
import dev.vlxd.datasetservice.model.dto.DatasetConfigUpdateDto;
import dev.vlxd.datasetservice.repository.DatasetConfigRepository;
import dev.vlxd.datasetservice.service.dataset.IDatasetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;

@Service
@Transactional
public class DatasetConfigService implements IDatasetConfigService {

    private final DatasetConfigRepository configRepository;
    private final IDatasetService datasetService;

    @Autowired
    public DatasetConfigService(DatasetConfigRepository configRepository,
                                IDatasetService datasetService) {
        this.configRepository = configRepository;
        this.datasetService = datasetService;
    }

    @Override
    public DatasetConfig getConfig(long datasetId, long userId) {
        return configRepository
                .findDatasetConfig(datasetId, userId, PermissionType.READ)
                .orElseThrow(() ->
                        new DatasetConfigNotFoundException(String.format("Dataset with id=%d doesn't have config or or you don't have READ permission", datasetId)));
    }

    @Override
    public DatasetConfig create(long datasetId, DatasetConfigCreateDto createDto, long ownerId) {
        Dataset dataset = datasetService.findByIdAndOwnerId(datasetId, ownerId);

        if (!ObjectUtils.isEmpty(dataset.getConfig())) {
            throw new DatasetConfigAlreadyExistsException(String.format("Dataset with id=%d already has config", datasetId));
        }

        DatasetConfig config = new DatasetConfig();
        config.setDataset(dataset);
        config.setClasses(createDto.classes);
        config.setUseClasses(createDto.useClasses);
        config.setIgnoreClasses(createDto.ignoreClasses);
        config.setUseIgnoreClasses(createDto.useIgnoreClasses);

        dataset.setConfig(config);
        dataset.setModificationDate(Instant.now());

        configRepository.save(config);

        return config;
    }

    @Override
    public DatasetConfig update(long datasetId, DatasetConfigUpdateDto updateDto, long ownerId) {
        DatasetConfig config = configRepository.findDatasetConfigAsOwner(datasetId, ownerId)
                .orElseThrow(() ->
                        new DatasetConfigNotFoundException(String.format("Dataset with id=%d doesn't have config or you aren't an owner of the dataset", datasetId)));

        config.setClasses(updateDto.classes);
        config.setUseClasses(updateDto.useClasses);
        config.setIgnoreClasses(updateDto.ignoreClasses);
        config.setUseIgnoreClasses(updateDto.useIgnoreClasses);

        config.getDataset().setModificationDate(Instant.now());

        configRepository.save(config);

        return config;
    }
}
