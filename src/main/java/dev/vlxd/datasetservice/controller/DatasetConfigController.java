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

package dev.vlxd.datasetservice.controller;

import dev.vlxd.datasetservice.model.DatasetConfig;
import dev.vlxd.datasetservice.model.assembler.DatasetConfigAssemblerService;
import dev.vlxd.datasetservice.model.dto.DatasetConfigCreateDto;
import dev.vlxd.datasetservice.model.dto.DatasetConfigDto;
import dev.vlxd.datasetservice.model.dto.DatasetConfigUpdateDto;
import dev.vlxd.datasetservice.service.config.IDatasetConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/datasets/{datasetId}/config")
public class DatasetConfigController {

    private final IDatasetConfigService configService;
    private final DatasetConfigAssemblerService configAssembler;

    @Autowired
    public DatasetConfigController(
            IDatasetConfigService configService,
            DatasetConfigAssemblerService configAssembler
    ) {
        this.configService = configService;
        this.configAssembler = configAssembler;
    }

    @PostMapping
    public ResponseEntity<DatasetConfigDto> createConfig(
            @PathVariable long datasetId,
            @RequestBody DatasetConfigCreateDto createDto,
            @RequestHeader("X-User-Id") long userId
    ) {
        DatasetConfig config = configService.create(datasetId, createDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(configAssembler.toModel(config));
    }

    @GetMapping
    public ResponseEntity<DatasetConfigDto> getConfig(
            @PathVariable long datasetId,
            @RequestHeader("X-User-Id") long userId
    ) {
        DatasetConfig config = configService.getConfig(datasetId, userId);

        return ResponseEntity.ok(configAssembler.toModel(config));
    }

    @PutMapping
    public ResponseEntity<DatasetConfigDto> updateConfig(
            @PathVariable long datasetId,
            @RequestBody DatasetConfigUpdateDto updateDto,
            @RequestHeader("X-User-Id") long userId
    ) {
        DatasetConfig config = configService.update(datasetId, updateDto, userId);

        return ResponseEntity.ok(configAssembler.toModel(config));
    }
}
