package com.example.popconback.files.util;


import com.example.popconback.files.dto.FileDto;
import com.example.popconback.files.exception.BadRequestException;
import com.example.popconback.files.exception.FileWriteException;
import com.example.popconback.files.exception.GCPFileUploadException;
import com.example.popconback.files.exception.InvalidFileTypeException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataBucketUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBucketUtil.class);

    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private String gcpBucketId;

    @Value("${gcp.dir.name}")
    private String gcpDirectoryName;


    public FileDto uploadFile(MultipartFile multipartFile, String fileName, String contentType) {

        try{

            LOGGER.debug("Start file uploading process on GCS");
            byte[] fileData = FileUtils.readFileToByteArray(convertFile(multipartFile));

            File forDelete = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            if(forDelete.delete()){
                System.out.println(" ");
            }else{
                System.out.println(" ");
            }

            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();

            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();

            Storage storage = options.getService();
            Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());

            RandomString id = new RandomString(6, ThreadLocalRandom.current());
            Blob blob = bucket.create(gcpDirectoryName + "/" + fileName + "-" + id.nextString() + checkFileExtension(fileName), fileData, contentType);

            String blobUrl = "https://storage.googleapis.com/popcon/"  + blob.getName();

            if(blob != null){
                LOGGER.debug("File successfully uploaded to GCS");
                return new FileDto(null,null, blob.getName(), blobUrl);
            }

        }catch (Exception e){
            LOGGER.error("An error occurred while uploading data. Exception: ", e);
            throw new GCPFileUploadException("An error occurred while storing data to GCS");
        }
        throw new GCPFileUploadException("An error occurred while storing data to GCS");
    }

    private File convertFile(MultipartFile file) {

        try{
            if(file.getOriginalFilename() == null){
                throw new BadRequestException("Original file name is null");
            }
            File convertedFile = new File(file.getOriginalFilename());

            FileOutputStream outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            LOGGER.debug("Converting multipart file : {}", convertedFile);
            return convertedFile;
        }catch (Exception e){
            throw new FileWriteException("An error has occurred while converting the file");
        }
    }

    private String checkFileExtension(String fileName) {
        if(fileName != null && fileName.contains(".")){
            String[] extensionList = {".png", ".jpg", ".pdf", ".doc", ".mp3"};

            for(String extension: extensionList) {
                if (fileName.endsWith(extension)) {
                    LOGGER.debug("Accepted file type : {}", extension);
                    return extension;
                }
            }
        }
        LOGGER.error("Not a permitted file type");
        throw new InvalidFileTypeException("Not a permitted file type");
    }
}