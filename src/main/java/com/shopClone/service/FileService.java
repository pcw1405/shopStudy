package com.shopClone.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath,String originalFileName, byte[] fileData) throws Exception{
        UUID uuid=UUID.randomUUID(); // 고유한 식별자를 생성한다
        String extension =originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName =uuid.toString()+extension; // 새로운 랜덤한 파일명
        String fileUploadFullUrl=uploadPath + "/" + savedFileName;
        FileOutputStream fos=new FileOutputStream(fileUploadFullUrl);
        //FileOutputStream 바이트단위로 데이터를 쓰기 위해 사용되는 클래스다 데이터 복사와 유사하다

        fos.write(fileData);
        fos.close();
        return savedFileName;
        // fileData 바이트 배열로 기록

    }
}
