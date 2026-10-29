package com.v2.competency.management.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.api.gax.paging.Page;
import com.v2.competency.management.service.PersonaImageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

@Service
@Transactional
public class PersonaImageServiceImpl implements PersonaImageService {

    private static final String BASE_PATH =
            "persona-Images/Nationality/";

    private static final String PUBLIC_BASE_URL =
            "https://storage.googleapis.com/";

    private final Storage storage;

    @Value("${gcp.storage.bucket-name:zimulate}")
    private String bucketName;

    public PersonaImageServiceImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String uploadPersonaImage(
            String ageRange,
            String nationality,
            Boolean male,
            String setting,
            MultipartFile image) throws IOException {

        validateInputs(ageRange, nationality, male, setting);

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(
                    "Image file must not be empty");
        }

        String normalizedNationality = normalizeNationality(nationality);
        String gender = male ? "Male" : "Female";
        String normalizedSetting = normalizeSetting(setting);

        String folderPath = buildFolderPath(
                normalizedNationality,
                gender,
                ageRange.trim(),
                normalizedSetting
        );

        String filePrefix = normalizedNationality + "_"
                + gender + "_"
                + ageRange.trim() + "_"
                + normalizedSetting + "_Option";

        String extension = getFileExtension(image.getOriginalFilename());

        // Find the highest existing option number.
        int nextOption = getNextOptionNumber(folderPath, filePrefix);

        String fileName = filePrefix + nextOption + "." + extension;
        String objectName = folderPath + fileName;

        String contentType = image.getContentType();

        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/octet-stream";
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(
                        BlobId.of(bucketName, objectName))
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, image.getBytes());

        return PUBLIC_BASE_URL + bucketName + "/" + objectName;
    }

    @Override
    public String getRandomPersonaImage(
            String ageRange,
            String nationality,
            Boolean male,
            String setting) {

        validateInputs(ageRange, nationality, male, setting);

        String normalizedNationality = normalizeNationality(nationality);
        String gender = male ? "Male" : "Female";
        String normalizedSetting = normalizeSetting(setting);

        String folderPath = buildFolderPath(
                normalizedNationality,
                gender,
                ageRange.trim(),
                normalizedSetting
        );

        String filePrefix = normalizedNationality + "_"
                + gender + "_"
                + ageRange.trim() + "_"
                + normalizedSetting + "_Option";

        // Match only files following the required OptionN.extension pattern.
        Pattern optionPattern = Pattern.compile(
                "^" + Pattern.quote(folderPath + filePrefix)
                        + "\\d+\\.[a-zA-Z0-9]+$"
        );

        List<String> imageUrls = new ArrayList<>();

        Page<Blob> blobs = storage.list(
                bucketName,
                BlobListOption.prefix(folderPath)
        );

        for (Blob blob : blobs.iterateAll()) {
            String objectName = blob.getName();

            if (optionPattern.matcher(objectName).matches()) {
                imageUrls.add(
                        PUBLIC_BASE_URL + bucketName + "/" + objectName
                );
            }
        }

        if (imageUrls.isEmpty()) {
            throw new IllegalArgumentException(
                    "No persona images found for the supplied attributes");
        }

        int randomIndex = ThreadLocalRandom.current()
                .nextInt(imageUrls.size());

        return imageUrls.get(randomIndex);
    }

    private int getNextOptionNumber(
            String folderPath,
            String filePrefix) {

        Pattern optionPattern = Pattern.compile(
                "^" + Pattern.quote(folderPath + filePrefix)
                        + "(\\d+)\\.[a-zA-Z0-9]+$"
        );

        int maxOption = 0;

        Page<Blob> blobs = storage.list(
                bucketName,
                BlobListOption.prefix(folderPath + filePrefix)
        );

        for (Blob blob : blobs.iterateAll()) {
            Matcher matcher = optionPattern.matcher(blob.getName());

            if (matcher.matches()) {
                int optionNumber = Integer.parseInt(matcher.group(1));
                maxOption = Math.max(maxOption, optionNumber);
            }
        }

        return maxOption + 1;
    }

    private String buildFolderPath(
            String nationality,
            String gender,
            String ageRange,
            String setting) {

        return BASE_PATH
                + nationality + "/"
                + gender + "/"
                + ageRange + "/"
                + setting + "/";
    }

    private String normalizeNationality(String nationality) {
        String value = nationality.trim().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private String normalizeSetting(String setting) {
        String value = setting.trim().toLowerCase(Locale.ROOT);

        if ("home".equals(value)) {
            return "Home";
        }

        if ("corp".equals(value)) {
            return "Corp";
        }

        throw new IllegalArgumentException(
                "Setting must be either home or corp");
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null) {
            throw new IllegalArgumentException(
                    "Original filename is missing");
        }

        int dotIndex = originalFilename.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == originalFilename.length() - 1) {
            throw new IllegalArgumentException(
                    "Image filename must have a valid extension");
        }

        String extension = originalFilename.substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);

        if (!extension.matches("[a-z0-9]+")) {
            throw new IllegalArgumentException(
                    "Invalid image file extension");
        }

        return extension;
    }

    private void validateInputs(
            String ageRange,
            String nationality,
            Boolean male,
            String setting) {

        if (ageRange == null || ageRange.trim().isEmpty()) {
            throw new IllegalArgumentException("Age range is required");
        }

        if (nationality == null || nationality.trim().isEmpty()) {
            throw new IllegalArgumentException("Nationality is required");
        }

        if (male == null) {
            throw new IllegalArgumentException("Gender is required");
        }

        if (setting == null || setting.trim().isEmpty()) {
            throw new IllegalArgumentException("Setting is required");
        }

        // Prevent input from creating unintended object paths.
        if (ageRange.contains("/") || ageRange.contains("\\")
                || nationality.contains("/") || nationality.contains("\\")
                || setting.contains("/") || setting.contains("\\")) {
            throw new IllegalArgumentException(
                    "Attributes must not contain path separators");
        }
    }
}

