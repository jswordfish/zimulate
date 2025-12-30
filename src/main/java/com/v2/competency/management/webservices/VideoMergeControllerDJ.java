package com.v2.competency.management.webservices;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.v2.competency.management.service.VideoMergerServiceDJ;

@RestController
@RequestMapping("/api/video")
public class VideoMergeControllerDJ {

    @Autowired
    VideoMergerServiceDJ videoMergeService;

    private static final String VIDEO_STORAGE_PATH = "/opt/eAssess/apache-tomcat-9.0.93/webapps/ROOT/audio";

    @PostMapping("/merge")
    public ResponseEntity<String> mergeVideos(@RequestParam("video1") String videoFileName1,
                                              @RequestParam("video2") String videoFileName2,
                                              String token) {
        try {
            // Construct full paths for input videos
            String videoPath1 = Paths.get(VIDEO_STORAGE_PATH, videoFileName1).toString();
            String videoPath2 = Paths.get(VIDEO_STORAGE_PATH, videoFileName2).toString();

            // Merge videos
            String mergedVideoPath = videoMergeService.mergeVideos(videoPath1, videoPath2);

            return ResponseEntity.ok("Merged video saved at: " + mergedVideoPath);
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error merging videos: " + e.getMessage());
        }
    }
    
    @PostMapping("/mergeMultipleVideos")
    public ResponseEntity<String> mergeVideos(@RequestParam("companyId") String companyId,
                                              @RequestParam("testName") String testName,
                                              @RequestParam("email") String email,
                                              @RequestParam("attempt") Integer attempt,
                                              String token) {
        try {
            
            String videoDir = Paths.get(VIDEO_STORAGE_PATH, companyId, testName, email, String.valueOf(attempt)).toString();
            File folder = new File(videoDir);
            
            if (!folder.exists() || !folder.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No videos found in the directory.");
            }

            
            File[] videoFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
            if (videoFiles == null || videoFiles.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No .mp4 videos found for merging.");
            }

            Arrays.sort(videoFiles, Comparator.comparing(File::getName)); 

            
            List<String> videoPaths = Arrays.stream(videoFiles)
                                            .map(File::getAbsolutePath)
                                            .collect(Collectors.toList());

            
            String mergedVideoPath = videoMergeService.mergeMultipleVideos(videoPaths, videoDir);

            return ResponseEntity.ok("Merged video saved at: " + mergedVideoPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error merging videos: " + e.getMessage());
        }
    }
}