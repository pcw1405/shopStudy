package com.shopClone.service;

import com.shopClone.entity.ItemImg;
import com.shopClone.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}") // properties 에서 설정한 내용을 불러움
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    //MultipartFile - 스프링에서 제공하는 인터페이스 , 클라이언트에서 전송된 정보

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName="";
        String imgUrl="";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName= fileService.uploadFile(itemImgLocation,oriImgName,
                    itemImgFile.getBytes());
            imgUrl="/images/item/"+ imgName;
        }
        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId,MultipartFile itemImgFile) throws Exception{
            // 첨부파일 이미지의 정보를 가지고 있는 객체- 파일이름, 크기 ,내용
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);
            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation+
                        "/"+savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            String imgUrl ="/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);

        }

    }
}
