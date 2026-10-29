package com.v2.competency.management.webservices;

import com.v2.competency.management.service.PersonaImageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/persona-images")
public class PersonaImageController {

    private final PersonaImageService personaImageService;

    public PersonaImageController(
            PersonaImageService personaImageService) {
        this.personaImageService = personaImageService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadPersonaImage(
            @RequestParam String ageRange,
            @RequestParam String nationality,
            @RequestParam Boolean male,
            @RequestParam String setting,
            @RequestPart("image") MultipartFile image,
            @RequestParam String token) {

        try {
            String imageUrl = personaImageService.uploadPersonaImage(
                    ageRange,
                    nationality,
                    male,
                    setting,
                    image
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("imageUrl", imageUrl);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload persona image");

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/random")
    public ResponseEntity<Map<String, String>> getRandomPersonaImage(
            @RequestParam String ageRange,
            @RequestParam String nationality,
            @RequestParam Boolean male,
            @RequestParam String setting,
            @RequestParam String token) {

        try {
            String imageUrl = personaImageService.getRandomPersonaImage(
                    ageRange,
                    nationality,
                    male,
                    setting
            );

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.NOT_FOUND).body(response);
        }
    }
}

