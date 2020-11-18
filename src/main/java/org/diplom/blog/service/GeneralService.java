package org.diplom.blog.service;

import org.diplom.blog.dto.Decision;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.api.response.StatisticsResponse;
import org.diplom.blog.dto.UserDto;
import org.diplom.blog.api.request.ModerationRequest;
import org.diplom.blog.api.response.*;
import org.diplom.blog.model.GlobalSetting;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.SettingsRepository;
import org.diplom.blog.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
public class GeneralService {

    @Value("${file-storage.relative-path}")
    private String uploadPath;
    @Value("${file-storage.depth}")
    private int depthStorage;

    private final UserService userService;
    private final PostRepository postRepository;
    private final SettingsRepository settingsRepository;

    @Autowired
    public GeneralService(UserService userService,
                          PostRepository postRepository,
                          SettingsRepository settingsRepository) {
        this.userService = userService;
        this.postRepository = postRepository;
        this.settingsRepository = settingsRepository;
    }

    public ResponseEntity<SettingsDto> getGlobalSettings(){
        SettingsDto settings = new SettingsDto();
        Iterable<GlobalSetting> globalSettings = settingsRepository.findAll();
        for (GlobalSetting setting : globalSettings) {
            settings.put(setting.getCode(),  setting.getValue().equals("YES"));
        }

        return ResponseEntity.ok(settings);
    }

    @Transactional
    public void setGlobalSettings(SettingsDto settings) throws Exception {

        if(settings != null && settings.size() > 0){
            List<GlobalSetting> globalSettings = new ArrayList<GlobalSetting>();

            for(String code : settings.keySet()) {
                if(settings.get(code) == null){
                    continue;
                }

                GlobalSetting setting = getSettingValueByCode(code);
                String settingValue = settings.get(code) ? "YES" : "NO";
                if(!settingValue.equals(setting.getCode())) {
                    setting.setValue(settingValue);
                    globalSettings.add(setting);
                }
            }

            if(!globalSettings.isEmpty()) {
                settingsRepository.saveAll(globalSettings);
            }
        }
    }

    //TODO: доработать
    public ResponseEntity<String> addImage(MultipartFile upload) {
        if(upload != null && upload.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }

        String retPath = "";//""/upload/cd/ef/ef/8449541f-138b-4947-b4bc-252c6960e44f_Apache-camel-logo.png";
        File uploadFolder = new File(uploadPath);

        try {
            String genericUploadDir = generateStorageFolderPath();
            Path absoluteUploadPath = Paths.get(genericUploadDir).toAbsolutePath();

            if (!Files.exists(absoluteUploadPath)) {
                Files.createDirectories(absoluteUploadPath);
            }

            Path uploadFilePath = Paths.get(absoluteUploadPath + "/" + UUID.randomUUID().toString() + "_" + upload.getOriginalFilename())
                    .toAbsolutePath();

            upload.transferTo(uploadFilePath);
            retPath = "\\" + uploadFolder.toPath().getParent().toAbsolutePath()
                                  .relativize(uploadFilePath).toString();

        } catch(IOException ex) {
            //TODO:доработать ошибку
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(retPath);
    }

    public ResponseEntity<CalendarResponse> getCalendar(String year) {

        Pattern pattern = Pattern.compile("(\\d{4})");
        Matcher matcher = pattern.matcher(year);
        DateFormat dateFormat = new SimpleDateFormat("yyyy");

        if(year.isBlank()){
            year = dateFormat.format(new Date());
        } else if(!matcher.find()){
            return ResponseEntity.badRequest().build();
        }
        //TODO: даты в базе в UTC, при получении, нужно приводить к TIMEZONE????
        List<Integer> listOfYear = postRepository.getDistinctYearAllPosts();
        List<Object[]> postCount = postRepository.getCountPostInDayOfYear(Integer.parseInt(year));
        Set<Integer> years = new HashSet<>(listOfYear);
        Map<String, Long> postCountMap = new HashMap<>();
        postCount.parallelStream()
                .forEach(obj -> postCountMap.put((String) obj[0], ((BigInteger)obj[1]).longValue()));

        CalendarResponse response = new CalendarResponse(years, postCountMap);

        return ResponseEntity.ok(response);
    }

    //TODO: доработать
    public ResponseEntity<AuthResponse> profile(String contentType, UserDto profile) {
        AuthResponse response = new AuthResponse(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        User currentUser = userService.getCurrentUser();

        List<Object[]> statistics = postRepository.getStatisticOfPostByUser(currentUser.getId());

        StatisticsResponse response = new StatisticsResponse()
                .setPostsCount(((BigInteger)statistics.get(0)[0]).longValue())
                .setLikesCount(((BigInteger) statistics.get(0)[3]).longValue())
                .setDislikesCount(((BigInteger) statistics.get(0)[4]).longValue())
                .setViewsCount(((BigInteger) statistics.get(0)[1]).longValue())
                .setFirstPublication(DateUtil.getLongFromTimestamp((Timestamp) statistics.get(0)[2]));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<StatisticsResponse> getAllStatistics() {

        List<Object[]> statistics = postRepository.getFullStatisticOfPost();

        StatisticsResponse response = new StatisticsResponse()
                .setPostsCount(((BigInteger)statistics.get(0)[0]).longValue())
                .setLikesCount(((BigInteger) statistics.get(0)[3]).longValue())
                .setDislikesCount(((BigInteger) statistics.get(0)[4]).longValue())
                .setViewsCount(((BigInteger) statistics.get(0)[1]).longValue())
                .setFirstPublication(DateUtil.getLongFromTimestamp((Timestamp) statistics.get(0)[2]));

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<CommonResponse> moderationPost(ModerationRequest request) {
        CommonResponse response = new CommonResponse();

        try {
            Post post = postRepository.findById(request.getPostId())
                            .orElseThrow(() -> new Exception("Not found post"));
            User moderator = userService.getCurrentUser();
            post.setModerationStatusValue(
                    request.getDecision().equals(Decision.ACCEPT)
                            ? ModerationStatus.ACCEPTED.toString()
                            : ModerationStatus.DECLINED.toString()
            );
            post.setModerator(moderator);
            postRepository.save(post);
            response.setResult(true);
        } catch (Exception ex) {
            response.setResult(false);
        }

        return ResponseEntity.ok(response);
    }

    public Boolean getBooleanSettingValueByCode(String settingCode) throws Exception {
        GlobalSetting setting = getSettingValueByCode(settingCode);
        return setting.getValue().equals("YES");
    }

    private GlobalSetting getSettingValueByCode(String settingCode) throws Exception {
        return settingsRepository.findByCode(settingCode)
                .orElseThrow(() -> new Exception(String.format("В базе отсутствует настройка с кодом %s"
                        , settingCode)));
    }

    private String generateStorageFolderPath(){
        //TODO: заменить этот массив
        String[] folderName = {"ab", "cd", "ef"};

        int maxRang = folderName.length;

        StringBuilder pathBuilder = new StringBuilder(uploadPath).append("/");
        for(int i=0; i<depthStorage; i++){
            int randomIndex = new Random().nextInt(maxRang);

            String subFolder = randomIndex >= folderName.length ? folderName[folderName.length] : folderName[randomIndex];
            pathBuilder.append(subFolder).append("/");
        }

        return pathBuilder.toString();
    }
}
