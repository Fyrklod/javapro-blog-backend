package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.EntityCount;
import org.diplom.blog.dto.Error;
import org.diplom.blog.dto.TagDto;
import org.diplom.blog.dto.UserDto;
import org.diplom.blog.dto.mapper.TagMapper;
import org.diplom.blog.dto.request.*;
import org.diplom.blog.dto.response.*;
import org.diplom.blog.model.Tag;
import org.diplom.blog.service.GeneralService;
import org.diplom.blog.service.InitService;
import org.diplom.blog.service.TagService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ApiGeneralController {
    //@Autowired
    private InitService initService;
    //@Autowired
    private GeneralService generalService;
    //@Autowired
    private TagService tagService;

    @GetMapping("/api/init")
    public InitResponse init() {
        return initService.getInit();
    }

    @PostMapping("/api/image")
    public ResponseEntity<String> addImage(@RequestHeader("Content-Type") String contentType, String upload) {
        String path = "";
        return ResponseEntity.status(HttpStatus.OK).body(path);
    }

    @PostMapping("/api/comment")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest comment) {
        CommentResponse response = new CommentResponse();
        Error error = new Error();
        error.setText("Метод не реализован");
        response.setErrors(error);
        response.setResult(false);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(defaultValue = "") String query) {
        TagResponse response = new TagResponse();

        List<EntityCount<Tag>> listTag = tagService.getTagsBySearch(query);
        if(listTag != null) {
            int maxCnt = (int) listTag.parallelStream().findFirst().get().getCountRecord();
            List<TagDto> tagDtoList = listTag.parallelStream()
                    .map(tc -> TagMapper.toTagDto(tc, maxCnt))
                    .collect(Collectors.toList());
            response.setTags(tagDtoList);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/api/moderation")
    public void moderation(@RequestParam ModerationRequest request) {

    }

    //TODO: переработать (результат не тот что ожидается)
    @GetMapping("/api/calendar")
    public @ResponseBody ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "") String year) {
        /*
        //TO BE
           {
                "years": [2017, 2018, 2019, 2020],
                "posts": {
                    "2019-12-17": 56,
                    "2019-12-14": 11,
                    "2019-06-17": 1,
                    "2020-03-12": 6
                }
            }
        //AS IS
        {
        "years":[2020],
        "posts":[] <--- выводится массив, должен быть объект с динамическими полями
        }
        * */

        CalendarResponse response = new CalendarResponse(Integer.parseInt(year));
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        if(!StringUtils.isEmptyOrWhitespace(year)){
            year = dateFormat.format(new Date());
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> profile(@RequestHeader("Content-Type") String contentType,
                                                @RequestBody UserDto profile) {
        AuthResponse response = new AuthResponse();
        response.setResult(true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        SettingsResponse settingsResponse = generalService.getGlobalSettings();
        return ResponseEntity.status(HttpStatus.OK).body(settingsResponse);
    }

    //TODO: переработать.. заменить JSONObject на Response
    @PutMapping("/api/settings")
    public @ResponseBody JSONObject modSettings() {
        return null;
    }
}
