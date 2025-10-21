package CS203G3.tariff_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import CS203G3.tariff_backend.repository.UserRepository;

@SpringBootTest(classes = TariffBackendApplication.class)
class TariffBackendApplicationTests {

	@MockitoBean
    private UserRepository userRepository;
	@Test
	void contextLoads() {
	}

}
