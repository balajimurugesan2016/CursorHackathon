package com.hackathon.shipmobility.web;

import com.hackathon.shipmobility.dto.ShipMobilityItem;
import com.hackathon.shipmobility.dto.ShipMobilityResponse;
import com.hackathon.shipmobility.service.ShipMobilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShipMobilityController.class)
class ShipMobilityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ShipMobilityService shipMobilityService;

	@Test
	void getShipMobility_returnsOkWithResponse() throws Exception {
		ShipMobilityResponse response = new ShipMobilityResponse(
				List.of("Genoa", "Lisbon"),
				List.of(
						new ShipMobilityItem("ELBEBORG", "110", List.of("Genoa")),
						new ShipMobilityItem("CGAS JAGUAR", "74", List.of("Lisbon"))
				)
		);
		when(shipMobilityService.getShipMobility()).thenReturn(response);

		mockMvc.perform(get("/api/ship-mobility"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.cities").isArray())
				.andExpect(jsonPath("$.cities.length()").value(2))
				.andExpect(jsonPath("$.cities[0]").value("Genoa"))
				.andExpect(jsonPath("$.cities[1]").value("Lisbon"))
				.andExpect(jsonPath("$.ships").isArray())
				.andExpect(jsonPath("$.ships.length()").value(2))
				.andExpect(jsonPath("$.ships[0].shipName").value("ELBEBORG"))
				.andExpect(jsonPath("$.ships[0].speed").value("110"))
				.andExpect(jsonPath("$.ships[0].cities[0]").value("Genoa"));
	}

	@Test
	void getShipMobility_emptyResult_returnsOk() throws Exception {
		when(shipMobilityService.getShipMobility())
				.thenReturn(new ShipMobilityResponse(List.of(), List.of()));

		mockMvc.perform(get("/api/ship-mobility"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cities").isEmpty())
				.andExpect(jsonPath("$.ships").isEmpty());
	}
}
