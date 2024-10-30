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

package dev.vlxd.datasetservice.service.archive;

import dev.vlxd.datasetservice.constant.ArchiveType;
import dev.vlxd.datasetservice.model.Dataset;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ArchiveManagerService {

    private final ArchiveServiceFactory archiveServiceFactory;

    public ArchiveManagerService(ArchiveServiceFactory archiveServiceFactory) {
        this.archiveServiceFactory = archiveServiceFactory;
    }

    public Dataset extractAndUpload(ArchiveType archiveType, InputStream inputStream, String datasetName, long userId) {
        IArchiveUploaderService archiveService = archiveServiceFactory.getArchiveService(archiveType);

        if (archiveService == null) {
            throw new UnsupportedOperationException("Archive service not found");
        }

        return archiveService.extractAndUpload(inputStream, datasetName, userId);
    }

    public InputStream archiveDataset(Dataset dataset) {
        return null;
    }
}
