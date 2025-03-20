// File: Event-Service/src/main/java/tn/esprit/spring/eventservice/controller/EventAreaController.java
        package tn.esprit.spring.eventservice.controller;

        import io.swagger.v3.oas.annotations.Operation;
        import io.swagger.v3.oas.annotations.Parameter;
        import io.swagger.v3.oas.annotations.enums.ParameterIn;
        import io.swagger.v3.oas.annotations.media.Content;
        import io.swagger.v3.oas.annotations.media.Schema;
        import io.swagger.v3.oas.annotations.responses.ApiResponse;
        import io.swagger.v3.oas.annotations.responses.ApiResponses;
        import io.swagger.v3.oas.annotations.tags.Tag;
        import lombok.AllArgsConstructor;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;
        import tn.esprit.spring.eventservice.entity.EventArea;
        import tn.esprit.spring.eventservice.services.IMPL.EventAreaServiceImpl;
        import tn.esprit.spring.eventservice.services.interfaces.ICloudinaryService;

        import java.io.IOException;
        import java.util.List;

        @CrossOrigin(origins = "*")
        @RestController
        @RequestMapping("/api/eventareas")
        @AllArgsConstructor
        @Tag(name = "Event Area Management", description = "Operations for managing event venues and locations")
        public class EventAreaController {

            private final EventAreaServiceImpl eventAreaService;
            private final ICloudinaryService cloudinaryService;

            // Create event area without image upload
/*
            @Operation(summary = "Create a new event area", description = "Adds a new venue where events can be held")
            @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Event area created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
            @PostMapping
            public ResponseEntity<EventArea> createEventArea(
                    @Parameter(description = "Event area details", required = true)
                    @RequestBody EventArea eventArea) {
                return new ResponseEntity<>(eventAreaService.saveEventArea(eventArea), HttpStatus.CREATED);
            }
*/

                        // Create event area with image upload (multipart/form-data)
            @Operation(summary = "Create a new event area with image upload", description = "Uploads an image to Cloudinary and creates an event area")
            @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Event area created successfully with image URL", content = @Content),
                @ApiResponse(responseCode = "400", description = "Invalid input data or image upload failed", content = @Content)
            })
            @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
            public ResponseEntity<EventArea> createEventAreaWithImage(
                    @RequestPart(value = "image", required = true) MultipartFile image,
                    @RequestParam(value = "name", required = true) String name,
                    @RequestParam(value = "capacity", required = true) Integer capacity,
                    @RequestParam(value = "latitude", required = true) Double latitude,
                    @RequestParam(value = "longitude", required = true) Double longitude,
                    @RequestParam(value = "description", required = true) String description) {

                if (image == null || image.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                try {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    EventArea eventArea = new EventArea();
                    eventArea.setName(name);
                    eventArea.setCapacity(capacity);
                    eventArea.setLatitude(latitude);
                    eventArea.setLongitude(longitude);
                    eventArea.setDescription(description);
                    eventArea.setAreaImg(imageUrl);
                    EventArea savedArea = eventAreaService.saveEventArea(eventArea);
                    return new ResponseEntity<>(savedArea, HttpStatus.CREATED);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            // Read
            @Operation(summary = "Get all event areas", description = "Retrieves a list of all available event venues")
            @ApiResponse(responseCode = "200", description = "List of event areas retrieved successfully")
            @GetMapping
            public ResponseEntity<List<EventArea>> getAllEventAreas() {
                return new ResponseEntity<>(eventAreaService.getAllEventAreas(), HttpStatus.OK);
            }

            @Operation(summary = "Get event area by ID", description = "Retrieves details of a specific event venue by its ID")
            @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Event area found"),
                @ApiResponse(responseCode = "404", description = "Event area not found", content = @Content)
            })
            @GetMapping("/{id}")
            public ResponseEntity<EventArea> getEventAreaById(
                    @Parameter(description = "ID of the event area to retrieve", required = true)
                    @PathVariable Long id) {
                return eventAreaService.getEventAreaById(id)
                        .map(area -> new ResponseEntity<>(area, HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }

            // Update
            @Operation(summary = "Update event area", description = "Updates an existing event venue's details")
            @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Event area updated successfully"),
                @ApiResponse(responseCode = "404", description = "Event area not found", content = @Content),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
            @PutMapping("/{id}")
            public ResponseEntity<EventArea> updateEventArea(
                    @Parameter(description = "ID of the event area to update", required = true)
                    @PathVariable Long id,
                    @Parameter(description = "Updated event area details", required = true)
                    @RequestBody EventArea eventArea) {
                if (!eventAreaService.getEventAreaById(id).isPresent()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                eventArea.setId(id);
                return new ResponseEntity<>(eventAreaService.updateEventArea(eventArea), HttpStatus.OK);
            }

            // Delete
            @Operation(summary = "Delete event area", description = "Removes an event venue from the system")
            @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Event area deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Event area not found", content = @Content)
            })
            @DeleteMapping("/{id}")
            public ResponseEntity<Void> deleteEventArea(
                    @Parameter(description = "ID of the event area to delete", required = true)
                    @PathVariable Long id) {
                if (!eventAreaService.getEventAreaById(id).isPresent()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                eventAreaService.deleteEventArea(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }