package com.boostmytool.beststore.services;

import com.boostmytool.beststore.models.product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productRepository extends JpaRepository<product,Integer> {
}
