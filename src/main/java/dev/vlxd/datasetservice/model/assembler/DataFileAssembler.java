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

import dev.vlxd.datasetservice.controller.DataFileController;
import dev.vlxd.datasetservice.controller.DataGroupController;
import dev.vlxd.datasetservice.controller.DatasetController;
import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.dto.DataFileDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DataFileAssembler implements RepresentationModelAssembler<DataFile, DataFileDto> {

    @Override
    public @NonNull DataFileDto toModel(@NonNull DataFile entity) {
        DataFileDto model = new DataFileDto(entity);

        model.add(
                linkTo(
                        methodOn(DataFileController.class).getDataFile(
                                entity.getDataGroup().getDataset().getId(),
                                entity.getDataGroup().getId(),
                                entity.getId(),
                                -1
                        ))
                        .withRel("self"),
                linkTo(
                        methodOn(DataGroupController.class).getGroup(
                                entity.getDataGroup().getDataset().getId(),
                                entity.getDataGroup().getId(),
                                -1
                        ))
                        .withRel("group"),
                linkTo(
                        methodOn(DatasetController.class).getDataset(
                                entity.getDataGroup().getDataset().getId(),
                                -1
                        ))
                        .withRel("dataset"),
                linkTo(
                        methodOn(DataFileController.class).getResource(
                                entity.getDataGroup().getDataset().getId(),
                                entity.getDataGroup().getId(),
                                entity.getId(),
                                -1
                        ))
                        .withRel("resource")
        );

        return model;
    }

    @Override
    public @NonNull CollectionModel<DataFileDto> toCollectionModel(@NonNull Iterable<? extends DataFile> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
