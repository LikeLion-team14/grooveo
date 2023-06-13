package com.kl.grooveo.boundedContext.library.service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.repository.FileInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    public FileInfoService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    public FileInfo saveFileInfo(FileInfo fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }

    public FileInfo findById(Long id) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(id);
        return optionalFileInfo.orElse(null);
    }

    public FileInfo getFileInfo(Long id) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(id);
        return optionalFileInfo.get();
    }

    public void modify(FileInfo fileInfo, String description) {
        fileInfo.setDescription(description);
        fileInfo.setModifyDate(LocalDateTime.now());
        this.fileInfoRepository.save(fileInfo);
    }

    public void delete(Long id) {
        FileInfo fileInfo = getFileInfo(id);
        fileInfoRepository.delete(fileInfo);
    }
}