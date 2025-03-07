package toy01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication
public class Toy01Application{

	public static void main(String[] args) {
		SpringApplication.run(Toy01Application.class, args);
	}

}
