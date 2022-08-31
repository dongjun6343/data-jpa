package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

// Java의 Proxy 기술로 가짜 클래스를 만든 후 주입을 해줘서 구현체가 없어도 사용할 수 있다.
// memberRepository.getClass() --> class com.sun.proxy.$ProxyXXX
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메소드이름으로 쿼리생성 -> 쿼리 메소드 기능
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 메소드이름으로 쿼리생성 단점 : 파라미터가 길어지면 메서드명이 너무 길어짐.
    // 리포지토리 메소드에 쿼리 정의하기(실무에서 많이 쓰임.)
    
    // 정적쿼리. (리포지토리 메소드에 쿼리 정의)
    // 동적쿼리. (QueryDSL)
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String usernamem, @Param("age") int age);

    // m.username이 String이므로. List<String>
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // Dto 조회.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


}
