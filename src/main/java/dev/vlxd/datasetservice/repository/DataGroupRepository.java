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

package dev.vlxd.datasetservice.repository;

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.model.DataGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataGroupRepository extends JpaRepository<DataGroup, Long> {

    @Query("SELECT dg " +
            "FROM data_group dg " +
            "JOIN permission p ON dg.dataset.id = p.dataset.id " +
            "WHERE :userId MEMBER OF p.userIds " +
            "AND dg.id = :groupId " +
            "AND dg.dataset.id = :datasetId " +
            "AND p.type = :permissionType")
    Optional<DataGroup> findDataGroup(long datasetId, long groupId, long userId, PermissionType permissionType);

    @Query("SELECT dg " +
            "FROM data_group dg " +
            "JOIN permission p ON dg.dataset.id = p.dataset.id " +
            "WHERE :userId MEMBER OF p.userIds " +
            "AND dg.dataset.id = :datasetId " +
            "AND p.type = :permissionType")
    Page<DataGroup> findDataGroups(long datasetId, long userId, PermissionType permissionType, Pageable pageable);
}
