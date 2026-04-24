package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.acl;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.EventEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class EventEligibilityRestAdapter implements EventEligibilityPort {
    private final RestTemplate restTemplate;

    @Value("${services.events.base-url}")
    private String baseUrl;

    public EventEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public boolean isEligible(String eventId) {
        String url = baseUrl + "/api/events/" + eventId + "/eligibility";

        try{
            EventEligibilityResponse eligible = restTemplate.getForObject(url, EventEligibilityResponse.class);
            return eligible != null && eligible.eligible();
        }catch (HttpClientErrorException.NotFound ex){
            throw ex;
        }catch (HttpClientErrorException ex){
            throw ex;
        }
        catch (Exception ex){
            throw ex;
        }

    }

    private record EventEligibilityResponse(String eventId, boolean eligible) {}
}
