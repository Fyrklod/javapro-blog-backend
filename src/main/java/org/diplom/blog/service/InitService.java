package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.response.InitResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
@AllArgsConstructor
public class InitService {

    private final InitResponse initResponse;

    public ResponseEntity<InitResponse> getInit(){
        return ResponseEntity.ok(initResponse);
    }
}
