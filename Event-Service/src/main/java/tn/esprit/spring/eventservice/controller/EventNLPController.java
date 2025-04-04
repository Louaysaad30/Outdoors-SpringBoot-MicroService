package tn.esprit.spring.eventservice.controller;

        import io.swagger.v3.oas.annotations.Operation;
        import io.swagger.v3.oas.annotations.Parameter;
        import io.swagger.v3.oas.annotations.responses.ApiResponse;
        import io.swagger.v3.oas.annotations.responses.ApiResponses;
        import io.swagger.v3.oas.annotations.tags.Tag;
        import lombok.AllArgsConstructor;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import tn.esprit.spring.eventservice.entity.Event;
        import tn.esprit.spring.eventservice.services.IMPL.EventServiceImpl;
        import tn.esprit.spring.eventservice.services.interfaces.IHuggingFaceService;

        import java.util.Arrays;
        import java.util.HashSet;
        import java.util.Map;
        import java.util.Optional;

        @CrossOrigin(origins = "*")
        @RestController
        @RequestMapping("/api/events/nlp")
        @AllArgsConstructor
        @Tag(name = "Event NLP Operations", description = "Natural language processing operations for event descriptions")
        public class EventNLPController {

            private static final Logger log = LoggerFactory.getLogger(EventNLPController.class);

            private final EventServiceImpl eventService;
            private final IHuggingFaceService huggingFaceService;

/*
            @PostMapping("/{eventId}/improve-description")
            public ResponseEntity<Event> improveDescription(
                    @Parameter(description = "ID of the event", required = true) @PathVariable Long eventId) {

                Optional<Event> eventOpt = eventService.getEventById(eventId);
                if (eventOpt.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                Event event = eventOpt.get();
                try {
                    String improvedText = huggingFaceService.improveText(event.getDescription());
                    event.setEnhancedDescription(improvedText);
                    Event savedEvent = eventService.saveEvent(event);
                    return ResponseEntity.ok(savedEvent);
                } catch (Exception e) {
                    log.error("Error improving event description: {}", e.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
*/

            @PostMapping("/{eventId}/extract-keywords")
            public ResponseEntity<Event> extractKeywords(
                    @Parameter(description = "ID of the event", required = true) @PathVariable Long eventId) {

                Optional<Event> eventOpt = eventService.getEventById(eventId);
                if (eventOpt.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                Event event = eventOpt.get();
                try {
                    String[] keywords = huggingFaceService.extractKeywords(event.getDescription());
                    event.setKeywords(new HashSet<>(Arrays.asList(keywords)));
                    Event savedEvent = eventService.saveEvent(event);
                    return ResponseEntity.ok(savedEvent);
                } catch (Exception e) {
                    log.error("Error extracting keywords: {}", e.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            @Operation(summary = "Preview text improvements", description = "Shows improved text without saving to database")
            @PostMapping("/preview-improvement")
            public ResponseEntity<Map<String, String>> previewImprovement(
                    @Parameter(description = "Text to improve", required = true) @RequestBody Map<String, String> textRequest) {

                String originalText = textRequest.get("text");
                if (originalText == null || originalText.trim().isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                try {
                    String improved = huggingFaceService.improveText(originalText);
                    return ResponseEntity.ok(Map.of(
                        "originalText", originalText,
                        "improvedText", improved
                    ));
                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }