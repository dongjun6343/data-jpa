package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("dongjun");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();
        
        // 검증
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);

    }
    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 다건조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();

        Assertions.assertThat(deleteCount).isEqualTo(0);
    }
    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto: memberDto) {
            System.out.println("dto = " + dto);
        }

    }
    @Test
    public void findMembers(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));

        for (Member member: result) {
            System.out.println("member = " + result);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        //만약 이름이 Unique하다면 리스트로 받을 필요없다
        //Member findMember = memberRepository.findMemberByUsername("AAA");
        // spring data jpa에서 null이면 try-catch문을 (NoResultException) 저절로 감싸줌. (논쟁거리)
        // 하지만 java8부터는 Optional을 사용하면 된다.
    }

    @Test
    public void paging(){
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        //스프링데이터JPA는 페이지를 1부터 시작하는게 아니라 0부터 시작한다!
        // Sort 조건을 username에서 DESC할거다!
        // ==> Sort.by(Sort.Direction.DESC,"username"
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // API는 dto로 변환해서 사용해야함. why? entity를 반환했을때 api스펙이 달라지면 어떻게 할거냐?!
        // map()을 사용하면 쉽게 변환할 수 있다.
        // (중요!!)
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));


        // 반환타입이 Page면 알아서 totalCount 쿼리를 날린다.
        // long totalCount = memberRepository.totalCount(age);

        // then (검증)
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();// totalCount와 동일.

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        // page번호를 가져온다.
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        // 전체페이지 개수
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        // 벌크성쿼리를 날리면 DB는 변경되지만 영속성컨텍스트는 모른다.
        // ==> member5의 나이는 그대로 40임.
        // 따라서 영속성컨텍스트를 날려준다.
//        em.flush();
//        em.clear();
        // 또는 @Modifying(clearAutomatically = true) 사용.


        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();

        //when N + 1 ==> 패치조인으로 해결한다.
        //select Member!
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        // 원래는 변경감지가 되어서 update쿼리가 나가야하지만,
        // 변경감지 체크를 하지 않는다.

        em.flush();
        //then
    }

    @Test
    public void lock(){
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }
}