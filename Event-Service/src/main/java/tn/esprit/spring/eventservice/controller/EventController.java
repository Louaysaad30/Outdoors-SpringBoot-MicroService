package tn.esprit.spring.eventservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.eventservice.entity.Event;
import tn.esprit.spring.eventservice.entity.EventArea;
import tn.esprit.spring.eventservice.entity.Status;
import tn.esprit.spring.eventservice.services.IMPL.EventServiceImpl;
import tn.esprit.spring.eventservice.services.interfaces.ICloudinaryService;
import tn.esprit.spring.eventservice.services.interfaces.IEventAreaService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
@Tag(name = "Event Management", description = "Operations for managing events")
public class EventController {

    private final EventServiceImpl eventService;
    private final IEventAreaService eventAreaService;
    private final ICloudinaryService cloudinaryService;
    // Create
    @Operation(summary = "Create a new event", description = "Adds a new event to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> createEvent(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "description", required = true) String description,
            @RequestParam(value = "startDate", required = true) String startDate,
            @RequestParam(value = "endDate", required = true) String endDate,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "eventAreaId", required = true) Long eventAreaId) {

        try {
            // First check if the event area exists
            Optional<EventArea> eventAreaOpt = eventAreaService.getEventAreaById(eventAreaId);
            if (eventAreaOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            EventArea eventArea = eventAreaOpt.get();
            if (!eventArea.isApproved()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // Define a formatter for parsing date strings
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Upload image if provided
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = cloudinaryService.uploadImage(image);
            }

            // Create and save event
            Event event = Event.builder()
                    .title(title)
                    .description(description)
                    .startDate(LocalDateTime.parse(startDate, formatter))
                    .endDate(LocalDateTime.parse(endDate, formatter))
                    .status(Status.valueOf(status))
                    .imageUrl(imageUrl)
                    .eventArea(eventArea)
                    .build();

            Event savedEvent = eventService.saveEvent(event);
            return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);

        } catch (DateTimeParseException e) {
            System.err.println("Date parse error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Read
    @Operation(summary = "Get all events", description = "Retrieves a list of all available events")
    @ApiResponse(responseCode = "200", description = "List of events retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
    }

    @Operation(summary = "Get event by ID", description = "Retrieves details of a specific event by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event found"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID of the event to retrieve", required = true)
            @PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update
  @Operation(summary = "Update event with image", description = "Updates an existing event's details with image upload support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or image upload failed")
    })
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "description", required = true) String description,
            @RequestParam(value = "startDate", required = true) String startDate,
            @RequestParam(value = "endDate", required = true) String endDate,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "eventAreaId", required = true) Long eventAreaId) {

        try {
            // Check if the event exists
            Optional<Event> eventOptional = eventService.getEventById(id);
            if (eventOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Event existingEvent = eventOptional.get();

            // Check if the event area exists
            Optional<EventArea> eventAreaOpt = eventAreaService.getEventAreaById(eventAreaId);
            if (eventAreaOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            EventArea eventArea = eventAreaOpt.get();

            // Parse dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Update image if provided
            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existingEvent.setImageUrl(imageUrl);
            }

            // Update other fields
            existingEvent.setTitle(title);
            existingEvent.setDescription(description);
            existingEvent.setStartDate(LocalDateTime.parse(startDate, formatter));
            existingEvent.setEndDate(LocalDateTime.parse(endDate, formatter));
            existingEvent.setStatus(Status.valueOf(status));
            existingEvent.setEventArea(eventArea);

            Event updatedEvent = eventService.updateEvent(existingEvent);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);

        } catch (DateTimeParseException e) {
            System.err.println("Date parse error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete
    @Operation(summary = "Delete event", description = "Removes an event from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID of the event to delete", required = true)
            @PathVariable Long id) {
        if (!eventService.getEventById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}