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

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
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
    public static final Integer CAPTCHA_WIDTH = 100;
    public static final Integer CAPTCHA_HEIGHT = 35;

    private final CaptchaRepository captchaRepository;
    private final ImageService imageService;

    @Autowired
    public CaptchaService(CaptchaRepository captchaRepository,
                          ImageService imageService) {
        this.captchaRepository = captchaRepository;
        this.imageService = imageService;
    }

    /**
     * Метод getCaptcha.
     * Генерация капчи.
     *
     * @return ResponseEntity<CaptchaResponse>
     * @see CaptchaResponse ;
     */
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

    /**
     * Метод checkCaptchaCode.
     * Проверка кода капчи полученного с фронта с кодом зарошенного из базы по секретному коду.
     *
     * @param captchaCode - введенный код капчи на фронте.
     * @param captchaSecret - секретный код для получения капчи из базы.
     * @return true - если коды совпали, false - коды не совпали.
     */
    public boolean checkCaptchaCode(String captchaCode, String captchaSecret) {
        try {
            CaptchaCode optionalCaptchaCode = captchaRepository.findBySecretCode(captchaSecret)
                                                               .orElseThrow();

            return optionalCaptchaCode.getCode().equals(captchaCode);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Метод captchaGenerate.
     * Генерация изображения Капчи.
     *
     * @param captchaCode - код капчи.
     * @return String - закодированное сгенерированное изображение.
     */
    @SneakyThrows
    private String captchaGenerate(String captchaCode)  {
        Cage cage = new GCage();
        BufferedImage bufferedImage = cage.drawImage(captchaCode);
        byte[] bytes = imageService.imageResize(bufferedImage, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        return Base64Utils.encodeToString(bytes);
    }

    /**
     * Метод generateCaptchaCode.
     * Генерация кода капчи.
     *
     * @return String.
     */
    private String generateCaptchaCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder captchaStringBuilder = new StringBuilder(SIZE_CAPTCHA_CODE);
        Random random = new Random();

        for (int i = 0; i < SIZE_CAPTCHA_CODE; i++) {
            char symbol = chars[random.nextInt(chars.length)];
            captchaStringBuilder.append(symbol);
        }

        return captchaStringBuilder.toString();
    }

    /**
     * Метод clearOldCaptcha.
     * Очистка старой капчи.
     */
    @Transactional
    private void clearOldCaptcha() {
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(HOURS_FOR_EXPIRE);
        captchaRepository.deleteByTimeLessThan(localDateTime);
    }
}
