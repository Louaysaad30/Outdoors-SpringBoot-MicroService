package tn.esprit.spring.userservice.Service.Interface;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
public interface ICloudinaryService {
        String uploadImage(MultipartFile file) throws IOException;
}
