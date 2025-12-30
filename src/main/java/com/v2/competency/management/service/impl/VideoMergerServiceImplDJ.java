package com.v2.competency.management.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


import org.springframework.stereotype.Service;

import com.v2.competency.management.service.VideoMergerServiceDJ;

@Service
public class VideoMergerServiceImplDJ implements VideoMergerServiceDJ {
	
	private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";

    private static final String VIDEO_STORAGE_PATH = "/opt/eAssess/apache-tomcat-9.0.93/webapps/ROOT/audio"; 

    @Override
    public String mergeVideos(String videoPath1, String videoPath2) {
        try {
            String mergedFileName = "merged_" + UUID.randomUUID() + ".mp4";
            String outputPath = Paths.get(VIDEO_STORAGE_PATH, mergedFileName).toString();

            String command = String.format("%s -i \"%s\" -i \"%s\" -filter_complex \"[0:v:0][1:v:0]concat=n=2:v=1[outv]\" -map \"[outv]\" -y \"%s\"",
                    FFMPEG_PATH, videoPath1, videoPath2, outputPath);

            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Capture output for debugging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);  // Log output to debug
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }

            // Ensure the file exists after merging
            File mergedFile = new File(outputPath);
            if (!mergedFile.exists() || mergedFile.length() == 0) {
                throw new IOException("Merged video was not created successfully.");
            }

            return outputPath; // Successfully saved video

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
	
    @Override
    public String mergeMultipleVideos(List<String> videoPaths, String outputDir) {
        try {
            if (videoPaths.size() < 2) {
                throw new IllegalArgumentException("At least two videos are required for merging.");
            }

            String mergedFileName = "merged_" + UUID.randomUUID() + ".mp4";
            String outputPath = Paths.get(outputDir, mergedFileName).toString();

            // Build FFmpeg command
            StringBuilder command = new StringBuilder(FFMPEG_PATH);
            for (String videoPath : videoPaths) {
                command.append(" -i \"").append(videoPath).append("\"");
            }
            command.append(" -filter_complex \"");

            for (int i = 0; i < videoPaths.size(); i++) {
                command.append("[").append(i).append(":v:0]");
            }
            command.append("concat=n=").append(videoPaths.size()).append(":v=1[outv]\" -map \"[outv]\" -y \"")
                   .append(outputPath).append("\"");

            // Execute FFmpeg command
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command.toString());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Capture FFmpeg output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }

            // Verify if the merged video is successfully created
            File mergedFile = new File(outputPath);
            if (!mergedFile.exists() || mergedFile.length() == 0) {
                throw new IOException("Merged video was not created successfully.");
            }

            return outputPath;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String mergeVideosFinal(String companyId, String testName, String email, Integer attempt) {
        try {
            // Define video storage path
            String videoDir = Paths.get(VIDEO_STORAGE_PATH, companyId, testName, email, String.valueOf(attempt)).toString();
            File folder = new File(videoDir);

            if (!folder.exists() || !folder.isDirectory()) {
                throw new RuntimeException("No videos found in the directory.");
            }

            // Fetch all .mp4 files
            File[] videoFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
            if (videoFiles == null || videoFiles.length == 0) {
                throw new RuntimeException("No .mp4 videos found for merging.");
            }

            // Sort videos by name (to maintain order)
            Arrays.sort(videoFiles, Comparator.comparing(File::getName));

            // Convert files to list of absolute paths
            List<String> videoPaths = Arrays.stream(videoFiles)
                                            .map(File::getAbsolutePath)
                                            .collect(Collectors.toList());

            // Merge videos using FFmpeg
            String mergedFileName = "merged_" + UUID.randomUUID() + ".mp4";
            String outputPath = Paths.get(videoDir, mergedFileName).toString();

            if (videoPaths.size() < 2) {
                throw new IllegalArgumentException("At least two videos are required for merging.");
            }

            // Build FFmpeg command
            StringBuilder command = new StringBuilder(FFMPEG_PATH);
            for (String videoPath : videoPaths) {
                command.append(" -i \"").append(videoPath).append("\"");
            }
            command.append(" -filter_complex \"");

            for (int i = 0; i < videoPaths.size(); i++) {
                command.append("[").append(i).append(":v:0]"); 
            }
            command.append("concat=n=").append(videoPaths.size()).append(":v=1[outv]\" -map \"[outv]\" -y \"")
                   .append(outputPath).append("\"");

            // Execute FFmpeg command
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command.toString());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Capture FFmpeg output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }

            // Verify if the merged video was created successfully
            File mergedFile = new File(outputPath);
            if (!mergedFile.exists() || mergedFile.length() == 0) {
                throw new IOException("Merged video was not created successfully.");
            }

            return outputPath;

        } catch (Exception e) {
            throw new RuntimeException("Error merging videos: " + e.getMessage(), e);
        }
    }

}
