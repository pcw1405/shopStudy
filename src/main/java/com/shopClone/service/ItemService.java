package com.shopClone.service;

import com.shopClone.dto.ItemFormDto;
import com.shopClone.dto.ItemImgDto;
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

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList=itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        //itemId에 해당하는 item의 img를 불러운다 itemImgList를 가져온다
        List<ItemImgDto> itemImgDtoList =new ArrayList<>();
        for (ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        // itemImgList를 순회하면서 각각의 ItemImg 를 ItemImgDto 로 변환하여 itemImgDtoList 추가

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto,List<MultipartFile> itemImgFileList) throws Exception{
        // 상품수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
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
