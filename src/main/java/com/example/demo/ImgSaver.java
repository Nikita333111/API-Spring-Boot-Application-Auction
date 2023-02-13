package com.example.demo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class ImgSaver {

    private static Logger inputErrorsLog = LogManager.getLogger("InputErrorsFile");
    public static MultipartFile img;
    public ImgSaver(MultipartFile multipartFile){
        img = multipartFile;
    }

    public String saveImg() {
        if (!ImgSaver.img.isEmpty()) {
            String pathToUpload = "C:/Users/Никита/Desktop/skillboxHW/demo/target/classes/static/images/";//getClass().getComponentType().getResource("/static/images").getPath();
            try {
                if (!new File(pathToUpload).exists()) {
                    new File(pathToUpload).mkdir();
                }
                System.out.println(pathToUpload);
                String orgName = img.getOriginalFilename();
                String filePath = pathToUpload + orgName;
                File dest = new File(filePath);
                img.transferTo(dest);
                return orgName;
            }catch (IOException e){
                inputErrorsLog.info(e.getMessage());
            }

        }
        return null;
    }
}
