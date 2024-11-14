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

package dev.vlxd.datasetservice.service.group;

import dev.vlxd.datasetservice.exception.DataGroupNameIsTakenException;
import dev.vlxd.datasetservice.exception.DataGroupNotFoundException;
import dev.vlxd.datasetservice.exception.PermissionDeniedException;
import dev.vlxd.datasetservice.model.DataGroup;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.dto.DataGroupCreateDto;
import dev.vlxd.datasetservice.repository.DataGroupRepository;
import dev.vlxd.datasetservice.service.dataset.IDatasetService;
import dev.vlxd.datasetservice.util.Permissions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DataGroupService implements IDataGroupService {

    private final DataGroupRepository dataGroupRepository;
    private final IDatasetService datasetService;

    @Autowired
    public DataGroupService(DataGroupRepository dataGroupRepository, IDatasetService datasetService) {
        this.dataGroupRepository = dataGroupRepository;
        this.datasetService = datasetService;
    }

    @Override
    public DataGroup createGroup(long datasetId, DataGroupCreateDto createDto, long userId) {
        Dataset dataset = datasetService.findById(datasetId, userId);

        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.CREATE)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to create data group",
                    userId,
                    Permissions.CREATE
            ));
        }

        if (dataGroupRepository.existsByName(datasetId, createDto.name)) {
            throw new DataGroupNameIsTakenException("Data group name is taken");
        }

        DataGroup group = new DataGroup();

        group.setName(createDto.name);
        group.setDataset(dataset);

        dataGroupRepository.save(group);

        return group;
    }

    @Override
    public DataGroup getGroup(long datasetId, long groupId, long userId) {
        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.READ)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to get data group with id = %d",
                    userId,
                    Permissions.READ,
                    groupId
            ));
        }

        return dataGroupRepository.findById(groupId)
                .orElseThrow(() ->
                                     new DataGroupNotFoundException(String.format(
                                             "Data group with id = %d not found",
                                             groupId
                                     )));
    }

    @Override
    public Page<DataGroup> listGroups(long datasetId, long userId, Pageable pageable) {
        if (!datasetService.checkUserPermissions(datasetId, userId, Permissions.READ)) {
            throw new PermissionDeniedException(String.format(
                    "User with id = %d hasn't got %s permissions to get data groups of dataset with id = %d",
                    userId,
                    Permissions.READ,
                    datasetId
            ));
        }

        return dataGroupRepository.findAllByDatasetId(datasetId, pageable);
    }
}
