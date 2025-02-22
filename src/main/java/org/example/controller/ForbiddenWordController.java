package org.example.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.service.ForbiddenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "违禁词管理，需要管理员权限",value = "违禁词管理")
@RestController
@RequestMapping("/forbidden_word")
//@PreAuthorize("hasRole('ADMIN')")
public class ForbiddenWordController {
    @Autowired
    ForbiddenService forbiddenService;

    @PostMapping("/add")
    public ForbiddenWord addWord(@RequestBody String word) {
        log.info("违禁词管理");
             return forbiddenService.addWord(word);


    }

    @DeleteMapping("/{word}")
    public void removeWord(@PathVariable String word) {
        log.info(word);
       forbiddenService.removeWord(word);
    }
}


