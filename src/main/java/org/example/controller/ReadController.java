package org.example.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ResourceNotFoundException;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    public ResponseEntity<List<FontResource>> getAvailableFonts() {
        log.info("获取所有可用字体");
        try {
            List<FontResource> list = readService.getAvailableFonts();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("[字体管理] 获取字体列表失败 | Error={}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    /**
     * 上传新字体
     *
     * @param file
     * @return
     */
    @PostMapping("/font/add")
    @ApiOperation(value = "上传新字体", notes = "用户上传新的字体文件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "字体上传成功"),
            @ApiResponse(code = 400, message = "无效文件格式"),
            @ApiResponse(code = 500, message = "上传失败")
    })
    public ResponseEntity<String> addFont(
            @ApiParam(value = "字体文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传新字体");
        try {
            readService.addFont(file);
            return ResponseEntity.ok("add font successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
            @ApiResponse(code = 400, message = "无效文件类型"),
            @ApiResponse(code = 504, message = "处理超时")
    })
    public ResponseEntity<BackgroundDTO> uploadBackground(
            @ApiParam(value = "背景文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传背景文件: {}", file.getOriginalFilename());
        try {
            CompletableFuture<BackgroundDTO> future = backgroundService.uploadTemporary(file);
            BackgroundDTO preview = future.get(25, TimeUnit.SECONDS); // 限制最多等待25秒
            return ResponseEntity.ok(preview);
        } catch (TimeoutException e) {
            log.warn("[背景管理] 上传超时 | 文件名={}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            log.error("[背景管理]上传失败 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 上传渐变背景
     *
     * @param gradient 字符串gradient值
     * @return
     */
    @PostMapping("/background/upload/gradient")
    @ApiOperation("上传渐变背景")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功", response = BackgroundDTO.class),
            @ApiResponse(code = 400, message = "无效渐变参数")
    })
    public ResponseEntity<BackgroundDTO> uploadBackground(
            @ApiParam(value = "渐变背景css字符串值", required = true) @RequestBody String gradient) {
        log.info("上传渐变背景");
        try {
            CompletableFuture<BackgroundDTO> future = backgroundService.uploadTemporary(gradient);
            BackgroundDTO preview = future.get(25, TimeUnit.SECONDS); // 限制最多等待25秒
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            log.error("上传失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            @ApiResponse(code = 400, message = "操作失败"),
            @ApiResponse(code = 409, message = "资源已过期")
    })
    public ResponseEntity<String> confirmBackground(
            @ApiParam(value = "临时背景ID", required = true) @RequestParam("tempId") String tempId,
            @ApiParam(value = "用户ID", required = true) @RequestParam("userId") Integer userId) {
        log.info("确认背景: tempId={}, userId={}", tempId, userId);

        try {
            String result = backgroundService.confirmSave(tempId, userId)
                    .get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        } catch (Exception e) {
            log.error("[背景管理] 保存失败 | TempID={}, Error={}", tempId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<List<String>> userBackgroundsList(
            @AuthenticationPrincipal Users user) {
        log.info("获取用户所有背景");
        try {
            return ResponseEntity.ok(backgroundService.getUserBackgroundsUrl(user));
        } catch (Exception e) {
            log.error("[背景管理] 查询失败 | 用户={}, Error={}", user.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    @GetMapping("/setting")
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
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[阅读设置] 获取失败 | 用户={}, Error={}", user.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            @ApiResponse(code = 200, message = "保存设置成功"),
            @ApiResponse(code = 400, message = "保存失败")
    })
    @PostMapping("/setting")
    public ResponseEntity<String> saveUserSettings(
           // @AuthenticationPrincipal Users user,
              @RequestParam("userId") Integer userId,
            @Valid @RequestBody ReadingSettingDTO settingDTO) {
        log.info("保存用户阅读设置");
        try {
            readService.updateUserSetting(userId, settingDTO);
            return ResponseEntity.ok("reading setting successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}