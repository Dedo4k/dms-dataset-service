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

package dev.vlxd.datasetservice.model.dto;

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.model.Permission;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.ArrayList;
import java.util.List;

@Relation(collectionRelation = "permissions")
public class PermissionDto extends RepresentationModel<PermissionDto> {
    public Long id;
    public PermissionType type;
    public List<Long> userIds = new ArrayList<>();

    public PermissionDto(Permission permission) {
        id = permission.getId();
        type = permission.getType();
        userIds.addAll(permission.getUserIds());
    }
}
