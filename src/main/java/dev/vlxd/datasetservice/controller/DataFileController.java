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

import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.assembler.DataFileAssemblerService;
import dev.vlxd.datasetservice.model.dto.DataFileDto;
import dev.vlxd.datasetservice.service.file.IDataFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/v1/datasets/{datasetId}/groups/{groupId}/files")
public class DataFileController {

    private final IDataFileService dataFileService;
    private final DataFileAssemblerService dataFileAssembler;

    @Autowired
    public DataFileController(
            IDataFileService dataFileService,
            DataFileAssemblerService dataFileAssembler
    ) {
        this.dataFileService = dataFileService;
        this.dataFileAssembler = dataFileAssembler;
    }

    @PostMapping
    public ResponseEntity<DataFileDto> createDataFile(
            @PathVariable long datasetId,
            @PathVariable long groupId,
            @RequestHeader("X-User-Id") long userId,
            @RequestPart String filename,
            @RequestPart(required = false) MultipartFile file
    ) {
        if (file == null) {
            DataFile dataFile = dataFileService.createDataFile(datasetId, groupId, filename, userId);

            return ResponseEntity.ok(dataFileAssembler.toModel(dataFile));
        } else {
            try (InputStream inputStream = file.getInputStream()) {
                DataFile dataFile = dataFileService.createDataFile(datasetId, groupId, filename, inputStream, userId);

                return ResponseEntity.ok(dataFileAssembler.toModel(dataFile));
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file input stream", e);
            }
        }
    }

    @GetMapping("/{dataFileId}")
    public ResponseEntity<DataFileDto> getDataFile(
            @PathVariable long datasetId,
            @PathVariable long groupId,
            @PathVariable long dataFileId,
            @RequestHeader("X-User-Id") long userId
    ) {
        DataFile dataFile = dataFileService.getDataFile(datasetId, dataFileId, userId);

        return ResponseEntity.ok(dataFileAssembler.toModel(dataFile));
    }

    @GetMapping("/{dataFileId}/resource")
    public ResponseEntity<Resource> getResource(
            @PathVariable long datasetId,
            @PathVariable long groupId,
            @PathVariable long dataFileId,
            @RequestHeader("X-User-Id") long userId
    ) {
        return dataFileService.getResource(datasetId, dataFileId, userId);
    }

    @PutMapping("/{dataFileId}")
    public ResponseEntity<DataFileDto> updateDataFileSource(
            @PathVariable long datasetId,
            @PathVariable long groupId,
            @PathVariable long dataFileId,
            @RequestHeader("X-User-Id") long userId,
            @RequestBody MultipartFile file
    ) {
        try (InputStream inputStream = file.getInputStream()) {
            DataFile dataFile = dataFileService.updateDataFile(datasetId, dataFileId, inputStream, userId);

            return ResponseEntity.ok(dataFileAssembler.toModel(dataFile));
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file input stream", e);
        }
    }

    @DeleteMapping("/{dataFileId}")
    public ResponseEntity<DataFileDto> deleteDataFile(
            @PathVariable long datasetId,
            @PathVariable long groupId,
            @PathVariable long dataFileId,
            @RequestHeader("X-User-Id") long userId
    ) {
        DataFile dataFile = dataFileService.deleteDataFile(datasetId, dataFileId, userId);

        return ResponseEntity.ok(dataFileAssembler.toModel(dataFile));
    }
}
