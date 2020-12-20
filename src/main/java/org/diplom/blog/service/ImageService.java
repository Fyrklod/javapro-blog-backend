package org.diplom.blog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.diplom.blog.dto.ImageType;
import org.diplom.blog.dto.StorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * @author Andrey.Kazakov
 * @date 01.12.2020
 */
@Service
public class ImageService {

    @Value("${file-storage.type}")
    private String storageType;

    @Value("${cloudinary.url-resources}")
    private String urlResources;
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${file-storage.relative-path.post}")
    private String uploadPostPath;
    @Value("${file-storage.relative-path.avatar}")
    private String uploadAvatarPath;
    @Value("${file-storage.temp-location}")
    private String tmpUploadPath;
    @Value("${file-storage.depth}")
    private int depthStorage;

    private final Cloudinary cloudinary;
    //TODO:в ТЗ указаны только "jpg", "png"
    private final String[] permittedExtension = {"jpg", "png", "gif", "bmp"};

    @Autowired
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @SneakyThrows
    public String uploadImage(MultipartFile multipartFile, ImageType type) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidParameterException("Загружаемое сообщение не может быть пустым");
        }

        return (StorageType.StorageType(storageType)  == StorageType.CLOUD)
                    ? uploadImageToCloud(multipartFile)
                    : uploadImageToLocalFolder(multipartFile, type);
    }

    @SneakyThrows
    public String uploadImage(String fileName, byte[] originalFileBytes, ImageType type) {
        if(originalFileBytes == null) {
            throw new InvalidParameterException("Массив байтов файла не должен быть пустым");
        }

        return (StorageType.StorageType(storageType)  == StorageType.CLOUD)
                ? uploadImageToCloud(originalFileBytes)
                : uploadImageToLocalFolder(fileName, originalFileBytes, type);
    }

    @SneakyThrows
    public void deleteImage(String relativePathImage) {
        if(StorageType.StorageType(storageType)  == StorageType.CLOUD){
            deleteImageFromCloud(
                    FilenameUtils.removeExtension(
                        StringUtils.getFilename(relativePathImage)
                    )
            );
        } else {
            deleteImageFromLocalStorage(relativePathImage);
        }
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

    @SneakyThrows
    private String uploadImageToCloud(MultipartFile multipartFile) {
        return uploadImageToCloud(multipartFile.getBytes());
    }

    @SneakyThrows
    private String uploadImageToCloud(byte[] originalFileBytes) {
        Map uploadResult = cloudinary.uploader().upload(originalFileBytes, ObjectUtils.emptyMap());
        String result = (String) uploadResult.get("secure_url");
        // вернет /javaesbblog/image/upload/v1608324264/rr1qwgjrutu5xp1ahrfd.jpg
        return result.substring(result.indexOf(cloudName) - 1);
    }

    @SneakyThrows
    private String uploadImageToLocalFolder(MultipartFile multipartFile, ImageType type) {

        return uploadImageToLocalFolder(multipartFile.getOriginalFilename(),
                multipartFile.getBytes(),
                type);
    }

    @SneakyThrows
    private String uploadImageToLocalFolder(String fileName, byte[] originalFileBytes, ImageType type) {
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

    private void deleteImageFromCloud(String publicName) {
        try {
            if (publicName.isBlank()) {
                throw new InvalidParameterException("Не указано имя удаляемого файла");
            }

            Map result = cloudinary.uploader().destroy(publicName, ObjectUtils.emptyMap());
            //раскомментировать, если нужно будет выдавать ошибку, если удаляемое изображение не найдено
            /*if(result.containsKey("result") && result.get("result")=="not found") {
                throw new InvalidParameterException("В облаке отсутствует файл с публичным именем '" + publicName + "'");
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteImageFromLocalStorage(String relativePathImage) {
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
