package CS203G3.tariff_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import CS203G3.tariff_backend.repository.UserRepository;

@SpringBootTest(classes = TariffBackendApplication.class)
@TestPropertySource(properties = {
    "clerk.jwk-set-uri=http://localhost:8080/jwks",
	"FRONTEND_ORIGIN=http://localhost:3000"
})
class TariffBackendApplicationTests {

	@MockitoBean
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}

}
