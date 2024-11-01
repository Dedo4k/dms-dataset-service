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

import dev.vlxd.datasetservice.controller.DatasetController;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.dto.DatasetDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DatasetAssembler implements RepresentationModelAssembler<Dataset, DatasetDto> {

    @Override
    public @NonNull DatasetDto toModel(@NonNull Dataset entity) {
        DatasetDto model = new DatasetDto(entity);

        model.add(
                linkTo(
                        methodOn(DatasetController.class).getDataset(entity.getId(), -1))
                        .withRel("self"),
                linkTo(
                        methodOn(DatasetController.class).getConfig(entity.getId(), -1))
                        .withRel("config"),
                linkTo(
                        methodOn(DatasetController.class).downloadDataset(entity.getId()))
                        .withRel("download")
        );

        return model;
    }

    @Override
    public @NonNull CollectionModel<DatasetDto> toCollectionModel(@NonNull Iterable<? extends Dataset> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
