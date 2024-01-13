package com.shopClone.entity;

import com.shopClone.constant.ItemSellStatus;
import com.shopClone.dto.ItemFormDto;
import com.shopClone.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,length = 50)
    private String itemNm;

    @Column(name="price",nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm=itemFormDto.getItemNm();
        this.price=itemFormDto.getPrice();
        this.stockNumber=itemFormDto.getStockNumber();
        this.itemDetail=itemFormDto.getItemDetail();
        this.itemSellStatus=itemFormDto.getItemSellStatus();

    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0 ){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: ) ");
        }
        this.stockNumber =restStock;
    }

    public void addStock(int stockNumber) {
        this.stockNumber+= stockNumber;
    }


}
