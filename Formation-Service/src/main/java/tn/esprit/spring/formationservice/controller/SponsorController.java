package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.entity.Sponsor;
import tn.esprit.spring.formationservice.services.interfaces.ISponsorService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
@RequiredArgsConstructor
public class SponsorController {

    private final ISponsorService sponsorService;

    @PostMapping("/add")
    public ResponseEntity<?> addSponsor(@RequestPart("sponsor") Sponsor sponsor, @RequestPart("logo") MultipartFile logo) {
        try {
            Sponsor savedSponsor = sponsorService.addSponsor(sponsor, logo);
            return new ResponseEntity<>(savedSponsor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Duplicate name/email
        } catch (IOException e) {
            return new ResponseEntity<>("Logo upload failed", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        return ResponseEntity.ok(sponsorService.getAllSponsors());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        sponsorService.deleteSponsor(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSponsor(@PathVariable Long id,
                                           @RequestPart("sponsor") Sponsor sponsor,
                                           @RequestPart(name = "logo", required = false) MultipartFile logo) {
        try {
            Sponsor updatedSponsor = sponsorService.updateSponsor(id, sponsor, logo);
            return ResponseEntity.ok(updatedSponsor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logo update failed");
        }
    }

}
