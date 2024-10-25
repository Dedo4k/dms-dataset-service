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

import dev.vlxd.datasetservice.model.Dataset;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/datasets")
public class DatasetController {

    @GetMapping(value = "/list")
    public ResponseEntity<List<Dataset>> listDatasets(@RequestHeader("X-USER-ID") long userId,
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

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadDataset() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<Object> downloadDataset(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }
}
