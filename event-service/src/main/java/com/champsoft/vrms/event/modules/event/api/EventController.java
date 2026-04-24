package com.champsoft.vrms.event.modules.event.api;


import com.champsoft.vrms.event.modules.event.api.dto.CreateEventRequest;
import com.champsoft.vrms.event.modules.event.api.dto.EventEligibilityResponse;
import com.champsoft.vrms.event.modules.event.api.dto.EventResponse;
import com.champsoft.vrms.event.modules.event.api.dto.UpdateEventRequest;
import com.champsoft.vrms.event.modules.event.api.mapper.EventApiMapper;
import com.champsoft.vrms.event.modules.event.application.service.EventCrudService;
import com.champsoft.vrms.event.modules.event.application.service.EventEligibilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventCrudService crudService;
    private final EventEligibilityService eligibilityService;

    public EventController(
            EventCrudService crudService,
            EventEligibilityService eligibilityService
    ) {
        this.crudService = crudService;
        this.eligibilityService = eligibilityService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@RequestBody CreateEventRequest request) {
        var event = crudService.create(
                request.title(),
                request.description(),
                request.venueName(),
                request.venueAddress(),
                request.venueCity(),
                request.startDateTime(),
                request.endDateTime(),
                request.totalCapacity()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EventApiMapper.toResponse(event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        var event = crudService.getById(id);
        return ResponseEntity.ok(EventApiMapper.toResponse(event));
    }

    @GetMapping
    public ResponseEntity<java.util.List<EventResponse>> getAll() {
        var events = crudService.list().stream()
                .map(EventApiMapper::toResponse)
                .toList();

        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(
            @PathVariable String id,
            @RequestBody UpdateEventRequest request
    ) {
        var event = crudService.update(
                id,
                request.title(),
                request.description(),
                request.venueName(),
                request.venueAddress(),
                request.venueCity(),
                request.startDateTime(),
                request.endDateTime(),
                request.totalCapacity()
        );

        return ResponseEntity.ok(EventApiMapper.toResponse(event));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<EventResponse> activate(@PathVariable String id) {
        var event = crudService.activate(id);
        return ResponseEntity.ok(EventApiMapper.toResponse(event));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancel(@PathVariable String id) {
        var event = crudService.cancel(id);
        return ResponseEntity.ok(EventApiMapper.toResponse(event));
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<EventEligibilityResponse> checkEligibility(@PathVariable String id) {
        boolean eligible = eligibilityService.isEligible(id);
        return ResponseEntity.ok(new EventEligibilityResponse(id, eligible));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        crudService.delete(id);
        return ResponseEntity.noContent().build();
    }

}