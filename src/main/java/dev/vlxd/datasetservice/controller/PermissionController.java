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

package dev.vlxd.datasetservice.controller;

import dev.vlxd.datasetservice.model.Permission;
import dev.vlxd.datasetservice.model.assembler.PermissionAssembler;
import dev.vlxd.datasetservice.model.dto.PermissionDto;
import dev.vlxd.datasetservice.model.dto.PermissionUpdateDto;
import dev.vlxd.datasetservice.service.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/datasets/{datasetId}/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionAssembler permissionAssembler;

    @Autowired
    public PermissionController(PermissionService permissionService, PermissionAssembler permissionAssembler) {
        this.permissionService = permissionService;
        this.permissionAssembler = permissionAssembler;
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> getPermission(
            @PathVariable long datasetId,
            @PathVariable long permissionId,
            @RequestHeader("X-User-Id") long userId
    ) {
        Permission permission = permissionService.getPermission(datasetId, permissionId, userId);

        return ResponseEntity.ok(permissionAssembler.toModel(permission));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<PermissionDto>> listPermissions(
            @PathVariable long datasetId,
            @RequestHeader("X-User-Id") long userId
    ) {
        List<Permission> permissions = permissionService.listPermissions(datasetId, userId);

        return ResponseEntity.ok(permissionAssembler.toCollectionModel(permissions));
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> updatePermission(
            @PathVariable long datasetId,
            @PathVariable long permissionId,
            @RequestBody PermissionUpdateDto updateDto,
            @RequestHeader("X-User-Id") long userId
    ) {
        Permission permission = permissionService.updatePermission(datasetId, permissionId, updateDto, userId);

        return ResponseEntity.ok(permissionAssembler.toModel(permission));
    }
}
