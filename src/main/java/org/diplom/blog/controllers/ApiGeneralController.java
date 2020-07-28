package org.diplom.blog.controllers;

import org.diplom.blog.dto.model.Error;
import org.diplom.blog.dto.model.User;
import org.diplom.blog.dto.request.*;
import org.diplom.blog.dto.response.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
public class ApiGeneralController {

    @Value("${blog.info.title}")
    private String title;

    @Value("${blog.info.subtitle}")
    private String subtitle;

    @Value("${blog.info.contact.phone}")
    private String phone;

    @Value("${blog.info.contact.email}")
    private String email;

    @Value("${blog.info.copyright}")
    private String copyright;

    @Value("${blog.info.copyrightFrom}")
    private String copyrightFrom;

    @GetMapping("/api/init")
    public @ResponseBody JSONObject init() {
        //TODO:переработать
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("subtitle", subtitle);
        json.put("phone", phone);
        json.put("email", email);
        json.put("copyright", copyright);
        json.put("copyrightFrom", copyrightFrom);

        return json;
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
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/api/moderation")
    public void moderation(@RequestParam ModerationRequest request) {

    }

    @GetMapping("/api/calendar")
    public @ResponseBody JSONObject getCalendar(@RequestParam @DateTimeFormat(pattern="yyyy") Date year) {

        return null;
    }

    @PostMapping("/api/profile/my")
    public ResponseEntity<AuthResponse> profile(@RequestHeader("Content-Type") String contentType,
                                                @RequestBody User profile) {
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
    public @ResponseBody JSONObject getSettings() {

        return null;
    }

    @PutMapping("/api/settings")
    public @ResponseBody JSONObject modSettings() {

        return null;
    }
}
