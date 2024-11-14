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

import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.dto.DatasetDto;
import dev.vlxd.datasetservice.model.dto.DatasetUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

@Component
public class DatasetAssemblerService {

    private final DatasetAssembler datasetAssembler;
    private final DatasetUploadAssembler uploadAssembler;
    private final PagedResourcesAssembler<Dataset> pagedAssembler;

    @Autowired
    public DatasetAssemblerService(
            DatasetAssembler datasetAssembler,
            DatasetUploadAssembler uploadAssembler
    ) {
        this.datasetAssembler = datasetAssembler;
        this.uploadAssembler = uploadAssembler;
        this.pagedAssembler = new PagedResourcesAssembler<>(null, null);
    }

    public DatasetDto toModal(Dataset dataset) {
        return datasetAssembler.toModel(dataset);
    }

    public PagedModel<DatasetDto> toPagedModel(Page<Dataset> page) {
        return pagedAssembler.toModel(page, datasetAssembler);
    }

    public DatasetUploadDto toUploadModel(Dataset dataset) {
        return uploadAssembler.toModel(dataset);
    }
}
