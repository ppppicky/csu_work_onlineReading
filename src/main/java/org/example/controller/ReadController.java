package org.example.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BackgroundDTO;
import org.example.dto.ReadingSettingDTO;
import org.example.entity.FontResource;
import org.example.entity.Users;
import org.example.service.BackgroundService;
import org.example.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/read")
@Api(value = "阅读控制器",tags = "阅读管理接口")
@Slf4j
public class ReadController {
    @Autowired
    ReadService readService;

    @Autowired
    BackgroundService backgroundService;


    /**
     * 获取可用字体列表
     *
     * @return 字体列表
     */
    @GetMapping("/font/list")
    @ApiOperation(value = "获取所有可用字体", notes = "返回系统支持的字体列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取字体列表", response = FontResource.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "请求错误")
    })
    public ResponseEntity<List<FontResource>> getAvailableFonts() {
        log.info("获取所有可用字体");
        try {
            List<FontResource> list = readService.getAvailableFonts();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    /**
     * 上传新字体
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传新字体", notes = "用户上传新的字体文件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "字体上传成功"),
            @ApiResponse(code = 400, message = "上传失败")
    })
    @PostMapping("/font/add")
    public ResponseEntity<String> addFont(
            @ApiParam(value = "字体文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传新字体");
        try {
            readService.addFont(file);
            return ResponseEntity.ok("add font successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    /**
     * 上传背景图片/视频
     *
     * @param file
     * @return BackgroundDTO
     */
    @ApiOperation(value = "上传背景图片/视频", notes = "用户上传背景图片/视频")
    @PostMapping("/background/upload")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功", response = BackgroundDTO.class),
            @ApiResponse(code = 400, message = "上传失败")
    })
    public ResponseEntity<BackgroundDTO> uploadBackground(
            @ApiParam(value = "背景图片/视频文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传背景图片/视频");
        try {
            BackgroundDTO preview = backgroundService.uploadTemporary(file);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 上传渐变背景
     *
     * @param gradient 字符串gradient值
     * @return
     */
    @ApiOperation("上传渐变背景")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功", response = BackgroundDTO.class),
            @ApiResponse(code = 400, message = "上传失败")
    })
    @PostMapping("/background/upload/gradient")
    public ResponseEntity<BackgroundDTO> uploadBackground(
            @ApiParam(value = "渐变背景css字符串值", required = true) @RequestBody String gradient) {
        log.info("上传渐变背景");
        try {
            BackgroundDTO preview = backgroundService.uploadTemporary(gradient);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 确认背景保存
     *
     * @param tempId 背景tempId
     * @param userId
     * @return
     */
    @ApiOperation("确认背景保存")
    @PostMapping("/background/confirm")
    @ApiResponses({
            @ApiResponse(code = 200, message = "背景保存成功", response = String.class),
            @ApiResponse(code = 400, message = "操作失败")
    })
    public ResponseEntity<String> confirmBackground(
            @ApiParam(value = "临时背景ID", required = true) @RequestParam("tempId") String tempId,
            @ApiParam(value = "用户ID", required = true) @RequestParam("userId") Integer userId) {
        log.info("确认背景保存");
        try {
            backgroundService.confirmSave(tempId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 获取用户所有背景
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "获取指定用户所有背景", notes = "获取指定用户所有背景")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功", response = List.class),
            @ApiResponse(code = 400, message = "操作失败")
    })
    @GetMapping("/background/list/{userId}")
    public ResponseEntity<List<String>> userBackgroundsList(@AuthenticationPrincipal Users user) {
        log.info("获取用户所有背景");
        try {
            return ResponseEntity.ok(backgroundService.getUserBackgroundsUrl(user));
        } catch (Exception e) {
            log.error("获取用户所有背景失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 获取指定背景
     *
     * @param backgroundId
     * @return
     */
    @ApiOperation(value = "获取指定背景", notes = "获取指定背景")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取", response = BackgroundDTO.class),
            @ApiResponse(code = 400, message = "获取失败")
    })
    @GetMapping("/background/{backgroundId}")
    public ResponseEntity<String> getBackground(@PathVariable Integer backgroundId) {
        log.info("获取指定背景");
        try {

            return ResponseEntity.ok(backgroundService.getBackgroundUrl(backgroundId));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户阅读设置
     *
     * @param user
     * @return
     */
    @ApiOperation("获取用户阅读设置")
    @GetMapping("/setting/{userId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取设置", response = ReadingSettingDTO.class),
            @ApiResponse(code = 400, message = "获取失败")
    })
    public ResponseEntity<ReadingSettingDTO> getUserSettings(
            @AuthenticationPrincipal Users user) {
        log.info("获取用户阅读设置");
        try {
            ReadingSettingDTO settingDTO = readService.getUserSettings(user);

            return ResponseEntity.ok(settingDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 保存用户阅读设置
     *
     * @param userId
     * @param settingDTO
     * @return
     */
    @ApiOperation("保存用户阅读设置")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存设置成功", response = String.class),
            @ApiResponse(code = 400, message = "保存失败")
    })
    @PostMapping("/setting")
    public ResponseEntity<String> saveUserSettings(
            //@AuthenticationPrincipal Users user,
            @RequestParam("userId") Integer userId,
            @Valid @RequestBody ReadingSettingDTO settingDTO) {
        log.info("保存用户阅读设置");
        try {
            readService.updateUserSetting(userId, settingDTO);
            return ResponseEntity.ok("reading setting successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}