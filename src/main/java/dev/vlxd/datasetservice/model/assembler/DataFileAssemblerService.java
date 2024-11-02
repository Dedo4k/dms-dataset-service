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

package dev.vlxd.datasetservice.model.assembler;

import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.dto.DataFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataFileAssemblerService {

    private final DataFileAssembler dataFileAssembler;

    @Autowired
    public DataFileAssemblerService(DataFileAssembler dataFileAssembler) {
        this.dataFileAssembler = dataFileAssembler;
    }

    public DataFileDto toModel(DataFile dataFile) {
        return dataFileAssembler.toModel(dataFile);
    }
}
