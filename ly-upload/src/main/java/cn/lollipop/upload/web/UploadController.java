package cn.lollipop.upload.web;

import cn.lollipop.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(MultipartFile file) {
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
}
