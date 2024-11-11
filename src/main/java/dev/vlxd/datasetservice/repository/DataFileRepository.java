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
import dev.vlxd.datasetservice.model.DataFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataFileRepository extends JpaRepository<DataFile, Long> {

    @Query("SELECT df " +
            "FROM data_file df " +
            "JOIN permission p ON df.dataGroup.dataset.id = p.dataset.id " +
            "WHERE :userId MEMBER OF p.userIds " +
            "AND df.id = :dataFileId " +
            "AND df.dataGroup.id = :groupId " +
            "AND df.dataGroup.dataset.id = :datasetId " +
            "AND p.type IN :permissions " +
            "GROUP BY df.id " +
            "HAVING COUNT(p) = :permissionsSize")
    Optional<DataFile> findDataFile(long datasetId, long groupId, long dataFileId, long userId, List<PermissionType> permissions, int permissionsSize);
}
