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

import dev.vlxd.datasetservice.constant.PermissionType;
import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.Dataset;
import dev.vlxd.datasetservice.model.Permission;
import dev.vlxd.datasetservice.model.Record;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/v1/datasets")
public class DatasetController {

    @GetMapping(value = "/list")
    public ResponseEntity<List<Dataset>> listDatasets(@RequestHeader("X-User-Id") long userId,
                                                      Pageable pageable) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createDataset() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dataset> getDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Dataset> updateDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/upload", consumes = {"application/zip"})
    public ResponseEntity<Dataset> uploadDataset(HttpServletRequest request,
                                                @RequestHeader(value = "X-Dataset-Name", required = false) String datasetName,
                                                @RequestHeader("X-User-Id") long userId) {
        try (InputStream is = request.getInputStream(); ZipInputStream zis = new ZipInputStream(is)) {
            Dataset dataset = new Dataset();
            dataset.setOwnerId(userId);
            dataset.setCreationDate(Instant.now());
            dataset.setModificationDate(Instant.now());
            dataset.setPermissions(
                    Arrays.stream(PermissionType.values())
                            .map(type -> new Permission(type, dataset, Collections.singletonList(userId)))
                            .collect(Collectors.toSet()));

            ZipEntry entry = zis.getNextEntry();

            if (!ObjectUtils.isEmpty(datasetName)) {
                dataset.setName(datasetName);
            } else {
                if (entry != null) {
                    dataset.setName(entry.getName());
                } else {
                    throw new RuntimeException("Can not resolve dataset recordName");
                }
            }

            String recordName;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String fileName = entry.getName();
                    recordName = fileName.replaceAll(".xml|.json|.jpg|.png|.jpeg", "");

                    Record record = dataset.getRecords().get(recordName);

                    if (record == null) {
                         record = new Record(recordName, dataset);
                         dataset.getRecords().put(recordName, record);
                    }

                    DataFile dataFile = new DataFile(fileName, record);
                    record.addDataFile(dataFile);
                }
            }

            return ResponseEntity.ok(dataset);
        } catch (IOException ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<Object> downloadDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }
}
