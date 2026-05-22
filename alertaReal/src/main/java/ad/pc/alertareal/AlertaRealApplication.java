package ad.pc.alertareal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertaRealApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertaRealApplication.class, args);
    }
}