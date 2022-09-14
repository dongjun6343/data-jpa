package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;


    // http://localhost:8080/members?page=0&size=3&sort=id,desc&sort=username,desc
    // page , size , sort 제공.
    // @PageableDefault(size = 5) 설정. (디폴트설정 --> 글로벌설정)
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5) Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }


    // entity를 외부 api에 노출하면 안된다 (내부설계를 다 노출하는것) --> 항상 Dto로 반환해주자. (중요!!!)
    @GetMapping("/members/v1")
    public Page<MemberDto> listV1(@PageableDefault(size = 5) Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);

        //1.
        //Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //2.  dto에 member를 볼수있도록 설정한 후
        //Page<MemberDto> map = page.map(member -> new MemberDto(member));

        //3. 메서드 래퍼런스로 바꿀 수 있음. (alt+enter)
        Page<MemberDto> map = page.map(MemberDto::new);
        
        //4. 더 줄이면
        // return memberRepository.findAll(pageable).map(MemberDto::new);

        return map;
    }

    // TEST데이터 생성
    @PostConstruct
    public void init(){
        for (int i=0; i<100; i++){
            memberRepository.save(new Member("user"+ i, i));
        }
    }
}
