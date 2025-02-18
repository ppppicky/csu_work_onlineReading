package org.example.service;

import org.example.dto.CoverTempDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CoverTempService {

    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException;
    public CoverTempDTO getTempCover(String tempKey);

    String moveToPermanent(String tempPath);

    public void deleteTempCover(String tempKey) ;
}
