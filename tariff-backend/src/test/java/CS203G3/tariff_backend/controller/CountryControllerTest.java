package CS203G3.tariff_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import CS203G3.tariff_backend.config.TestSecurityConfig;
import CS203G3.tariff_backend.exception.ResourceNotFoundException;
import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.service.CountryService;

@WebMvcTest(value = CountryController.class, 
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class, 
        OAuth2ResourceServerAutoConfiguration.class,
        SecurityAutoConfiguration.class
    })
@Import(TestSecurityConfig.class)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Country country;
    private List<Country> countryList;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setIsoCode("SGP");
        country.setName("Singapore");
        country.setRegion("Asia");

        Country country2 = new Country();
        country2.setIsoCode("USA");
        country2.setName("United States");
        country2.setRegion("North America");

        countryList = Arrays.asList(country, country2);
    }

    @Test
    void getAllCountries_ShouldReturnCountryList() throws Exception {
        when(countryService.getAllCountries()).thenReturn(countryList);

        mockMvc.perform(get("/api/countries"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].isoCode").value("SGP"))
               .andExpect(jsonPath("$[1].isoCode").value("USA"));
    }

    @Test
    void getAllCountries_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(countryService.getAllCountries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/countries"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCountryById_WhenExists_ShouldReturnCountry() throws Exception {
        when(countryService.getCountryById("SGP")).thenReturn(country);

        mockMvc.perform(get("/api/countries/SGP"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isoCode").value("SGP"))
               .andExpect(jsonPath("$.name").value("Singapore"));
    }

    @Test
    void getCountryById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(countryService.getCountryById("XXX"))
            .thenThrow(new ResourceNotFoundException("Country", "XXX"));

        mockMvc.perform(get("/api/countries/XXX"))
               .andExpect(status().isNotFound());
    }

    @Test
    void createCountry_WithValidData_ShouldReturnCreated() throws Exception {
        when(countryService.createCountry(any(Country.class))).thenReturn(country);

        mockMvc.perform(post("/api/countries")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(country)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.isoCode").value("SGP"))
               .andExpect(jsonPath("$.name").value("Singapore"));
    }

    @Test
    void updateCountry_WhenExists_ShouldReturnUpdatedCountry() throws Exception {
        Country updatedCountry = new Country();
        updatedCountry.setIsoCode("SGP");
        updatedCountry.setName("Republic of Singapore");
        updatedCountry.setRegion("Southeast Asia");

        when(countryService.updateCountry(eq("SGP"), any(Country.class))).thenReturn(updatedCountry);

        mockMvc.perform(put("/api/countries/SGP")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedCountry)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isoCode").value("SGP"))
               .andExpect(jsonPath("$.name").value("Republic of Singapore"));
    }

    @Test
    void deleteCountry_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(countryService).deleteCountry("SGP");

        mockMvc.perform(delete("/api/countries/SGP"))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteCountry_WhenNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Country", "XXX"))
            .when(countryService).deleteCountry("XXX");

        mockMvc.perform(delete("/api/countries/XXX"))
               .andExpect(status().isNotFound());
    }
}
