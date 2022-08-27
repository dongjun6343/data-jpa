package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 객체를 찍을때 출력.
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    // 멤버랑 팀은 다대일 관계
    // ManyTo는 fetch = FetchType.LAZY로 바꿔주기! (지연로딩으로 세팅, 즉시로딩은 성능최적화하기 어렵다.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") //FK명
    private Team team;
// JPA에서는 protected로 기본생성자를 만들어줘야함. --> @NoArgsConstructor(access = AccessLevel.PROTECTED)로 밑에꺼 대신.
//    protected Member() {
//
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    //멤버가 팀을 변경한다.
    public void changeTeam(Team team){
        this.team = team;
        // 연관관계에 있는 team에 있는 member에도 세팅.
        // 객체이므로 관계가 있는 쪽에도 세팅해줘야함.
        team.getMembers().add(this);
    }
}
