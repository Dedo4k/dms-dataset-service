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

import dev.vlxd.datasetservice.model.DataGroup;
import dev.vlxd.datasetservice.model.assembler.DataGroupAssemblerService;
import dev.vlxd.datasetservice.model.dto.DataGroupDto;
import dev.vlxd.datasetservice.service.groups.IDataGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/datasets/{datasetId}/groups")
public class DataGroupController {

    private final IDataGroupService dataGroupService;
    private final DataGroupAssemblerService dataGroupAssembler;

    @Autowired
    public DataGroupController(IDataGroupService dataGroupService,
                               DataGroupAssemblerService dataGroupAssembler) {
        this.dataGroupService = dataGroupService;
        this.dataGroupAssembler = dataGroupAssembler;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<DataGroupDto> getGroup(@PathVariable long datasetId,
                                                 @PathVariable long groupId,
                                                 @RequestHeader("X-User-Id") long userId) {
        DataGroup group = dataGroupService.getGroup(datasetId, groupId, userId);

        return ResponseEntity.ok(dataGroupAssembler.toModel(group));
    }

    @GetMapping
    public ResponseEntity<PagedModel<DataGroupDto>> listGroups(@PathVariable long datasetId,
                                                               @RequestHeader("X-User-Id") long userId,
                                                               @PageableDefault Pageable pageable) {
        Page<DataGroup> groups = dataGroupService.listGroups(datasetId, userId, pageable);

        return ResponseEntity.ok(dataGroupAssembler.toPagedModel(groups));
    }
}
