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
import dev.vlxd.datasetservice.controller.PermissionController;
import dev.vlxd.datasetservice.model.Permission;
import dev.vlxd.datasetservice.model.dto.PermissionDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PermissionAssembler implements RepresentationModelAssembler<Permission, PermissionDto> {

    @Override
    public @NonNull PermissionDto toModel(@NonNull Permission entity) {
        PermissionDto model = new PermissionDto(entity);

        model.add(
                linkTo(
                        methodOn(PermissionController.class).getPermission(
                                entity.getDataset().getId(),
                                entity.getId(),
                                -1
                        ))
                        .withRel("self"),
                linkTo(
                        methodOn(DatasetController.class).getDataset(
                                entity.getDataset().getId(),
                                -1
                        ))
                        .withRel("dataset")
        );

        return model;
    }

    @Override
    public @NonNull CollectionModel<PermissionDto> toCollectionModel(@NonNull Iterable<? extends Permission> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
