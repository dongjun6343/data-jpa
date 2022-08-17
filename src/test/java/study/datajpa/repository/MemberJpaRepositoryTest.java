package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

// jupiter : junit5
@SpringBootTest
@Transactional
@Rollback(false) // 등록쿼리가 보임. rollback을 저절로 해주므로 false로 해줌.
class MemberJpaRepositoryTest {
    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        // member 테스트
        // 생성자를 통해서 값을 넣는게 좋다. (setter 대신에!)
        Member member = new Member("dongjun");
        // 저장
        Member saveMember = memberJpaRepository.save(member);
        // 조회
        Member findMember = memberJpaRepository.find(saveMember.getId());

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        
        // 영속성 보장!! true
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}