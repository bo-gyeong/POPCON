package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.dto.OCR.CheckImageSizeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OcrService {

    public CheckImageSizeDto checkImageSize(MultipartFile file) {

        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            //System.out.println(String.format("width = %d height = %d", width, height));

            CheckImageSizeDto checkImageSizeDto = new CheckImageSizeDto();
            checkImageSizeDto.setWidth(width);
            checkImageSizeDto.setHeight(height);

            return checkImageSizeDto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
