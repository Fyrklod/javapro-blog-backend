package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.request.ModerationRequest;
import org.diplom.blog.dto.SettingsDto;
import org.diplom.blog.api.response.StatisticsResponse;
import org.diplom.blog.dto.UserDto;
import org.diplom.blog.api.response.*;
import org.diplom.blog.service.CommentService;
import org.diplom.blog.service.GeneralService;
import org.diplom.blog.service.InitService;
import org.diplom.blog.service.TagService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final InitService initService;
    private final GeneralService generalService;
    private final CommentService commentService;
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

    //TODO: доработать вместе с репозиторием
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
    public ResponseEntity<CommonResponse> moderation(@RequestBody ModerationRequest request) {
        return generalService.moderationPost(request);
    }

    @GetMapping("/api/calendar")
    public @ResponseBody ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "") String year) {
        return generalService.getCalendar(year);
    }

    //TODO: доработать вместе с репозиторием
    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<AuthResponse> profile(@RequestHeader("Content-Type") String contentType,
                                                @RequestBody UserDto profile) {

        return generalService.profile(contentType, profile);
    }

    @GetMapping("/api/statistics/my")
    @PreAuthorize("hasAuthority('user:writer')")
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
    @PreAuthorize("hasAuthority('user:approver')")
    public void modSettings(@RequestBody SettingsDto settings) throws Exception {
        generalService.setGlobalSettings(settings);
    }
}
