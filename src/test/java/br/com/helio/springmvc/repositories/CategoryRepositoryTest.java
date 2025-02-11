package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.entities.Beer;
import br.com.helio.springmvc.entities.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;

    @BeforeEach
    void setup() {
        testBeer = beerRepository.findAll().getFirst();
    }

    @Test
    @Transactional
    void testAddCategory() {
        Category savedCategory = categoryRepository.save(
            Category.builder()
                .description("beer")
            .build()
        );

        testBeer.addCategory(savedCategory);
        Beer savedBeer = beerRepository.save(testBeer);

        System.out.println(savedBeer.getBeerName());
    }
}