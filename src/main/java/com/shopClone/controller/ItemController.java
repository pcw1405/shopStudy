package com.shopClone.controller;

import com.shopClone.dto.ItemFormDto;
import com.shopClone.service.ItemService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String ItemForm(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){
        //valid는 유효성 검사를 위한 것이다. 예를 들어 숫자에 숫자 문자에 문자
        // valid는 폼데이터를 객체로 변활할 때 그 결과를 bindingResult에 저장
        //MultipartFile은 업로드된 파일의 메타데이터 및 내용을 다루기 위한 객체 (업로드 된 파일의 특징과 내용 )
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty()&& itemFormDto.getId() ==null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값 입니다. ");
            return "item/itemForm";
        }
        try{
            itemService.saveItem(itemFormDto,itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}") // 수정목록 조회
    public String itemDtl(@PathVariable("itemId") Long itemId,Model model){

        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto",itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }
}
