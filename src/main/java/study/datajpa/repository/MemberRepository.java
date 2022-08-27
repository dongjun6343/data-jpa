package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

// Java의 Proxy 기술로 가짜 클래스를 만든 후 주입을 해줘서 구현체가 없어도 사용할 수 있다.
// memberRepository.getClass() --> class com.sun.proxy.$ProxyXXX
public interface MemberRepository extends JpaRepository<Member, Long> {

}
