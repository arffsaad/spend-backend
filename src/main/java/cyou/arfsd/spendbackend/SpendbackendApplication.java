package cyou.arfsd.spendbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpendbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpendbackendApplication.class, args);
	}

}
