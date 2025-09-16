package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {

    Optional<Card> findCardById(UUID id);

    boolean existsByIdAndOwnerId(UUID cardId,
                                 UUID ownerId);

    Page<Card> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Card> findAll(Pageable pageable);

    boolean existsByCardNumberHash(String cardNumberHash);

}

