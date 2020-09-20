package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.repository.CommentRepository;
import org.diplom.blog.repository.SettingsRepository;
import org.springframework.stereotype.Service;

/**
 * @author Andrey.Kazakov
 * @date 16.09.2020
 */
@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
}
