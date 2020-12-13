package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.request.ModerationRequest;
import org.diplom.blog.api.request.ProfileRequest;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.api.response.StatisticsResponse;
import org.diplom.blog.api.response.*;
import org.diplom.blog.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final InitService initService;
    private final UserService userService;
    private final GeneralService generalService;
    private final CommentService commentService;
    private final SettingService settingService;
    private final TagService tagService;

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<String> addImage(@RequestParam MultipartFile image) {
        return generalService.addImage(image);
    }

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest comment) {
        return commentService.addComment(comment);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(defaultValue = "") String query) {
        return tagService.getTagsBySearch(query);
    }

    @PostMapping("/api/moderation")
    @PreAuthorize("hasAuthority('user:approver')")
    public ResponseEntity<SimpleResponse> moderation(@RequestBody ModerationRequest request) {
        return generalService.moderationPost(request);
    }

    @GetMapping("/api/calendar")
    public @ResponseBody ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "") String year) {
        return generalService.getCalendar(year);
    }

    @PostMapping(value = "/api/profile/my",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<ProfileResponse> profile(@RequestPart(name = "photo", required = false) MultipartFile photo,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) String password,
                                                @RequestParam(required = false) int removePhoto) {

        return userService.profile(photo, name, email, password, removePhoto);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<ProfileResponse> profile(@RequestBody ProfileRequest profileRequest) {
        return userService.profile(profileRequest);
    }

    @GetMapping("/api/statistics/my")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        return settingService.getMyStatistics();
    }


    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        return settingService.getAllStatistics();
    }

    @GetMapping("/api/settings")
    public ResponseEntity<SettingsDto> getSettings() {
        return settingService.getGlobalSettings();
    }

    @PutMapping("/api/settings")
    @PreAuthorize("hasAuthority('user:approver')")
    public void modSettings(@RequestBody SettingsDto settings) throws Exception {
        settingService.setGlobalSettings(settings);
    }
}
