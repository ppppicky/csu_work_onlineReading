package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.example.dto.ReadingSettingDTO;
import org.example.entity.*;
import org.example.mapper.ReadMapper;
import org.example.repository.*;
import org.example.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class ReadSImpl implements ReadService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FontRepository fontRepository;
    @Autowired
    private BackgroundRepo backgroundRepo;
    @Autowired
    private SettingRepo settingRepo;

    /**
     * 获取所有可用字体资源
     * @return 字体资源列表
     */
    @Override
    public List<FontResource> getAvailableFonts() {
        return fontRepository.findAll();
    }

    /**
     * 获取用户的阅读设置
     * @param user
     * @return 用户的阅读设置 DTO
     */
    @Override
    public ReadingSettingDTO getUserSettings(Users user) {
        Users confirmUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("user not existed"));
        ReadingSetting setting = settingRepo.findByUser(confirmUser)
                .orElseThrow(() -> new IllegalArgumentException("setting not existed"));

        ReadingSettingDTO dto = new ReadingSettingDTO();
        dto.setFontFamily(setting.getFontResource().getFontName());
        dto.setFontSize(setting.getFontSize());
        dto.setBackgroundType(setting.getBackgroundType());
        dto.setSolidColor(setting.getBackgroundType() == BackgroundType.SOLID_COLOR ? setting.getSolidColor() : null);
        dto.setBackgroundId(setting.getBackgroundResource() != null ? setting.getBackgroundResource().getBackgroundId() : null);
        dto.setLineSpacing(setting.getLineSpacing());
        dto.setThemeMode(setting.getThemeMode());
        dto.setUpdateTime(setting.getUpdateTime());
        return dto;
    }
    /**
     * 更新用户阅读设置
     * @param userId
     * @param settingDTO 阅读设置 DTO
     */
    @Override
    public void updateUserSetting(Integer userId, ReadingSettingDTO settingDTO) {
        ReadingSetting setting = settingRepo.findByUser(userRepository.findById(userId).get()).
                orElseGet(() -> {
                    ReadingSetting newSetting = new ReadingSetting();
                    newSetting.setUser(userRepository.findById(userId).get());
                    return newSetting;
                });

        // 设置背景
        BackgroundType backgroundType = settingDTO.getBackgroundType();
        if (backgroundType == BackgroundType.SOLID_COLOR) {
            setting.setSolidColor(settingDTO.getSolidColor());
            setting.setBackgroundResource(null);
        } else {
            BackgroundResource backgroundResource = backgroundRepo.findById(settingDTO.getBackgroundId())
                    .orElseThrow(() -> new IllegalArgumentException("background resource not existed"));
            setting.setBackgroundResource(backgroundResource);
            setting.setBackgroundType(backgroundResource.getResourceType());
            setting.setSolidColor(null);
        }

        setting.setFontResource(fontRepository.findByFontName(settingDTO.getFontFamily()));
        setting.setFontSize(settingDTO.getFontSize());
        setting.setLineSpacing(settingDTO.getLineSpacing());
        setting.setThemeMode(settingDTO.getThemeMode());
        setting.setUpdateTime(LocalDateTime.now());
        settingRepo.save(setting);
    }
    /**
     * 添加字体文件到系统
     * @param file 上传的字体文件
     */
    @Override
    public void addFont(MultipartFile file) {

        FontResource fontResource = new FontResource();
        try {
            byte[] bytes = file.getBytes();
            TTFParser parser = new TTFParser();
            InputStream ips = file.getInputStream();
            TrueTypeFont ttf = parser.parse(ips);

            String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/fonts/";

            String fileName = ttf.getName() + ".ttf";
            File fontFile = new File(ttf.getName());
            file.transferTo(fontFile);
            Path path = Paths.get(baseDir, fileName);
            Files.write(path, bytes);

            fontResource.setFontName(getFontName(ttf));
            fontResource.setStoragePath("/fonts/" + fileName);
            fontRepository.save(fontResource);

        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        }
    }
    /**
     * 获取字体名称
     * @param font
     * @return 字体名称
     */
    public static String getFontName(TrueTypeFont font) {
        try {
            NamingTable nameTable = font.getNaming();
            String fontFamily = null;

            for (NameRecord record : nameTable.getNameRecords()) {

                log.info(NameRecord.NAME_FULL_FONT_NAME + "   " + NameRecord.NAME_FONT_FAMILY_NAME);

                if (record.getNameId() == NameRecord.NAME_FONT_FAMILY_NAME) { // Name ID 1 = Font Family Name
                    fontFamily = record.getString();
                }
            }
            return fontFamily != null ? fontFamily : "UnknownFont";
        } catch (Exception e) {
            e.printStackTrace();
            return "UnknownFont";
        }
    }
}
