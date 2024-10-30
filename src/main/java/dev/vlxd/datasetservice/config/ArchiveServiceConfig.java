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

package dev.vlxd.datasetservice.config;

import dev.vlxd.datasetservice.constant.ArchiveType;
import dev.vlxd.datasetservice.service.archive.IArchiveUploaderService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ArchiveServiceConfig {

    @Bean
    public Map<ArchiveType, IArchiveUploaderService> archiveServices(@Qualifier("zipArchiveUploaderService") IArchiveUploaderService zipArchiveService) {
        Map<ArchiveType, IArchiveUploaderService> services = new HashMap<>();
        services.put(ArchiveType.ZIP, zipArchiveService);
        return services;
    }
}
