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

package dev.vlxd.datasetservice.service.permission;

import dev.vlxd.datasetservice.exception.PermissionNotFoundException;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.Permission;
import dev.vlxd.datasetservice.model.dto.PermissionUpdateDto;
import dev.vlxd.datasetservice.repository.PermissionRepository;
import dev.vlxd.datasetservice.service.dataset.IDatasetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final IDatasetService datasetService;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, IDatasetService datasetService) {
        this.permissionRepository = permissionRepository;
        this.datasetService = datasetService;
    }

    @Override
    public Permission getPermission(long datasetId, long permissionId, long ownerId) {
        datasetService.findByIdAndOwnerId(datasetId, ownerId);

        return permissionRepository.findById(permissionId)
                .orElseThrow(() ->
                                     new PermissionNotFoundException(String.format(
                                             "Permission with id = %d not found",
                                             permissionId
                                     )));
    }

    @Override
    public List<Permission> listPermissions(long datasetId, long ownerId) {
        Dataset dataset = datasetService.findByIdAndOwnerId(datasetId, ownerId);

        return dataset.getPermissions();
    }

    @Override
    public Permission updatePermission(long datasetId, long permissionId, PermissionUpdateDto updateDto, long ownerId) {
        Permission permission = getPermission(datasetId, permissionId, ownerId);

        if (!updateDto.userIds.contains(ownerId)) {
            updateDto.userIds.add(ownerId);
        }

        List<Long> userIds = permission.getUserIds();

        userIds.removeIf(userId -> !updateDto.userIds.contains(userId) && userId != ownerId);

        updateDto.userIds.forEach(userId -> {
            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
        });

        permissionRepository.save(permission);

        return permission;
    }
}
