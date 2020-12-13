package org.diplom.blog.service;

import lombok.SneakyThrows;
import org.diplom.blog.api.response.StatisticsResponse;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.model.GlobalSetting;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.SettingsRepository;
import org.diplom.blog.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey.Kazakov
 * @date 13.12.2020
 */
@Service
public class SettingService {

    private final SettingsRepository settingsRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public SettingService(SettingsRepository settingsRepository,
                          PostRepository postRepository,
                          UserService userService) {
        this.settingsRepository = settingsRepository;
        this.postRepository = postRepository;
        this.userService = userService;
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
            List<GlobalSetting> globalSettings = new ArrayList<>();

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

    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        User currentUser = userService.getCurrentUser();

        List<Object[]> statistics = postRepository.getStatisticOfPostByUser(currentUser.getId());

        StatisticsResponse response = StatisticsResponse.builder()
                .postsCount(((BigInteger)statistics.get(0)[0]).longValue())
                .likesCount(((BigInteger) statistics.get(0)[3]).longValue())
                .dislikesCount(((BigInteger) statistics.get(0)[4]).longValue())
                .viewsCount(((BigInteger) statistics.get(0)[1]).longValue())
                .firstPublication(DateUtil.getLongFromTimestamp((Timestamp) statistics.get(0)[2]))
                .build();

        return ResponseEntity.ok(response);
    }

    @SneakyThrows
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        Boolean statisticsIsPublic = getBooleanSettingValueByCode("STATISTICS_IS_PUBLIC");

        if(!statisticsIsPublic) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Object[]> statistics = postRepository.getFullStatisticOfPost();

        StatisticsResponse response = StatisticsResponse.builder()
                .postsCount(((BigInteger)statistics.get(0)[0]).longValue())
                .likesCount(((BigInteger) statistics.get(0)[3]).longValue())
                .dislikesCount(((BigInteger) statistics.get(0)[4]).longValue())
                .viewsCount(((BigInteger) statistics.get(0)[1]).longValue())
                .firstPublication(DateUtil.getLongFromTimestamp((Timestamp) statistics.get(0)[2]))
                .build();

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
}
