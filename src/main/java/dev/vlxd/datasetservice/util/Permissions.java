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

package dev.vlxd.datasetservice.util;

import dev.vlxd.datasetservice.constant.PermissionType;

import java.util.List;

public class Permissions {
    public static final List<PermissionType> CREATE = List.of(
            PermissionType.READ,
            PermissionType.CREATE
    );
    public static final List<PermissionType> READ = List.of(PermissionType.READ);
    public static final List<PermissionType> UPDATE = List.of(
            PermissionType.READ,
            PermissionType.UPDATE
    );
    public static final List<PermissionType> DELETE = List.of(
            PermissionType.READ,
            PermissionType.DELETE
    );
    public static final List<PermissionType> OWNER = List.of(
            PermissionType.CREATE,
            PermissionType.READ,
            PermissionType.UPDATE,
            PermissionType.DELETE
    );
}
