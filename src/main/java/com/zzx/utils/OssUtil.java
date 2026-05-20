package com.zzx.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.common.comm.SignVersion;
import com.zzx.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * OSS工具类
 */
@Component
public class OssUtil {

    @Autowired
    private OssConfig ossConfig;

    private OSS ossClient;

    /**
     * 获取OSS客户端
     */
    private OSS getOssClient() {
        if (ossClient == null) {
            DefaultCredentialProvider credentials = new DefaultCredentialProvider(
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
            );

            // 创建配置对象，指定使用V4签名
            ClientBuilderConfiguration clientConfig = new ClientBuilderConfiguration();
            clientConfig.setSignatureVersion(SignVersion.V4);

            // 使用新的构建方式，指定region
            ossClient = OSSClientBuilder.create()
                    .endpoint("https://" + ossConfig.getEndpoint())
                    .credentialsProvider(credentials)
                    .clientConfiguration(clientConfig)
                    .region(ossConfig.getRegion())
                    .build();
        }
        return ossClient;
    }

    /**
     * 上传文件到OSS
     * @param file 上传的文件
     * @param folder 存储文件夹
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = folder + "/" + UUID.randomUUID().toString() + fileExtension;

            // 创建上传请求
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileExtension));
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(),
                fileName,
                new ByteArrayInputStream(file.getBytes())
            );
            putObjectRequest.setMetadata(metadata);

            // 上传文件
            OSS ossClient = getOssClient();
            ossClient.putObject(putObjectRequest);

            // 返回文件访问URL
            return "https://" + ossConfig.getBucketName() + "." +
                   ossConfig.getEndpoint().replace("https://", "") + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 从OSS删除文件
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName != null) {
                OSS ossClient = getOssClient();
                ossClient.deleteObject(ossConfig.getBucketName(), fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 从URL中提取文件名
     * @param fileUrl 文件URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        // 从URL中提取文件名部分
        String domain = "https://" + ossConfig.getBucketName() + "." +
                       ossConfig.getEndpoint().replace("https://", "") + "/";
        if (fileUrl.startsWith(domain)) {
            return fileUrl.substring(domain.length());
        }
        return null;
    }

    /**
     * 根据文件扩展名获取Content-Type
     * @param fileExtension 文件扩展名
     * @return Content-Type
     */
    private String getContentType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 关闭OSS客户端
     */
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}