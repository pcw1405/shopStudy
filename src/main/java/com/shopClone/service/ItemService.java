package com.shopClone.service;

import com.shopClone.dto.ItemFormDto;
import com.shopClone.dto.ItemSearchDto;
import com.shopClone.dto.MainItemDto;
import com.shopClone.entity.Item;
import com.shopClone.entity.ItemImg;
import com.shopClone.repository.ItemImgRepository;
import com.shopClone.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;


     private final ItemImgService itemImgService;
//
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        // 이미지 등록

        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg =new ItemImg();
            itemImg.setItem(item);

            if(i ==0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }
    // itemSearchDto와 pageabel를 매개변수로 받아서 itemRepository에서 Item 데이터를 조회하는 작업이다.
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }

}
