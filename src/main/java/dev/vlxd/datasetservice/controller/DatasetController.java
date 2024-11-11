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
import dev.vlxd.datasetservice.model.assembler.DatasetAssemblerService;
import dev.vlxd.datasetservice.model.dto.DatasetDto;
import dev.vlxd.datasetservice.model.dto.DatasetUpdateDto;
import dev.vlxd.datasetservice.model.dto.DatasetUploadDto;
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
    private final DatasetAssemblerService datasetAssembler;

    @Autowired
    public DatasetController(IDatasetService datasetService,
                             DatasetAssemblerService datasetAssembler) {
        this.datasetService = datasetService;
        this.datasetAssembler = datasetAssembler;
    }

    @PostMapping
    public ResponseEntity<Object> createDataset() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<DatasetDto> getDataset(@PathVariable long datasetId,
                                                 @RequestHeader("X-User-Id") long userId) {
        Dataset dataset = datasetService.findById(datasetId, userId);

        return ResponseEntity.ok(datasetAssembler.toModal(dataset));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<PagedModel<DatasetDto>> listDatasets(@RequestHeader("X-User-Id") long userId,
                                                               @PageableDefault Pageable pageable) {
        Page<Dataset> datasets = datasetService.listDatasets(userId, pageable);

        return ResponseEntity.ok(datasetAssembler.toPagedModel(datasets));
    }

    @PutMapping("/{datasetId}")
    public ResponseEntity<DatasetDto> updateDataset(@PathVariable long datasetId,
                                                    @RequestBody DatasetUpdateDto dataset,
                                                    @RequestHeader("X-User-Id") long userId) {
        Dataset updated = datasetService.update(datasetId, dataset, userId);

        return ResponseEntity.ok(datasetAssembler.toModal(updated));
    }

    @DeleteMapping("/{datasetId}")
    public ResponseEntity<DatasetDto> deleteDataset(@PathVariable long datasetId,
                                                    @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(datasetAssembler.toModal(datasetService.deleteDataset(datasetId, userId)));
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

    @PostMapping("/{datasetId}/download")
    public ResponseEntity<Object> downloadDataset(@PathVariable long datasetId) {
        return ResponseEntity.ok(null);
    }
}
