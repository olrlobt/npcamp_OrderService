package com.nbcamp.orderservice.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.dto.CategoryRequest;
import com.nbcamp.orderservice.domain.category.dto.CategoryResponse;
import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.category.repository.CategoryJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryJpaRepository categoryJpaRepository;

	@Transactional
	public CategoryResponse createCategory(CategoryRequest request) {
		checkCategoryDuplicate(request.category());
		Category category = Category.create(request);
		categoryJpaRepository.save(category);
		return new CategoryResponse(category.getId(), category.getCategory());
	}

	@Transactional(readOnly = true)
	public CategoryResponse getCategory(UUID categoryId) {
		Category category = findById(categoryId);
		return new CategoryResponse(category.getId(), category.getCategory());
	}

	@Transactional(readOnly = true)
	public List<CategoryResponse> getAllCategory() {
		List<Category> categories = categoryJpaRepository.findAllByOrderByCategoryAsc();
		return categories.stream()
			.map(category -> new CategoryResponse(category.getId(), category.getCategory()))
			.collect(Collectors.toList());
	}

	@Transactional
	public CategoryResponse updateCategory(UUID categoryId, CategoryRequest request) {
		checkCategoryDuplicate(request.category());
		Category category = findById(categoryId);
		category.update(request);
		categoryJpaRepository.save(category);
		return new CategoryResponse(category.getId(), category.getCategory());
	}

	@Transactional
	public void deleteCategory(UUID categoryId, User user) {
		Category category = findById(categoryId);
		category.delete(user.getId());
	}

	public void checkCategoryDuplicate(String category) {
		if (categoryJpaRepository.findByCategory(category).isPresent()) {
			throw new IllegalArgumentException(ErrorCode.EXIST_CATEGORY.getMessage());
		}
	}

	public Category findById(UUID categoryId) {
		return categoryJpaRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage()));
	}


}

