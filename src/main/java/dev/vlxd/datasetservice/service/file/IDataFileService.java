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

package dev.vlxd.datasetservice.service.file;

import dev.vlxd.datasetservice.model.DataFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

public interface IDataFileService {

    DataFile createDataFile(long datasetId, long groupId, String filename, long userId);

    DataFile createDataFile(long datasetId, long groupId, String filename, InputStream inputStream, long userId);

    DataFile getDataFile(long datasetId, long dataFileId, long userId);

    ResponseEntity<Resource> getResource(long datasetId, long dataFileId, long userId);

    DataFile updateDataFile(long datasetId, long dataFileId, InputStream inputStream, long userId);
}
