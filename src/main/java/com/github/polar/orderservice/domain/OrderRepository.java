package com.github.polar.orderservice.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {}
