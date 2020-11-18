package org.diplom.blog.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.SneakyThrows;
import org.diplom.blog.api.response.CaptchaResponse;
import org.diplom.blog.model.CaptchaCode;
import org.diplom.blog.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Andrey.Kazakov
 * @date 18.10.2020
 */
@Service
public class CaptchaService {

    public static final String TEMPLATE_HEADER_FOR_CAPTCHA = "data:image/png;base64, %s";
    public static final Integer SIZE_CAPTCHA_CODE = 4;
    public static final Integer HOURS_FOR_EXPIRE = 1;

    private final CaptchaRepository captchaRepository;

    @Autowired
    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<CaptchaResponse> getCaptcha()  {
        CompletableFuture.runAsync(this::clearOldCaptcha);

        String secretCode = UUID.randomUUID().toString();
        String code = generateCaptchaCode();

        CaptchaCode captchaCode = CaptchaCode.builder()
                .code(code)
                .secretCode(secretCode)
                .build();

        captchaRepository.save(captchaCode);

        String captchaBase64 = captchaGenerate(code);
        CaptchaResponse response = new CaptchaResponse(secretCode,
                String.format(TEMPLATE_HEADER_FOR_CAPTCHA, captchaBase64));

        return ResponseEntity.ok(response);
    }

    public boolean checkCaptchaCode(String captchaCode, String captchaSecret) {
        Optional<CaptchaCode> optionalCaptchaCode = captchaRepository.findBySecretCode(captchaSecret);

        if(optionalCaptchaCode.isPresent()){
            return optionalCaptchaCode.get().getCode().equals(captchaCode);
        }

        return false;
    }

    @SneakyThrows
    private String captchaGenerate(String captchaCode)  {
        Cage cage = new GCage();
        BufferedImage bufferedImage = cage.drawImage(captchaCode);
        byte[] bytes = imageResize(bufferedImage, 100, 35);//cage.draw(captchaCode);//cage.getTokenGenerator().next()
        return Base64Utils.encodeToString(bytes);
    }

    private String generateCaptchaCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder(SIZE_CAPTCHA_CODE);
        Random random = new Random();

        for (int i = 0; i < SIZE_CAPTCHA_CODE; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    @Transactional
    private synchronized void clearOldCaptcha() {
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(HOURS_FOR_EXPIRE);
        captchaRepository.deleteByTimeLessThan(localDateTime);
    }

    @SneakyThrows
    private byte[] imageResize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if(targetHeight <= 0 || targetWidth <= 0){
            throw new Exception("Одна из сторон изображения 0 или меньше");
        }
        byte[] imageInByte = null;
        BufferedImage resizedImage = new BufferedImage(
                targetWidth, targetHeight, originalImage.getType()
        );
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, "jpg", outputStream);
            outputStream.flush();
            imageInByte = outputStream.toByteArray();
        }

        return imageInByte;
    }
}
