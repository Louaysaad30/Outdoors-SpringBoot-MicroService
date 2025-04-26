// CloudinaryServiceImpl.java
package tn.esprit.spring.formationservice.services.IMPL;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.formationservice.services.interfaces.ICloudinaryService;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements ICloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString(); // ou "secure_url" pour HTTPS
    }
}
// Compare this snippet from Formation-Service/src/main/java/tn/esprit/spring/formationservice/services/interfaces/IFormationService.java: