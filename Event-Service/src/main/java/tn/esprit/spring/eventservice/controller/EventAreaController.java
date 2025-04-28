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
        import tn.esprit.spring.eventservice.dto.EventAreaApprovalDTO;
        import tn.esprit.spring.eventservice.dto.EventAreaStatusChangeDTO;
        import tn.esprit.spring.eventservice.entity.Event;
        import tn.esprit.spring.eventservice.entity.EventArea;
        import tn.esprit.spring.eventservice.entity.EventAreaStatus;
        import tn.esprit.spring.eventservice.services.IMPL.EventAreaServiceImpl;
        import tn.esprit.spring.eventservice.services.interfaces.ICloudinaryService;
        import tn.esprit.spring.eventservice.services.interfaces.IUserServiceClient;

        import java.io.IOException;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        @CrossOrigin(origins = "*")
        @RestController
        @RequestMapping("/api/eventareas")
        @AllArgsConstructor
        @Tag(name = "Event Area Management", description = "Operations for managing event venues and locations")
        public class EventAreaController {

            private final EventAreaServiceImpl eventAreaService;
            private final ICloudinaryService cloudinaryService;
            private final IUserServiceClient userServiceClient;

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


            @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
            @Operation(summary = "Create a new event area with image upload", description = "Creates an event area pending admin approval")
            public ResponseEntity<EventArea> createEventAreaWithImage(
                    @RequestHeader("User-ID") Long userId,
                    @RequestPart(value = "image", required = true) MultipartFile image,
                    @RequestParam(value = "name", required = true) String name,
                    @RequestParam(value = "capacity", required = true) Integer capacity,
                    @RequestParam(value = "latitude", required = true) Double latitude,
                    @RequestParam(value = "longitude", required = true) Double longitude,
                    @RequestParam(value = "description", required = true) String description) {

                if (!userServiceClient.userExists(userId)) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
                    eventArea.setUserId(userId);
                    eventArea.setStatus(EventAreaStatus.PENDING);

                    EventArea savedArea = eventAreaService.saveEventArea(eventArea);
                    return new ResponseEntity<>(savedArea, HttpStatus.CREATED);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            // Add new endpoints for the approval workflow
            @GetMapping("/pending")
            @Operation(summary = "Get pending event areas", description = "Retrieves all event areas pending approval")
            public ResponseEntity<List<EventArea>> getPendingEventAreas() {
                return ResponseEntity.ok(eventAreaService.getPendingEventAreas());
            }

            @PostMapping("/{id}/approve")
            @Operation(summary = "Approve event area", description = "Approves a pending event area")
            public ResponseEntity<EventArea> approveEventArea(
                    @PathVariable Long id,
                    @RequestBody(required = false) EventAreaApprovalDTO approvalDTO) {

                return eventAreaService.getEventAreaById(id)
                        .map(eventArea -> {
                            eventArea.setStatus(EventAreaStatus.APPROVED);
                            // Clear any existing rejection message
                            eventArea.setRejectionMessage(null);
                            return new ResponseEntity<>(eventAreaService.updateEventArea(eventArea), HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }

            @PostMapping("/{id}/reject")
            @Operation(summary = "Reject event area", description = "Rejects a pending event area with feedback")
            public ResponseEntity<EventArea> rejectEventArea(
                    @PathVariable Long id,
                    //@RequestHeader("User-ID") Long adminId,
                    @RequestBody(required = false) EventAreaApprovalDTO approvalDTO) {

                return eventAreaService.getEventAreaById(id)
                        .map(eventArea -> {
                            eventArea.setStatus(EventAreaStatus.REJECTED);

                            // Store rejection message if provided
                            if (approvalDTO != null && approvalDTO.getMessage() != null) {
                                eventArea.setRejectionMessage(approvalDTO.getMessage());
                            }

                            return new ResponseEntity<>(eventAreaService.updateEventArea(eventArea), HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }

            @GetMapping("/rejected")
            @Operation(summary = "Get rejected event areas", description = "Retrieves all rejected event areas with feedback")
            public ResponseEntity<List<EventArea>> getRejectedEventAreas() {
                return ResponseEntity.ok(eventAreaService.getRejectedEventAreas());
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

             @PostMapping("/{id}/change-status")
             @Operation(summary = "Change event area status", description = "Changes the status of an event area to a new status with optional message")
             public ResponseEntity<EventArea> changeEventAreaStatus(
                     @PathVariable Long id,
                     @RequestBody EventAreaStatusChangeDTO statusChangeDTO) {

                 return eventAreaService.getEventAreaById(id)
                         .map(eventArea -> {
                             // Update the status
                             eventArea.setStatus(statusChangeDTO.getNewStatus());

                             if (statusChangeDTO.getNewStatus() == EventAreaStatus.REJECTED &&
                                 statusChangeDTO.getMessage() != null) {
                                 // Store rejection message if changing to REJECTED
                                 eventArea.setRejectionMessage(statusChangeDTO.getMessage());
                             } else if (statusChangeDTO.getNewStatus() == EventAreaStatus.APPROVED) {
                                 // Clear rejection message if changing to APPROVED
                                 eventArea.setRejectionMessage(null);
                             }

                             return new ResponseEntity<>(eventAreaService.updateEventArea(eventArea), HttpStatus.OK);
                         })
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
             }
            // Approved event areas
            @GetMapping("/approved")
            @Operation(summary = "Get approved event areas", description = "Retrieves all event areas that have been approved")
            public ResponseEntity<List<EventArea>> getApprovedEventAreas() {
                return ResponseEntity.ok(eventAreaService.getApprovedEventAreas());
            }

            @GetMapping("/{id}/user")
            @Operation(summary = "Get event area with user details", description = "Retrieves event area details along with the user who created it")
            public ResponseEntity<Map<String, Object>> getEventAreaWithUserDetails(@PathVariable Long id) {
                return eventAreaService.getEventAreaById(id)
                        .map(eventArea -> {
                            Map<String, Object> response = new HashMap<>();
                            response.put("eventArea", eventArea);

                            // Fetch user details if userId exists
                            if (eventArea.getUserId() != null) {
                                try {
                                    ResponseEntity<?> userResponse = userServiceClient.getUserById(eventArea.getUserId());
                                    if (userResponse.getStatusCode().is2xxSuccessful()) {
                                        response.put("user", userResponse.getBody());
                                    } else {
                                        response.put("user", Map.of(
                                            "error", "User not found",
                                            "userId", eventArea.getUserId()
                                        ));
                                    }
                                } catch (Exception e) {
                                    response.put("user", Map.of(
                                        "error", "Failed to retrieve user data",
                                        "userId", eventArea.getUserId(),
                                        "message", e.getMessage()
                                    ));
                                }
                            }

                            return ResponseEntity.ok(response);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }

            // Update
            @Operation(summary = "Update event area with image", description = "Updates an existing event venue's details with image upload support")
            @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Event area updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Event area not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or image upload failed", content = @Content)
            })
            @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
            public ResponseEntity<EventArea> updateEventArea(
                    @Parameter(description = "ID of the event area to update", required = true)
                    @PathVariable Long id,
                    @RequestPart(value = "image", required = false) MultipartFile image,
                    @RequestParam(value = "name", required = true) String name,
                    @RequestParam(value = "capacity", required = true) Integer capacity,
                    @RequestParam(value = "latitude", required = true) Double latitude,
                    @RequestParam(value = "longitude", required = true) Double longitude,
                    @RequestParam(value = "description", required = true) String description) {

                return eventAreaService.getEventAreaById(id)
                        .map(existingArea -> {
                            try {
                                // Update basic information
                                existingArea.setName(name);
                                existingArea.setCapacity(capacity);
                                existingArea.setLatitude(latitude);
                                existingArea.setLongitude(longitude);
                                existingArea.setDescription(description);


                                // Set status back to PENDING for review
                                existingArea.setStatus(EventAreaStatus.PENDING);

                                // Clear any existing rejection message since it's being resubmitted
                                existingArea.setRejectionMessage(null);

                                // Update image only if a new one is provided
                                if (image != null && !image.isEmpty()) {
                                    String imageUrl = cloudinaryService.uploadImage(image);
                                    existingArea.setAreaImg(imageUrl);
                                }

                                // Save the updated event area
                                EventArea updatedArea = eventAreaService.updateEventArea(existingArea);
                                return new ResponseEntity<EventArea>(updatedArea, HttpStatus.OK);
                            } catch (IOException e) {
                                return new ResponseEntity<EventArea>(HttpStatus.BAD_REQUEST);
                            }
                        })
                        .orElse(new ResponseEntity<EventArea>(HttpStatus.NOT_FOUND));
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


            @GetMapping("/{id}/events")
            @Operation(summary = "Get events by event area", description = "Retrieves all events that are hosted at this venue")
            public ResponseEntity<List<Event>> getEventsByEventArea(@PathVariable Long id) {
                return eventAreaService.getEventAreaById(id)
                        .map(eventArea -> {
                            // Since events is marked with @JsonIgnore in EventArea,
                            // we need to manually retrieve them
                            List<Event> events = eventArea.getEvents();
                            return ResponseEntity.ok(events);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }


            @GetMapping("/user/{userId}")
            @Operation(summary = "Get event areas by user ID", description = "Retrieves all event areas created by a specific user")
            public ResponseEntity<List<EventArea>> getEventAreasByUserId(@PathVariable Long userId) {
                return ResponseEntity.ok(eventAreaService.getEventAreasByUserId(userId));
            }



        }