package com.ttasum.memorial.domain.repository.flower;


import com.ttasum.memorial.domain.entity.flower.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerRepository extends JpaRepository<Flower, Integer> {

    List<Flower> findByEmotionAndDelFlag(String emotion, String delFlag);

    Page<Flower> findAll(Pageable pageable);

}