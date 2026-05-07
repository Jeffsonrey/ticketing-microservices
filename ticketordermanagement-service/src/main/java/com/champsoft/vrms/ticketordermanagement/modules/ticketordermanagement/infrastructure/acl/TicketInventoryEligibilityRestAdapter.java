package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.acl;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketInventoryEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TicketInventoryEligibilityRestAdapter implements TicketInventoryEligibilityPort {
    private final RestTemplate restTemplate;

    @Value("${services.ticket-inventories.base-url}")
    private String baseUrl;

    public TicketInventoryEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isEligible(String eventId, String ticketType, int quantity) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/ticket-inventories/eligibility")
                .queryParam("eventId", eventId)
                .queryParam("ticketType", ticketType)
                .queryParam("quantity", quantity)
                .toUriString();

        try{
            Boolean eligible = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(eligible);
        }catch (HttpClientErrorException.NotFound ex){
            throw ex;
        }catch (HttpClientErrorException ex){
            throw ex;
        }
        catch (Exception ex){
            throw ex;
        }

    }

}
