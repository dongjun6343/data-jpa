package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    // ctrl+shift+T --> MemberJpaRepositoryTest 생성.
    // JPA는 엔터티를 변경할때 변경감지를 해서 자동으로 update 쿼리를 날린다.
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    // 다건조회.
    // JPQL
    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    // 단건조회.
    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count(){
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
        // Long.class -> 반환타입.
        // .getSingleResult(); : 결과를 하나만 반환.
    }
}
