package org.example.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ForbiddenWord;
import org.example.service.ForbiddenService;
import org.example.util.ContentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "违禁词管理，需要管理员权限",value = "违禁词管理")
@RestController
@RequestMapping("/forbidden_word")
//@PreAuthorize("hasRole('ADMIN')")
public class ForbiddenWordController {
    @Autowired
    ForbiddenService forbiddenService;
@Autowired
    ContentFilter contentFilter;
    @PostMapping("/add")
    public ForbiddenWord addWord(@RequestBody String word) {
        log.info("违禁词管理");
             return forbiddenService.addWord(word);


    }
    @PostMapping("/ce")
    public ResponseEntity<String> ce(@RequestBody String word){
        return ResponseEntity.ok( contentFilter.filterFromFile(word)+"     "+contentFilter.filterFromDB(word));
    }

    @DeleteMapping("/{word}")
    public void removeWord(@PathVariable String word) {
        log.info(word);
       forbiddenService.removeWord(word);
    }
}


