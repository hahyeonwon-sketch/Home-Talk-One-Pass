package com.hometalk.onepass.community.repository;

import com.hometalk.onepass.community.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByBoardId(Long boardId);
}
