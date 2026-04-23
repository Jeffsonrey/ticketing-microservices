package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.infrastructure.acl;

import com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.application.port.out.CustomerEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerEligibilityRestAdapter implements CustomerEligibilityPort {
    private final RestTemplate restTemplate;

    @Value("${services.customers.base-url}")
    private String baseUrl;

    public CustomerEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isEligible(String customerId) {
        String url = baseUrl + "/customers/" + customerId + "/eligibility";

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
