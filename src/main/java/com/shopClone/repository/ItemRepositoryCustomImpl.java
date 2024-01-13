package com.shopClone.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopClone.dto.ItemSearchDto;
import com.shopClone.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

//    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
//        List<Item> content =queryFactory
//                .select(regDtsAfter)
//
//        return temp;
//    }
}
