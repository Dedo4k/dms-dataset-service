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

import dev.vlxd.datasetservice.exception.FilenameWithoutExtensionException;

public class FileUtils {

    public static String getFileExtension(String filename) throws FilenameWithoutExtensionException {
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new FilenameWithoutExtensionException("Filename doesn't have an extension");
        }
        return filename.substring(index);
    }

    public static String removeFileExtension(String filename) throws FilenameWithoutExtensionException {
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new FilenameWithoutExtensionException("Filename doesn't have an extension");
        }
        return filename.substring(0, index);
    }
}
