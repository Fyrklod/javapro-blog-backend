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

    /**
     * Метод init - Общие данные блога.
     * Метод возвращает общую информацию о блоге: название блога и подзаголовок для размещения в хэдере сайта,
     * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
     * Get запрос /api/init/
     *
     * @return ResponseEntity<InitResponse>.
     * @see InitResponse;
     */
    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    /**
     * Метод addImage - Загрузка изображений.
     * Метод загружает на сервер изображение в папку upload и три случайные подпапки.
     * Метод возвращает путь до изображения.
     * POST запрос /api/image
     *
     * @param image multipart form-data файл
     * @return ResponseEntity<String>
     */
    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<String> addImage(@RequestParam MultipartFile image) {
        return generalService.addImage(image);
    }

    /**
     * Метод addComment - Отправка комментария к посту.
     * Метод добавляет комментарий к посту.
     * POST запрос /api/comment
     *
     * @param commentRequest тело запроса в формате Json.
     * @return ResponseEntity<CommentResponse>.
     * @see CommentRequest;
     * @see CommentResponse;
     */
    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest commentRequest) {
        return commentService.addComment(commentRequest);
    }

    /**
     * Метод getTags - Получение списка тэгов.
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре query. В случае, если она не задана,
     * выводятся все тэги.
     * GET запрос /api/tag
     *
     * @param query подстрока запроса.
     * @return ResponseEntity<TagResponse>.
     * @see TagResponse;
     */
    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(defaultValue = "") String query) {
        return tagService.getTagsBySearch(query);
    }

    /**
     * Метод markPostsAsChecked - Модерация поста.
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение. Кроме того,
     * фиксируется moderator_id - идентификатор пользователя, который отмодерировал пост..
     * POST запрос /api/moderation"
     *
     * @param moderationRequest тело запроса в формате Json.
     * @return ResponseEntity<SimpleResponse>.
     * @see ModerationRequest;
     * @see SimpleResponse;
     */
    @PostMapping("/api/moderation")
    @PreAuthorize("hasAuthority('user:approver')")
    public ResponseEntity<SimpleResponse> markPostsAsChecked(@RequestBody ModerationRequest moderationRequest) {
        return generalService.moderationPost(moderationRequest);
    }

    /**
     * Метод getCalendar - Календарь (количества публикаций).
     * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
     * если параметр year не задан.
     * GET запрос /api/calendar
     *
     * @param year год для которого производится запрос.
     * @return ResponseEntity<CalendarResponse>.
     * @see CalendarResponse;
     */
    @GetMapping("/api/calendar")
    public @ResponseBody ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "") String year) {
        return generalService.getCalendar(year);
    }

    /**
     * Метод modifiedProfile - Редактирование моего профиля.
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля.
     * POST запрос /api/profile/my
     *
     * @param photo multipart form-data файл.
     * @param name обновленное имя.
     * @param email обновленный email.
     * @param password обновленный пароль.
     * @param removePhoto признак удаления фото.
     * @return ResponseEntity<ProfileResponse>.
     * @see ProfileResponse;
     */
    @PostMapping(value = "/api/profile/my",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<ProfileResponse> modifiedProfile(@RequestPart(name = "photo", required = false) MultipartFile photo,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) String password,
                                                @RequestParam(required = false) int removePhoto) {

        return userService.profile(photo, name, email, password, removePhoto);
    }

    /**
     * Метод modifiedProfile - Редактирование моего профиля.
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля.
     * POST запрос /api/profile/my
     *
     * @param profileRequest тело запроса в формате Json.
     * @return ResponseEntity<ProfileResponse>.
     * @see ProfileRequest;
     * @see ProfileResponse;
     */
    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<ProfileResponse> modifiedProfile(@RequestBody ProfileRequest profileRequest) {
        return userService.profile(profileRequest);
    }

    /**
     * Метод getMyStatistics - Моя статистика.
     * Метод возвращает статистику постов текущего авторизованного пользователя: общие количества параметров для
     * всех публикаций, у который он является автором и доступные для чтения.
     * GET запрос /api/statistics/my
     *
     * @return ResponseEntity<StatisticsResponse>.
     * @see StatisticsResponse;
     */
    @GetMapping("/api/statistics/my")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        return settingService.getMyStatistics();
    }

    /**
     * Метод getAllStatistics - Статистика по всему блогу.
     * Метод выдаёт статистику по всем постам блога.
     * GET запрос /api/statistics/all
     *
     * @return ResponseEntity<StatisticsResponse>.
     * @see StatisticsResponse;
     */
    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        return settingService.getAllStatistics();
    }

    /**
     * Метод getSettings - Получение настроек.
     * Метод возвращает глобальные настройки блога.
     * GET запрос /api/settings
     *
     * @return ResponseEntity<SettingsDto>.
     * @see SettingsDto;
     */
    @GetMapping("/api/settings")
    public ResponseEntity<SettingsDto> getSettings() {
        return settingService.getGlobalSettings();
    }

    /**
     * Метод modSettings - Сохранение настроек.
     * Метод записывает глобальные настройки блога в таблицу.
     * PUT  запрос /api/settings
     *
     * @param settings тело запроса в формате Json.
     * @see SettingsDto;
     */
    @PutMapping("/api/settings")
    @PreAuthorize("hasAuthority('user:approver')")
    public void modSettings(@RequestBody SettingsDto settings) throws Exception {
        settingService.saveGlobalSettings(settings);
    }
}
