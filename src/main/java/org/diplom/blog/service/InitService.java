package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.response.InitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
public class InitService {

    private final InitResponse initResponse;

    @Autowired
    public InitService(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    public ResponseEntity<InitResponse> getInit(){
        return ResponseEntity.ok(initResponse);
    }
}
