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
import dev.vlxd.datasetservice.model.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

    boolean existsByNameAndOwnerId(String datasetName, long ownerId);

    Optional<Dataset> findDatasetsByIdAndOwnerId(long id, long ownerId);

    @Query("SELECT d " +
            "FROM dataset d " +
            "JOIN permission p ON d.id = p.dataset.id " +
            "WHERE :userId MEMBER OF p.userIds " +
            "AND :id = p.dataset.id " +
            "AND p.type = :permissionType")
    Optional<Dataset> findDatasetByIdAndUserPermission(long id, long userId, PermissionType permissionType);

    @Query("SELECT d " +
            "FROM dataset d " +
            "JOIN permission p ON d.id = p.dataset.id " +
            "WHERE :userId MEMBER OF p.userIds " +
            "AND p.type = :permissionType")
    Page<Dataset> findDatasetsByUserPermission(long userId, PermissionType permissionType, Pageable pageable);
}
