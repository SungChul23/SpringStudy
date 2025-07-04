package com.kim.SpringStudy.controller;

import com.kim.SpringStudy.domain.Item;
import com.kim.SpringStudy.domain.ItemRepository;
import com.kim.SpringStudy.service.ItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor //롬복 문법

//inal이나 @NonNull이 붙은 필드들만 포함한 생성자를 자동으로 생성
public class ItemController {
    //오브젝트 뽑아서  넣어주세요.(레포지, 서비스)
    private final ItemRepository itemRepository;
    private final ItemService itemService; //서비스 폴더내 saveItem함수를 사용하기 위한 변수 설정

//    @Autowired (롬복 미사용 시)
//    public ItemController(ItemRepository itemRepository, ItemService itemService) {
//        this.itemRepository = itemRepository;
//        this.itemService = itemService;
//    }

    @GetMapping("/list")
    String list(Model model) {
        List<Item> result = itemService.findItem(); // 디펜던시 인잭션으로 findItem 함수 가져옴
        model.addAttribute("items", result);

        return "list";
    }

    @GetMapping("/list/product/{id}")
    String product(@PathVariable long id, Model model) {

        try {
            Optional<Item> product = itemRepository.findById(id);
            if (product.isPresent()) { //값이 있나요?
                model.addAttribute("product", product.get()); //optional 타입
                return "product";
            } else {
                return "redirect:/list";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/list";
        }

    }

    @GetMapping("/write")
    String write() {
        return "write.html";
    }

    @PostMapping("/add")
    String addpost(String title, Integer price) {
        itemService.saveItem(title, price); //오브젝 뽑아쓴걸 사용 = 디펜던시 인젝션 패턴
        return "redirect:/list";
    }

    @GetMapping("/edit/{id}")
    String edit(@PathVariable Long id, Model model) {
        Optional<Item> result = itemRepository.findById(id); // 디펜던시 인잭션으로 findItem 함수 가져옴
        if (result.isPresent()) {
            model.addAttribute("items", result.get());
            return "edit";
        }
        return "redirect:/list";
    }

    @PostMapping("/edit")
    public String editpost(@RequestParam Long id, @RequestParam String title, @RequestParam Integer price) {
        itemService.editItem(id, title, price);
        return "redirect:/list";
    }

    @GetMapping("/delete/{id}")
    String delete(@PathVariable Long id, Model model) {
        Optional<Item> result = itemRepository.findById(id); // 디펜던시 인잭션으로 findItem 함수 가져옴
        if (result.isPresent()) {
            model.addAttribute("items", result.get());
            return "delete";
        }
        return "redirect:/list";
    }

    @PostMapping("/delete")
    public String deletepost(@RequestParam Long id) {
        itemService.deleteItem(id);
        return "redirect:/list";
    }
}


//1. 레포지 만들기 2. 원하는 클래스에 레포지 등록, 3.레포지.입출력분법 작성
//1. 레포지에서 인터페이스 생성후 테이블명, 기본키 타입작성
//2. 아이템 컨트롤러 들어가서 레포지에 대한 변수 설정 (private 변수명)
//2.1 @RequiredArgsConstructor 사용 (롬복 사용) -> 그렇지 않으면 @AutoWried로 주입해야함
//3. 아이템 컨트롤러에 생성한 레포지 변수를 통하여 함수 사용 ex) findall(), get.(0).title ~ 등등

// --- ajax ---
//form 말고 ajax로 데이터전송하면 @RequestBody를 사용
// 클라이언트와 통신시 서버 에러 코드도 보내주기()
//return ResponseEntity.status(Httpstatus.NOT_FOUND).body()