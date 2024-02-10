package com.shopClone.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopClone.constant.ItemSellStatus;
import com.shopClone.dto.ItemSearchDto;
import com.shopClone.dto.MainItemDto;
import com.shopClone.entity.Item;
import com.shopClone.entity.QItem;
import com.shopClone.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory=new JPAQueryFactory(em);
    }
    // BooleanExpression 논리연산자

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }
    private BooleanExpression regDtsAfter(String searchDataType){
        LocalDateTime dataTime = LocalDateTime.now();
        if(StringUtils.equals("all",searchDataType) || searchDataType ==null){
            return null;
        }else if(StringUtils.equals("1d",searchDataType)){
            dataTime=dataTime.minusDays(1);
        }else if(StringUtils.equals("1w",searchDataType)){
            dataTime=dataTime.minusWeeks(1);
        }else if(StringUtils.equals("1m",searchDataType)){
            dataTime=dataTime.minusMonths(1);
        }else if(StringUtils.equals("6m",searchDataType)){
            dataTime=dataTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dataTime);
    }

    private BooleanExpression searchByLike(String searchBy,String searchQuery){
        if (StringUtils.equals("itemNm",searchBy)){
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createBy",searchBy)){
            return QItem.item.createBy.like("%"+searchQuery+"%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        List<Item> content =queryFactory
                .select(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDataType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total =queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDataType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();


        return new PageImpl<>(content,pageable,total);
    }

//    @Override
//    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
//        QItem item =QItem.item;
//        QItemImg itemImg = QItemImg.itemImg;
//
////        List<MainItemDto> content = queryFactory
////                .select(
////                        new QMainItemDto(
////                                item.id,
////                                item.itemNm,
////                                itemImg.itemDetail,
////                                itemImg.imgUrl,
////                                item.price)
////
////
////
////                        )
////                )
//        return new PageImpl<>(content,pageable,total);
//    }
}
