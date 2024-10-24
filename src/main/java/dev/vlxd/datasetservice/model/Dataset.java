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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "dataset")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "owner_id", nullable = false)
    private int ownerId;

    @Column(name = "created_at", nullable = false)
    private Instant creationDate;

    @Column(name = "modified_at", nullable = false)
    private Instant modificationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "config_id", referencedColumnName = "id", nullable = false)
    private DatasetConfig config;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataset")
    private Set<Permission> permissions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataset")
    private Set<Record> records;
}
