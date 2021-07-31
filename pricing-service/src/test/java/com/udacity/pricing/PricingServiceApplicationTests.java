package com.udacity.pricing;

import com.udacity.pricing.api.PricingController;
import com.udacity.pricing.service.PricingService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
//@WebMvcTest(PricingController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class PricingServiceApplicationTests {
	@Autowired
	private MockMvc mvc;

	@MockBean
	PricingService svc;

	@Test
	public void contextLoads() {
	}

	@Test
	public void getPrice() throws Exception{
		String id = "1";
		mvc.perform(get("/services/price")
					.param("vehicleId", id))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

		verify(svc, times(1)).getPrice((Long) id);
	}

}
