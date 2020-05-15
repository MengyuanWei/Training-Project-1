/*
 *  Copyright 2019, Liwei Wang <daveywang@live.com>.
 *  All rights reserved.
 *  Author: Liwei Wang
 *  Date: 06/2019
 */

package com.ascending.training.controller;

import com.ascending.training.service.FileService;
import com.ascending.training.service.MessageService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = {"/files"})
public class FileController {
    private static final String queueName = "training_queue_ascending_com";
    @Autowired
    private Logger logger;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //you can return either s3 key or file url
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("test file name:"+file.getOriginalFilename());
        try {
           return fileService.uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName, HttpServletRequest request) {
//        request.getSession()
        Resource resource = null;
        String msg = "The file doesn't exist.";
        ResponseEntity responseEntity;

        try {
            String url = fileService.getFileUrl(fileName);
            logger.debug(msg);
            responseEntity = ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(url);
        }
        catch (Exception ex) {
            responseEntity = ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(ex.getMessage());
            logger.debug(ex.getMessage());
        }

        return responseEntity;
    }


//    @RequestMapping(value = "/{bucketName}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity uploadFile(@PathVariable String bucketName, @RequestParam("file") MultipartFile file) {
//        String msg = String.format("The file name=%s, size=%d could not be uploaded.", file.getOriginalFilename(), file.getSize());
//        ResponseEntity responseEntity = ResponseEntity.status(HttpServletResponse.SC_NOT_ACCEPTABLE).body(msg);
//        try {
//            String url = fileService.uploadFile(file);
//            if (url != null) {
//                msg = String.format("The file name=%s, size=%d was uploaded, url=%s", file.getOriginalFilename(), file.getSize(), url);
//                messageService.sendMessage(queueName, url);
//                responseEntity = ResponseEntity.status(HttpServletResponse.SC_OK).body(msg);
//            }
//            logger.info(msg);
//        }
//        catch (Exception e) {
//            responseEntity = ResponseEntity.status(HttpServletResponse.SC_NOT_ACCEPTABLE).body(e.getMessage());
//            logger.error(e.getMessage());
//        }
//
//        return responseEntity;
//    }

//    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
//        Resource resource = null;
//        String msg = "The file doesn't exist.";
//        ResponseEntity responseEntity;
//
//        try {
//            Path filePath = Paths.get(fileDownloadDir).toAbsolutePath().resolve(fileName).normalize();
//            resource = new UrlResource(filePath.toUri());
//            if(!resource.exists()) return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(msg);
//            responseEntity = ResponseEntity.ok()
//                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                             .body(resource);;
//
//            msg = String.format("The file %s was downloaded", resource.getFilename());
//            //Send message to SQS
//            messageService.sendMessage(queueName, msg);
//            logger.debug(msg);
//        }
//        catch (Exception ex) {
//            responseEntity = ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(ex.getMessage());
//            logger.debug(ex.getMessage());
//        }
//
//        return responseEntity;
//    }
}
