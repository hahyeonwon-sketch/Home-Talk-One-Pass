package com.hometalk.onepass;
<<<<<<< HEAD

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnepassApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnepassApplication.class, args);
	}


}


=======
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OnepassApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnepassApplication.class, args);
    }

}
>>>>>>> e9d88f9f25b225b57753bc38dec6fe1dea0382f0
