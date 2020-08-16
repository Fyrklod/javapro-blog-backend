package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.response.SettingsResponse;
import org.diplom.blog.model.GlobalSetting;
import org.diplom.blog.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
@AllArgsConstructor
public class GeneralService {
    //@Autowired
    private SettingsRepository settingsRepository;

    public SettingsResponse getGlobalSettings(){
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSetting> globalSettings = settingsRepository.findAll();
        for (GlobalSetting setting : globalSettings) {
            settingsResponse.put(setting.getCode(), setting.getValue().equals("YES"));
        }

        return settingsResponse;
    }
}
