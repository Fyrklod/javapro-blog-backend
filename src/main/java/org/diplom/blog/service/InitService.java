package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.response.InitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
@AllArgsConstructor
public class InitService {

    //@Autowired
    private InitResponse initResponse;

    public InitResponse getInit(){
        return initResponse;
    }
}
