package TVS_SFL.BearingHousingBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BearingHousingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BearingHousingBackendApplication.class, args);
	}

}
