package com.phuoclv.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
class AiApplicationTests {

	@Test
	void contextLoads() {
		ApplicationModules.of(AiApplication.class).verify();
	}

}
