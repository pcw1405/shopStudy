package com.shopClone.repository;

import com.shopClone.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>,
    QuerydslPredicateExecutor<Item>, ItemRepositoryCustom{
        // querydsl은 동적인 쿼리 실행이다
        // 동적쿼리를 실행하고 해당하는 Item객체를 반환

        List<Item> findByItemNm(String itemNm);

//        List<Item>

        List<Item> findByItemNmOrItemDetail(String itemNm,String itemDetail);

//        @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
//        List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);


}
