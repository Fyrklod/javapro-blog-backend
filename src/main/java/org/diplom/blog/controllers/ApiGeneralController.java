package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.request.ModerationRequest;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.api.response.StatisticsResponse;
import org.diplom.blog.dto.UserDto;
import org.diplom.blog.api.response.*;
import org.diplom.blog.service.GeneralService;
import org.diplom.blog.service.InitService;
import org.diplom.blog.service.TagService;
//import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final InitService initService;
    private final GeneralService generalService;
    private final TagService tagService;

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    @PostMapping("/api/image")
    public ResponseEntity<String> addImage(@RequestHeader("Content-Type") String contentType, String upload) {
        return generalService.addImage(contentType, upload);
    }

    @PostMapping("/api/comment")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest comment) {
        return generalService.addComment(comment);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(defaultValue = "") String query) {
        return tagService.getTagsBySearch(query);
    }

    @PostMapping("/api/moderation")
    public ResponseEntity<CommonResponse> moderation(@RequestParam ModerationRequest request) {
        return generalService.moderationPost(request);
    }

    //TODO: переработать (результат не тот что ожидается)
    @GetMapping("/api/calendar")
    public @ResponseBody ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "") String year) {
        return generalService.getCalendar(year);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> profile(@RequestHeader("Content-Type") String contentType,
                                                @RequestBody UserDto profile) {

        return generalService.profile(contentType, profile);
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        return generalService.getMyStatistics();
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        return generalService.getAllStatistics();
    }

    @GetMapping("/api/settings")
    public ResponseEntity<SettingsDto> getSettings() {
        return generalService.getGlobalSettings();
    }

    @PutMapping("/api/settings")
    public void modSettings(@RequestBody SettingsDto settings) {
        generalService.setGlobalSettings(settings);
    }
}
