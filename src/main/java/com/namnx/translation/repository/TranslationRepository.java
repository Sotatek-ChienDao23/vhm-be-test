package com.namnx.translation.repository;

import com.namnx.translation.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
}
