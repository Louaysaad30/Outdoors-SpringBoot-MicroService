package tn.esprit.spring.marketplaceservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.marketplaceservice.services.IMPL.CloudinaryService;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            return "Erreur lors de l'upload : " + e.getMessage();
        }
    }
}