package com.cos.chatapp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RequiredArgsConstructor //final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RestController //데이터 리턴 서버, Restuful 웹서비스의 컨트롤러
//Json 형태로 객체 데이터를 반환, 최근에 데이터를 응답으로 제공하는 Restful API를 개발할 때 주로 사용
public class ChatController {
    
    private final ChatRepository chatRepository;

    //귓속말 시 사용
    @CrossOrigin
    @GetMapping(value = "/sender/{sender}/receiver/{receiver}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMsg(@PathVariable String sender, @PathVariable String receiver){
        return chatRepository.mFindBySender(sender,receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // 일단 채팅 내역 모두 받아옴
    @CrossOrigin
    @GetMapping(value = "/chat/roomNum/{roomNum}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Integer roomNum){
        return chatRepository.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }

    //Data 1건 return : Mono
    //Data 여러건 return : Flux
    @CrossOrigin
    @PostMapping("/chat")
    public Mono<Chat> setMsg(@RequestBody Chat chat){
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat);//Mono 타입으로 리턴
        // 아래와 같이 해도 상관없지만, 데이터가 잘 들어갔는지 확인 위해 return
        // Object 를리턴하면 자동으로 JSON으로 변환 (Message Converter)
    }
    /*@PostMapping("/chat")
    public void setMsg(@RequestBody Chat chat){
        chat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }*/
}
