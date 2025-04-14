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
    public ResponseEntity<Sponsor> addSponsor(@RequestPart("sponsor") Sponsor sponsor, @RequestPart("logo") MultipartFile logo) {
        try {
            Sponsor savedSponsor = sponsorService.addSponsor(sponsor, logo);
            return new ResponseEntity<>(savedSponsor, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        List<Sponsor> sponsors = sponsorService.getAllSponsors();
        return new ResponseEntity<>(sponsors, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        sponsorService.deleteSponsor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Sponsor> updateSponsor(@PathVariable Long id, @RequestPart("sponsor") Sponsor sponsor, @RequestPart("logo") MultipartFile logo) {
        try {
            Sponsor updatedSponsor = sponsorService.updateSponsor(id, sponsor, logo);
            return new ResponseEntity<>(updatedSponsor, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}