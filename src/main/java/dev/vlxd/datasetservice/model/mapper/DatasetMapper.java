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

package dev.vlxd.datasetservice.model.mapper;

import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.dto.DatasetDto;
import dev.vlxd.datasetservice.model.dto.DatasetUploadDto;

import java.util.stream.IntStream;

public class DatasetMapper {

    public static DatasetDto toDto(Dataset dataset) {
        return new DatasetDto(dataset);
    }

    public static DatasetUploadDto toUploadDto(Dataset dataset) {
        DatasetUploadDto dto = new DatasetUploadDto();
        dto.id = dataset.getId();
        dto.name = dataset.getName();
        dto.creationDate = dataset.getCreationDate();
        dto.modificationDate = dataset.getModificationDate();
        dto.ownerId = dataset.getOwnerId();
        dto.groups = dataset.getDataGroups().values().size();
        dto.files = dataset.getDataGroups().values().stream()
                .flatMapToInt(dataGroup -> IntStream.of(dataGroup.getFiles().size()))
                .sum();
        return dto;
    }
}
