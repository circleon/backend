package com.circleon.config.dataloader;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.entity.Category;
import com.circleon.domain.circle.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Order(2)
public class CategoryDataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        if(categoryRepository.count() == 0) {
            for(CategoryType categoryType : CategoryType.values()) {
                Category category = Category.builder()
                        .type(categoryType)
                        .build();

                categoryRepository.save(category);
            }
        }
    }
}
