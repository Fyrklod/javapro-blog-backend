package org.diplom.blog.service;

import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import org.diplom.blog.dto.ImageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * @author Andrey.Kazakov
 * @date 01.12.2020
 */
@Service
public class ImageService {

    @Value("${file-storage.relative-path.post}")
    private String uploadPostPath;
    @Value("${file-storage.relative-path.avatar}")
    private String uploadAvatarPath;
    @Value("${file-storage.depth}")
    private int depthStorage;

    //TODO:в ТЗ указаны только "jpg", "png"
    private final String[] permittedExtension = {"jpg", "png", "gif", "bmp"};

    @SneakyThrows
    public String uploadImage(MultipartFile multipartFile, ImageType type) {
        if(multipartFile == null || multipartFile.isEmpty()){
            throw new InvalidParameterException("Загружаемое сообщение не может быть пустым");
        }

        return uploadImage(multipartFile.getOriginalFilename(),
                           multipartFile.getBytes(),
                           type);
    }

    @SneakyThrows
    public String uploadImage(String fileName, byte[] originalFileBytes, ImageType type) {
        if(originalFileBytes == null) {
            throw new InvalidParameterException("Массив байтов файла не должен быть пустым");
        }

        String fileExtension = Objects.requireNonNull(StringUtils.getFilenameExtension(fileName))
                                                                 .toLowerCase();

        if(!Arrays.asList(permittedExtension).contains(fileExtension)) {
            throw new InvalidParameterException("Загружаемый файл не является изображением");
        }

        String uploadFolder = (type == ImageType.POST_IMAGE)
                                    ? uploadPostPath
                                    : uploadAvatarPath;
        String uploadSubFolder = generateSubFolderPath();
        Path absoluteUploadPath = Paths.get(uploadFolder + uploadSubFolder).toAbsolutePath();

        if (!Files.exists(absoluteUploadPath)) {
            Files.createDirectories(absoluteUploadPath);
        }

        Path uploadFilePath = Paths.get(absoluteUploadPath
                                        + "/" + UUID.randomUUID().toString()
                                        + "_" + fileName)
                                .toAbsolutePath();

        //multipartFile.transferTo(uploadFilePath);
        Files.write(uploadFilePath, originalFileBytes);

        return "\\" + Paths.get(uploadFolder)
                .toAbsolutePath()
                .getParent()
                .relativize(uploadFilePath)
                .toString();
    }

    @SneakyThrows
    public byte[] imageResize(InputStream steam, int targetWidth, int targetHeight){
        BufferedImage image = ImageIO.read(steam);
        return imageResize(image, targetWidth, targetHeight);
    }

    @SneakyThrows
    public byte[] imageResize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if(targetHeight <= 0 || targetWidth <= 0){
            throw new InvalidParameterException("Одна из сторон изображения 0 или меньше");
        }
        byte[] imageInByte;
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

    public void deleteImageFromStorage(String relativePathImage) {
        if(relativePathImage.startsWith("\\")){
            relativePathImage = relativePathImage.substring(1);
        }

        Path deletionImagePath = Paths.get(relativePathImage);
        deleteImageTreeFolderIfEmpty(deletionImagePath);
    }

    private void deleteImageTreeFolderIfEmpty(Path deletionPath) {
        Assert.notNull(deletionPath, "Удаляемый файл не может быть пустым");
        File deletionFile = deletionPath.toFile();
        //Assert.isTrue((!deletionFile.exists()), "Folder is not exists");


        for(int i=depthStorage; i>=0; i--) {
            //если в родительском каталоге более одого файла - удаляем текущий и выходим
            if(Objects.requireNonNull(deletionFile.getParentFile().list()).length > 1 || i==0) {
                if(!FileSystemUtils.deleteRecursively(deletionFile)){
                    System.out.println("не удалось удалить");
                }
                break;
            }
            //если не вышли, то заменяем удаляемый объект на родительский каталог
            deletionFile = deletionFile.getParentFile();
        }
    }

    private String generateSubFolderPath() {
        String[] folderName = {"ab", "cd", "ef"};

        int maxRang = folderName.length;

        StringBuilder pathBuilder = new StringBuilder("/");
        for(int i=0; i<depthStorage; i++){
            int randomIndex = new Random().nextInt(maxRang);

            String subFolder = (randomIndex >= folderName.length)
                    ? folderName[folderName.length]
                    : folderName[randomIndex];
            pathBuilder.append(subFolder).append("/");
        }

        return pathBuilder.toString();
    }
}
