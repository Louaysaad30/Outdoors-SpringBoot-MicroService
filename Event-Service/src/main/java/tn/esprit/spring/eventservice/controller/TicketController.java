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
import tn.esprit.spring.eventservice.entity.Ticket;
import tn.esprit.spring.eventservice.services.IMPL.TicketServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
@Tag(name = "Ticket Management", description = "Operations for managing event tickets")
public class TicketController {

    private final TicketServiceImpl ticketService;

    // Create
    @Operation(summary = "Create a new ticket", description = "Adds a new ticket type for an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @Parameter(description = "Ticket details", required = true)
            @RequestBody Ticket ticket) {
        return new ResponseEntity<>(ticketService.saveTicket(ticket), HttpStatus.CREATED);
    }

    // Read
    @Operation(summary = "Get all tickets", description = "Retrieves a list of all available tickets")
    @ApiResponse(responseCode = "200", description = "List of tickets retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    @Operation(summary = "Get ticket by ID", description = "Retrieves details of a specific ticket by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket found"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(
            @Parameter(description = "ID of the ticket to retrieve", required = true)
            @PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> new ResponseEntity<>(ticket, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update
    @Operation(summary = "Update ticket", description = "Updates an existing ticket's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket updated successfully"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @Parameter(description = "ID of the ticket to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated ticket details", required = true)
            @RequestBody Ticket ticket) {
        if (!ticketService.getTicketById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ticket.setId(id);
        return new ResponseEntity<>(ticketService.updateTicket(ticket), HttpStatus.OK);
    }

    // Delete
    @Operation(summary = "Delete ticket", description = "Removes a ticket from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ticket deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @Parameter(description = "ID of the ticket to delete", required = true)
            @PathVariable Long id) {
        if (!ticketService.getTicketById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get tickets by event ID
    @Operation(summary = "Get tickets by event ID", description = "Retrieves all tickets for a specific event")
    @ApiResponse(responseCode = "200", description = "List of tickets retrieved successfully")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Ticket>> getTicketsByEventId(
            @Parameter(description = "ID of the event", required = true)
            @PathVariable Long eventId) {
        return new ResponseEntity<>(ticketService.getTicketsByEventId(eventId), HttpStatus.OK);
    }


    @PostMapping("/{id}/discount")
    @Operation(summary = "Apply discount to ticket", description = "Sets a custom discount percentage for a ticket")
    public ResponseEntity<Ticket> applyDiscount(
            @PathVariable Long id,
            @RequestBody Map<String, Object> discountRequest) {

        String code = (String) discountRequest.get("code");
        Double percentage = Double.valueOf(discountRequest.get("percentage").toString());

        return ResponseEntity.ok(ticketService.applyDiscount(id, code, percentage));
    }

/*    @GetMapping("/{id}/price")
    @Operation(summary = "Get effective ticket price", description = "Returns the price after applying any discount")
    public ResponseEntity<Map<String, Object>> getEffectivePrice(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    Double originalPrice = ticket.getPrice();
                    Double discountedPrice = ticketService.calculateDiscountedPrice(ticket);

                    Map<String, Object> response = new HashMap<>();
                    response.put("originalPrice", originalPrice);
                    response.put("discountedPrice", discountedPrice);
                    response.put("discountCode", ticket.getDiscountCode());

                    return ResponseEntity.ok(response);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }*/
}