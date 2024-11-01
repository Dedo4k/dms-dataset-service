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

import dev.vlxd.datasetservice.constant.ArchiveType;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.DatasetConfig;
import dev.vlxd.datasetservice.model.assembler.DatasetAssemblerService;
import dev.vlxd.datasetservice.model.assembler.DatasetConfigAssemblerService;
import dev.vlxd.datasetservice.model.dto.*;
import dev.vlxd.datasetservice.service.config.IDatasetConfigService;
import dev.vlxd.datasetservice.service.dataset.IDatasetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/v1/datasets")
public class DatasetController {

    private final IDatasetService datasetService;
    private final IDatasetConfigService configService;
    private final DatasetAssemblerService datasetAssembler;
    private final DatasetConfigAssemblerService configAssembler;

    @Autowired
    public DatasetController(IDatasetService datasetService,
                             IDatasetConfigService configService,
                             DatasetAssemblerService datasetAssembler, DatasetConfigAssemblerService configAssembler) {
        this.datasetService = datasetService;
        this.configService = configService;
        this.datasetAssembler = datasetAssembler;
        this.configAssembler = configAssembler;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<PagedModel<DatasetDto>> listDatasets(@RequestHeader("X-User-Id") long userId,
                                                               @PageableDefault Pageable pageable) {
        Page<Dataset> datasets = datasetService.listDatasets(userId, pageable);

        return ResponseEntity.ok(datasetAssembler.toPagedModel(datasets));
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createDataset() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatasetDto> getDataset(@PathVariable long id,
                                                 @RequestHeader("X-User-Id") long userId) {
        Dataset dataset = datasetService.findById(id, userId);

        return ResponseEntity.ok(datasetAssembler.toModal(dataset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatasetDto> updateDataset(@PathVariable long id,
                                                    @RequestBody DatasetUpdateDto dataset,
                                                    @RequestHeader("X-User-Id") long userId) {
        Dataset updated = datasetService.update(id, dataset, userId);

        return ResponseEntity.ok(datasetAssembler.toModal(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/upload", consumes = {"application/zip"})
    public ResponseEntity<DatasetUploadDto> uploadDataset(HttpServletRequest request,
                                                          @RequestHeader(value = "X-Dataset-Name") String datasetName,
                                                          @RequestHeader("X-User-Id") long userId) {
        try (InputStream inputStream = request.getInputStream()) {
            ArchiveType archiveType = ArchiveType.valueOfType(request.getContentType());
            Dataset dataset = datasetService.uploadDataset(archiveType, inputStream, datasetName, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(datasetAssembler.toUploadModel(dataset));
        } catch (IOException e) {
            throw new RuntimeException("Failed to process request input stream", e);
        }
    }

    @GetMapping("/{id}/config")
    public ResponseEntity<DatasetConfigDto> getConfig(@PathVariable long id, @RequestHeader("X-User-Id") long userId) {
        DatasetConfig config = configService.getConfig(id, userId);

        return ResponseEntity.ok(configAssembler.toModel(config));
    }

    @PostMapping("/{id}/config")
    public ResponseEntity<DatasetConfigDto> createConfig(@PathVariable long id,
                                                         @RequestBody DatasetConfigCreateDto createDto,
                                                         @RequestHeader("X-User-Id") long userId) {
        DatasetConfig config = configService.create(id, createDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(configAssembler.toModel(config));
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<Object> downloadDataset(@PathVariable long id) {
        return ResponseEntity.ok(null);
    }
}
