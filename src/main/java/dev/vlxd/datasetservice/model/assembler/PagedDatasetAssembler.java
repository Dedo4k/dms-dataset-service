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
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;

@Component
public class PagedDatasetAssembler extends PagedResourcesAssembler<Dataset> {

    private final DatasetAssembler assembler;

    public PagedDatasetAssembler(@Nullable HateoasPageableHandlerMethodArgumentResolver resolver,
                                 @Nullable UriComponents baseUri,
                                 DatasetAssembler datasetAssembler) {
        super(resolver, baseUri);
        this.assembler = datasetAssembler;
    }

    public PagedModel<DatasetDto> toPagedModel(Page<Dataset> page) {
        return super.toModel(page, assembler);
    }
}
