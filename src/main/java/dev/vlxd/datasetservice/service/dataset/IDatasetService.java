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
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.dto.DatasetCreateDto;
import dev.vlxd.datasetservice.model.dto.DatasetUpdateDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

public interface IDatasetService {

    Page<Dataset> listDatasets(long userId, Pageable pageable);

    Dataset findById(long datasetId, long userId);

    Dataset findByIdAndOwnerId(long datasetId, long ownerId);

    Dataset createDataset(DatasetCreateDto createDto, long userId);

    Dataset update(long datasetId, DatasetUpdateDto dataset, long userId);

    Dataset deleteDataset(long datasetId, long userId);

    Dataset uploadDataset(ArchiveType archiveType, InputStream inputStream, String datasetName, long userId);

    void downloadDataset(long datasetId, ArchiveType archiveType, HttpServletResponse response, long userId);

    boolean checkUserPermissions(long datasetId, long userId, List<PermissionType> permissions);
}
