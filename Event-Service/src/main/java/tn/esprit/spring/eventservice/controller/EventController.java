package tn.esprit.spring.eventservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.eventservice.entity.Event;
import tn.esprit.spring.eventservice.services.IMPL.EventServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
@Tag(name = "Event Management", description = "Operations for managing events")
public class EventController {

    private final EventServiceImpl eventService;

    // Create
    @Operation(summary = "Create a new event", description = "Adds a new event to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Parameter(description = "Event details", required = true)
            @RequestBody Event event) {
        return new ResponseEntity<>(eventService.saveEvent(event), HttpStatus.CREATED);
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
    @Operation(summary = "Update event", description = "Updates an existing event's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID of the event to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated event details", required = true)
            @RequestBody Event event) {
        if (!eventService.getEventById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        event.setId(id);
        return new ResponseEntity<>(eventService.updateEvent(event), HttpStatus.OK);
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