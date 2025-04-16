// ICloudinaryService.java
package tn.esprit.spring.formationservice.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
}
