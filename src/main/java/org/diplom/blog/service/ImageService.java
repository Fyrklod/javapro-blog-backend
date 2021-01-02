package org.diplom.blog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.diplom.blog.api.request.CommentRequest;
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
@Slf4j
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

    private final String[] permittedExtension = {"jpg", "png", "gif", "bmp"};

    @Autowired
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Метод uploadImage.
     * Загрузка изображения.
     *
     * @param multipartFile multipart form-data файл
     * @param imageType - тип изображения (для поста или аватарка).
     * @return относительный путь загруженного изображения.
     * @see ImageType ;
     */
    @SneakyThrows
    public String uploadImage(MultipartFile multipartFile, ImageType imageType) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidParameterException("Загружаемое сообщение не может быть пустым");
        }

        return (StorageType.StorageType(storageType)  == StorageType.CLOUD)
                    ? uploadImageToCloud(multipartFile)
                    : uploadImageToLocalFolder(multipartFile, imageType);
    }

    /**
     * Метод uploadImage.
     * Загрузка изображения.
     *
     * @param fileName имя файла
     * @param originalFileBytes - массив байтов изображения.
     * @param imageType - тип изображения (для поста или аватарка).
     * @return относительный путь загруженного изображения.
     * @see ImageType ;
     */
    @SneakyThrows
    public String uploadImage(String fileName, byte[] originalFileBytes, ImageType imageType) {
        if(originalFileBytes == null) {
            throw new InvalidParameterException("Массив байтов файла не должен быть пустым");
        }

        return (StorageType.StorageType(storageType)  == StorageType.CLOUD)
                ? uploadImageToCloud(originalFileBytes)
                : uploadImageToLocalFolder(fileName, originalFileBytes, imageType);
    }

    /**
     * Метод deleteImage.
     * Удаление изображения.
     *
     * @param relativePathImage относительный путь к изображению
     */
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

    /**
     * Метод imageResize.
     * Изменение размеров изображения.
     *
     * @param inputStream - поток изображения
     * @param targetWidth - ширина.
     * @param targetHeight - высота.
     * @return массив байтов измененного изображения.
     */
    @SneakyThrows
    public byte[] imageResize(InputStream inputStream, int targetWidth, int targetHeight){
        BufferedImage image = ImageIO.read(inputStream);
        return imageResize(image, targetWidth, targetHeight);
    }

    /**
     * Метод imageResize.
     * Изменение размеров изображения.
     *
     * @param originalImage - буффер изображения
     * @param targetWidth - ширина.
     * @param targetHeight - высота.
     * @return массив байтов измененного изображения.
     */
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

    /**
     * Метод uploadImageToCloud.
     * Загрузка изображения в облако.
     *
     * @param multipartFile multipart form-data файл
     * @return относительный путь загруженного изображения.
     */
    @SneakyThrows
    private String uploadImageToCloud(MultipartFile multipartFile) {
        return uploadImageToCloud(multipartFile.getBytes());
    }

    /**
     * Метод uploadImageToCloud.
     * Загрузка изображения в облако.
     *
     * @param originalFileBytes массив байтов загружаемого файла
     * @return относительный путь загруженного изображения.
     */
    @SneakyThrows
    private String uploadImageToCloud(byte[] originalFileBytes) {
        Map uploadResult = cloudinary.uploader().upload(originalFileBytes, ObjectUtils.emptyMap());
        String result = (String) uploadResult.get("secure_url");
        // вернет /javaesbblog/image/upload/v1608324264/rr1qwgjrutu5xp1ahrfd.jpg
        return result.substring(result.indexOf(cloudName) - 1);
    }

    /**
     * Метод uploadImageToLocalFolder.
     * Загрузка изображения в локальную папку на сервере.
     *
     * @param multipartFile multipart form-data файл
     * @param imageType - тип изображения (для поста или аватарка).
     * @return относительный путь загруженного изображения.
     * @see ImageType ;
     */
    @SneakyThrows
    private String uploadImageToLocalFolder(MultipartFile multipartFile, ImageType imageType) {

        return uploadImageToLocalFolder(multipartFile.getOriginalFilename(),
                multipartFile.getBytes(),
                imageType);
    }

    /**
     * Метод uploadImageToLocalFolder.
     * Загрузка изображения в локальную папку на сервере.
     *
     * @param fileName имя файла
     * @param originalFileBytes - массив байтов изображения.
     * @param imageType - тип изображения (для поста или аватарка).
     * @return относительный путь загруженного изображения.
     * @see ImageType ;
     */
    @SneakyThrows
    private String uploadImageToLocalFolder(String fileName, byte[] originalFileBytes, ImageType imageType) {
        String fileExtension = Objects.requireNonNull(StringUtils.getFilenameExtension(fileName))
                                                                 .toLowerCase();

        if(!Arrays.asList(permittedExtension).contains(fileExtension)) {
            throw new InvalidParameterException("Загружаемый файл не является изображением");
        }

        String uploadFolder = (imageType == ImageType.POST_IMAGE)
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

        Files.write(uploadFilePath, originalFileBytes);

        return "\\" + Paths.get(uploadFolder)
                .toAbsolutePath()
                .getParent()
                .relativize(uploadFilePath)
                .toString();
    }

    /**
     * Метод deleteImageFromCloud.
     * Удаление изображения в облаке.
     *
     * @param publicName уникальное имя файла
     */
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

    /**
     * Метод deleteImageFromLocalStorage.
     * Удаление изображения из локальной папки сервера.
     *
     * @param relativePathImage относительный путь к изображению
     */
    private void deleteImageFromLocalStorage(String relativePathImage) {
        if(relativePathImage.startsWith("\\")){
            relativePathImage = relativePathImage.substring(1);
        }

        Path deletionImagePath = Paths.get(relativePathImage);
        deleteImageTreeFolderIfEmpty(deletionImagePath);
    }

    /**
     * Метод deleteImageTreeFolderIfEmpty.
     * Удаление изображения вместе с деревом каталогов (если они остаются пустыми).
     *
     * @param deletionPath относительный путь к изображению
     */
    private void deleteImageTreeFolderIfEmpty(Path deletionPath) {
        Assert.notNull(deletionPath, "Удаляемый файл не может быть пустым");
        File deletionFile = deletionPath.toFile();

        for(int i=depthStorage; i>=0; i--) {
            //если в родительском каталоге более одого файла - удаляем текущий и выходим
            if(Objects.requireNonNull(deletionFile.getParentFile().list()).length > 1 || i==0) {
                if(!FileSystemUtils.deleteRecursively(deletionFile)){
                    log.info("Не удалось удалить {}", deletionFile);
                }
                break;
            }
            //если не вышли, то заменяем удаляемый объект на родительский каталог
            deletionFile = deletionFile.getParentFile();
        }
    }

    /**
     * Метод generateSubFolderPath.
     * Генерация пути вложенных каталогов.
     *
     * @return относительный путь к файлу.
     */
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
