package uit.carbon_shop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uit.carbon_shop.service.MailService;


@SpringBootApplication
@Slf4j
public class CarbonShopApplication implements CommandLineRunner {

    public static void main(final String[] args) {
        SpringApplication.run(CarbonShopApplication.class, args);
    }

    @Autowired
    MailService mailService;

    @Override
    public void run(String... args) throws Exception {
        mailService.sendMail("20520593@gm.uit.edu.vn", "Test mail", "test");
        log.info("Mail sent");
    }
}
