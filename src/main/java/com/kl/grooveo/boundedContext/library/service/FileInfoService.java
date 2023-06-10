package com.kl.grooveo.boundedContext.library.service;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.repository.FileInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    public FileInfoService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    public FileInfo saveFileInfo(String title, String description, String fileUrl) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setTitle(title);
        fileInfo.setDescription(description);
        fileInfo.setFileUrl(fileUrl);

        return fileInfoRepository.save(fileInfo);
    }
}