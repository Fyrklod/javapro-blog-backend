package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.Error;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.dto.response.StatisticsResponse;
import org.diplom.blog.dto.UserDto;
import org.diplom.blog.dto.request.CommentRequest;
import org.diplom.blog.dto.request.ModerationRequest;
import org.diplom.blog.dto.response.*;
import org.diplom.blog.model.GlobalSetting;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.SettingsRepository;
import org.diplom.blog.utils.DateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
@AllArgsConstructor
public class GeneralService {

    private final PostRepository postRepository;
    private final SettingsRepository settingsRepository;
    private final Map<String, Boolean> settingsMap;

    public ResponseEntity<SettingsDto> getGlobalSettings(){
        SettingsDto settings = new SettingsDto();
        Iterable<GlobalSetting> globalSettings = settingsRepository.findAll();
        for (GlobalSetting setting : globalSettings) {
            settings.put(setting.getCode(), setting.getValue().equals("YES"));
        }

        settingsMap.putAll(settings);

        return ResponseEntity.ok(settings);
    }

    @Transactional
    public void setGlobalSettings(SettingsDto settings) {

        if(settings != null && settings.size() > 0){
            List<GlobalSetting> globalSettings = new ArrayList<GlobalSetting>();

            for(String code : settings.keySet()){
                GlobalSetting setting = settingsRepository.findByCode(code);
                String settingValue = settings.get(code) ? "NO" : "YES";
                setting.setValue(settingValue);
                globalSettings.add(setting);
            }

            settingsRepository.saveAll(globalSettings);
        }
    }

    public ResponseEntity<String> addImage(String contentType, String upload){
        String path = "";
        return ResponseEntity.ok(path);
    }

    //TODO: доработать вместе с репозиторием
    public ResponseEntity<CommentResponse> addComment(CommentRequest comment) {
        CommentResponse response = new CommentResponse();
        Error error = new Error();
        error.setText("Метод не реализован");
        response.setErrors(error);
        response.setResult(false);
        return ResponseEntity.ok(response);
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

    public ResponseEntity<AuthResponse> profile(String contentType, UserDto profile) {
        AuthResponse response = new AuthResponse(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        StatisticsResponse response = new StatisticsResponse();
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

    public ResponseEntity<CommonResponse> moderationPost(ModerationRequest request) {
        CommonResponse response = new CommonResponse();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    public Boolean getSettingValueByCode(String settingCode) {
        if(settingsMap.containsKey(settingCode)) {
            return settingsMap.get(settingCode);
        }

        return false;
    }
}
