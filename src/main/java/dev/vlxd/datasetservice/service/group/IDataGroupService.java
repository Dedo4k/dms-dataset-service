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

package dev.vlxd.datasetservice.service.group;

import dev.vlxd.datasetservice.model.DataGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDataGroupService {

    DataGroup getGroup(long datasetId, long groupId, long userId);

    Page<DataGroup> listGroups(long datasetId, long userId, Pageable pageable);
}
