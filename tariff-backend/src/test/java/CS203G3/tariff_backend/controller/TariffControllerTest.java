package CS203G3.tariff_backend.controller;

import static org.mockito.Mockito.doNothing;
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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import CS203G3.tariff_backend.config.TestSecurityConfig;
import CS203G3.tariff_backend.dto.TariffDto;
import CS203G3.tariff_backend.service.TariffService;

@WebMvcTest(value = TariffController.class, 
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class, 
        OAuth2ResourceServerAutoConfiguration.class,
        SecurityAutoConfiguration.class
    })
@Import(TestSecurityConfig.class)
public class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffService tariffService;

    @Autowired
    private ObjectMapper objectMapper;

    private TariffDto tariffDto;
    private List<TariffDto> tariffList;

    @BeforeEach
    void setUp() {
        tariffDto = new TariffDto();
        tariffDto.setTariffID(1L);
        tariffDto.setExporterCode("SGP");
        tariffDto.setImporterCode("USA");
        tariffDto.setHSCode("0101");

        TariffDto tariffDto2 = new TariffDto();
        tariffDto2.setTariffID(2L);
        tariffDto2.setExporterCode("USA");
        tariffDto2.setImporterCode("SGP");
        tariffDto2.setHSCode("0102");

        tariffList = Arrays.asList(tariffDto, tariffDto2);
    }

    @Test
    void getAllTariffRates_ShouldReturnTariffList() throws Exception {
        when(tariffService.getAllTariffRates()).thenReturn(tariffList);

        mockMvc.perform(get("/api/tariffs"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].tariffID").value(1))
               .andExpect(jsonPath("$[1].tariffID").value(2));
    }

    @Test
    void getAllTariffRates_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(tariffService.getAllTariffRates()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tariffs"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTariffById_WhenExists_ShouldReturnTariff() throws Exception {
        when(tariffService.getTariffById(1L)).thenReturn(Arrays.asList(tariffDto));

        mockMvc.perform(get("/api/tariffs/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[0].tariffID").value(1))
               .andExpect(jsonPath("$[0].exporterCode").value("SGP"));
    }

    @Test
    void getTariffsByPage_ShouldReturnPagedResults() throws Exception {
        when(tariffService.getTariffsByPage(1, 10)).thenReturn(tariffList);

        mockMvc.perform(get("/api/tariffs/batch?page=1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteTariff_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(tariffService).deleteTariff(1L);

        mockMvc.perform(delete("/api/tariffs/1"))
               .andExpect(status().isNoContent());
    }
}
