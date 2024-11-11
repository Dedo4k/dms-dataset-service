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

package dev.vlxd.datasetservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "dataset")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "dataset_name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String alias;

    private String description;

    @Column(name = "owner_id", nullable = false)
    private long ownerId;

    @Column(name = "created_at", nullable = false)
    private Instant creationDate;

    @Column(name = "modified_at", nullable = false)
    private Instant modificationDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", referencedColumnName = "id")
    private DatasetConfig config;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataset")
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataset")
    @MapKey(name = "name")
    private Map<String, DataGroup> dataGroups = new HashMap<>();
}
