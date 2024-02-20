package mariemoore.PDFGenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class PDFGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PDFGeneratorApplication.class);
		app.run(PDFGeneratorApplication.class, args);
	}
}