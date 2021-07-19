package space.nov29.cataria.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.nov29.cataria.dto.AssetsUploadResponse;
import space.nov29.cataria.service.AssetsService;

@RestController
@RequestMapping("/admin")
public class AssetsManagementController {

    @Autowired
    private AssetsService assetsService;

    @PostMapping("/assets")
    public ResponseEntity uploadAssets(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = assetsService.put(file);
            AssetsUploadResponse response = new AssetsUploadResponse(filePath);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
