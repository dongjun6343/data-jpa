package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

// Java의 Proxy 기술로 가짜 클래스를 만든 후 주입을 해줘서 구현체가 없어도 사용할 수 있다.
// memberRepository.getClass() --> class com.sun.proxy.$ProxyXXX
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 스프링 데이터 JPA는 반환타입을 유연하게 쓸수있도록 지원.
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional
    
    // 성능이 안나올경우 (전체 count 쿼리는 매우 무겁다)
    // count쿼리가 있으면 성능이 안좋아질 수 있어서 분리해서 사용한다.(실무)
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)  // @Modifying : executeUpdate
                                           // (clearAutomatically = true) : em.clear();
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // team 객체까지 생성(proxy가 아니라 진짜 객체)
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) // @EntityGraph == fetch join
    // 간단할때는 EntityGraph 사용
    // 복잡할때는 fetch join 사용.
    List<Member> findAll();

    // 조회용으로만 쓸거야 ==> JPA Hint (성능 최적화)
    // 하지만 생각보다 크게 차이가 안남. 성능 테스트를 해보고 진짜 필요하다라고 생각하면 그때 쓰자.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);


    // selec for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
