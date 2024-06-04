// http://localhost:8080/

package com.example.fyp;
import com.example.fyp.DeepPurple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class FypApplication {

	public static void main(String[] args) {
		// SpringApplication.run(FypApplication.class, args);
	}

	@GetMapping("/")
	public String apiRoot() {
		return "hello world...";
	}

}
