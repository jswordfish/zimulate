package com.v2.competency.management.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class FileUtils {
	
	// Pattern to match characters that are generally unsafe or problematic in Linux filenames.
    // This includes:
    // - Control characters (\p{Cntrl})
    // - Forward slash (/) - used as path separator
    // - Null character (\0) - invalid in filenames
    // - Various characters with special meaning in shell/regex/filesystem:
    //   \ (backslash), ? (question mark), * (asterisk), " (double quote),
    //   < (less than), > (greater than), | (pipe), : (colon),
    //   ; (semicolon), & (ampersand), $ (dollar sign), ` (backtick),
    //   ' (single quote), space ( ), # (hash/pound)
    // - Characters that might cause issues with certain tools or file systems.
    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[\\p{Cntrl}/\\\\?*\"<>|:;&$`'# ]");

    /**
     * Sanitizes a string to be a valid and safer filename for a Linux file system.
     * Replaces invalid or problematic characters with underscores.
     * Also handles potential issues like starting with a hyphen or being empty after sanitization.
     *
     * @param inputName The original string to sanitize.
     * @return A sanitized string suitable for use as a filename.
     */
    public static String sanitizeFilename(String inputName) {
        if (inputName == null || inputName.trim().isEmpty()) {
            // Return a default name or throw an exception for empty/null input
            return "untitled_file";
        }

        // 1. Normalize characters (e.g., handle accented characters)
        // This helps convert characters like 'é' into 'e' followed by a combining accent,
        // which can then be handled by the replacement step.
        String normalizedName = Normalizer.normalize(inputName, Normalizer.Form.NFD);

        // 2. Remove combining diacritical marks (accents)
        // This removes the combining accent characters left by normalization.
        normalizedName = normalizedName.replaceAll("\\p{M}", "");

        // 3. Replace invalid/problematic characters with underscores
        String sanitizedName = INVALID_CHARS_PATTERN.matcher(normalizedName).replaceAll("_");

        // 4. Trim leading/trailing underscores that might result from replacement
        sanitizedName = sanitizedName.replaceAll("^_|_$", "");

        // 5. Handle cases where the sanitized name might be empty or start with a hyphen
        if (sanitizedName.isEmpty()) {
            throw new RuntimeException("Invalid file name. Reconsider a different name for your roleplay");
        }
        if (sanitizedName.startsWith("-")) {
            sanitizedName = "_" + sanitizedName.substring(1); // Prepend underscore if starts with hyphen
        }

        // Optional: Truncate filename if it's excessively long (Linux limit is typically 255 bytes)
        // This basic truncation is based on character count, not bytes.
        // For byte-accurate truncation, you'd need to consider character encoding.
        // if (sanitizedName.length() > 200) { // Example limit
        //     sanitizedName = sanitizedName.substring(0, 200);
        // }


        return sanitizedName;
    }

}
