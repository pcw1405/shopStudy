package com.shopClone.repository;

import com.shopClone.dto.ItemSearchDto;
import com.shopClone.dto.MainItemDto;
import com.shopClone.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepositoryCustom {

  Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

   Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);



}
