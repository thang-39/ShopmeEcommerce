package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole() {
        //prepare
        Role roleAdmin = Role.builder()
                .name("Admin")
                .description("manage everything")
                .build();

        //act
        Role saveRole = repo.save(roleAdmin);

        //assert
        assertThat(saveRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = Role.builder()
                .name("Salesperson")
                .description("manage product price, customers, shipping, order and sales report")
                .build();

        Role roleEditor = Role.builder()
                .name("Editor")
                .description("manage categories, brands, products, articles and menus")
                .build();

        Role roleShipper = Role.builder()
                .name("Shipper")
                .description("view products, view orders and update order status")
                .build();

        Role roleAssistant = Role.builder()
                .name("Assistant")
                .description("manage questions and reviews")
                .build();

        repo.saveAll(List.of(roleSalesperson,roleEditor,roleShipper,roleAssistant));
    }
}
