package org.example.service;

import org.example.dto.CoverTempDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CoverTempService {

    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException;
    public CoverTempDTO getTempCover(String tempKey);
    public void saveTempCoverData(CoverTempDTO tempDTO, String tempCoverKey) throws IOException;
    String moveToPermanent(String tempPath);

    public void deleteTempCover(String tempKey) ;
}
