package com.example.reactive.webclientdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@SpringBootApplication
public class WebclientDemoApplication {
	private static ApplicationContext applicationContext;

	@Autowired
	private void setApplicationContext(ApplicationContext ctx) {
		applicationContext = ctx;
	}

	public static void main(String[] args) {
		SpringApplication.run(WebclientDemoApplication.class, args);
		displayAllBeans();
	}

	public static void displayAllBeans() {
		String[] allBeanNames = applicationContext.getBeanNamesForType(DiskSpaceHealthIndicator.class);
		for(String beanName : allBeanNames) {
			System.out.println(beanName);
		}

		allBeanNames = applicationContext.getBeanNamesForType(DriveComponent.class);
		for(String beanName : allBeanNames) {
			System.out.println(beanName);
		}
	}

}



@Slf4j
@Component
@ConfigurationProperties(prefix = "media.health")
@Data
class MediaHealthConfigLoader {
	private List<DriveConfig> drives;

	@PostConstruct
	public void postProcessBeanFactory() {
		log.info("Found {} configured drives", drives.size());
	}
}

@Data
class DriveConfig {
	private String name;
	private String path;
}

@Configuration
@EnableConfigurationProperties
@AllArgsConstructor
class DiskSpaceConfig {
	private final MediaHealthConfigLoader mediaHealthConfigLoader;
	private final ConfigurableListableBeanFactory beanFactory;

	/* dynamically registration of the custom DiskSpaceHealthIndicators */
	@PostConstruct
	public void registerBeans(){
		mediaHealthConfigLoader.getDrives().forEach(drive -> {
			DiskSpaceHealthIndicator newBean = new DiskSpaceHealthIndicator(new File(drive.getPath()), DataSize.ofMegabytes(100));
			beanFactory.initializeBean(newBean, drive.getName());
			beanFactory.registerSingleton(drive.getName(), newBean);
		});
	}
}

@Component
@AllArgsConstructor
class DriveComponent {

	/*
  use the dynamic registered DiskSpaceHealthIndicator driveE.
  note that this does not make much sense since driveE may not be configured in application.yaml
 */
	private final DiskSpaceHealthIndicator driveE;
}
