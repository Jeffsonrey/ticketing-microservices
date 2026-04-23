package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.acl;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.CustomerEligibilityPort;
import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.TicketInventoryEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class TicketInventoryEligibilityRestAdapter implements TicketInventoryEligibilityPort {
    private final RestTemplate restTemplate;

    @Value("${services.ticket-inventories.base-url}")
    private String baseUrl;

    public TicketInventoryEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isEligible(String customerId) {
        String url = baseUrl + "/ticket-inventories/" + customerId + "/eligibility";

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