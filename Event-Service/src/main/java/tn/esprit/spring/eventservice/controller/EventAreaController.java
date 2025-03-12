package tn.esprit.spring.eventservice.controller;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.AllArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import tn.esprit.spring.eventservice.entity.EventArea;
    import tn.esprit.spring.eventservice.services.IMPL.EventAreaServiceImpl;

    import java.util.List;

    @RestController
    @RequestMapping("/api/eventareas")
    @AllArgsConstructor
    @Tag(name = "Event Area Management", description = "Operations for managing event venues and locations")
    public class EventAreaController {

        private final EventAreaServiceImpl eventAreaService;

        // Create
        @Operation(summary = "Create a new event area", description = "Adds a new venue where events can be held")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event area created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PostMapping
        public ResponseEntity<EventArea> createEventArea(
                @Parameter(description = "Event area details", required = true)
                @RequestBody EventArea eventArea) {
            return new ResponseEntity<>(eventAreaService.saveEventArea(eventArea), HttpStatus.CREATED);
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
            @ApiResponse(responseCode = "404", description = "Event area not found")
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
            @ApiResponse(responseCode = "404", description = "Event area not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
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
            @ApiResponse(responseCode = "404", description = "Event area not found")
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