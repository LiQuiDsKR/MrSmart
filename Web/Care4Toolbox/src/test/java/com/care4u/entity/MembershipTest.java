package com.care4u.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class MembershipTest {

    @Autowired
    MembershipRepository repository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    //@WithMockUser(username = "gildong", roles = "USER")
    public void auditingTest(){
        Membership newItem = new Membership();
        repository.save(newItem);

        em.flush();
        em.clear();

        Membership item = (repository.findByCode(newItem.getCode()));
        if (item == null) {
        	throw new EntityNotFoundException();
        }

        System.out.println("register time : " + item.getRegTime());
        System.out.println("update time : " + item.getUpdateTime());
        System.out.println("create member : " + item.getCreatedBy());
        System.out.println("modify member : " + item.getModifiedBy());
    }

}