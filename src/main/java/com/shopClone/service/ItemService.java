package com.shopClone.service;

import com.shopClone.dto.ItemFormDto;
import com.shopClone.entity.Item;
import com.shopClone.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;


//     private final ItemImgService itemImgService;
//
//     private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        // 이미지 등록

//        for(int i=0;)

        return item.getId();
    }

}
