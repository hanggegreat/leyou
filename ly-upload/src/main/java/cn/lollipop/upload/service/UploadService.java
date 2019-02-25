package cn.lollipop.upload.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.upload.config.UploadProperties;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    private final UploadProperties uploadProperties;
    private final FastFileStorageClient fastFileStorageClient;

    @Autowired
    public UploadService(UploadProperties uploadProperties, FastFileStorageClient fastFileStorageClient) {
        this.uploadProperties = uploadProperties;
        this.fastFileStorageClient = fastFileStorageClient;
    }

    public String uploadImage(MultipartFile file) {
        try {
            // 校验文件类型
            String contentType = file.getContentType();

            if (!uploadProperties.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionConstant.INVALID_FILE_TYPE);
            }

            // 校验文件内容
            if (ImageIO.read(file.getInputStream()) == null) {
                throw new LyException(ExceptionConstant.INVALID_FILE_TYPE);
            }

            // 上传文件到FastDFS
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            log.error("[文件上传失败]: {}", e);
            throw new LyException(ExceptionConstant.UPLOAD_FILE_ERROR);
        }
    }
}
