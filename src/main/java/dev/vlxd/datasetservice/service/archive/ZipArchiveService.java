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

package dev.vlxd.datasetservice.service.archive;

import dev.vlxd.datasetservice.model.DataFile;
import dev.vlxd.datasetservice.model.DataGroup;
import dev.vlxd.datasetservice.model.Dataset;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipArchiveService implements IArchiveService {

    @Override
    public Dataset extractDataset(InputStream inputStream) {
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            Dataset dataset = new Dataset();

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String fileName = entry.getName();
                    String groupName = fileName.replaceAll(".xml|.json|.jpg|.png|.jpeg", "");

                    DataGroup dataGroup = dataset.getDataGroups().get(groupName);

                    if (dataGroup == null) {
                        dataGroup = new DataGroup(groupName, dataset);
                        dataset.getDataGroups().put(groupName, dataGroup);
                    }

                    DataFile dataFile = new DataFile(fileName, dataGroup);
                    dataGroup.addDataFile(dataFile);
                }
            }

            return dataset;
        } catch (IOException e) {
            throw new RuntimeException("Error with processing ZIP archive", e);
        }
    }

    @Override
    public InputStream archiveDataset(Dataset dataset) {
        return null;
    }
}
