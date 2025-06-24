package com.ttasum.memorial.domain.repository.donationStory;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface DonationStoryRepositoryCustom {
    @Transactional(readOnly = true)
    Page<DonationStory> searchStories(
            String searchField,
            String keyword,
            Pageable pageable);
}
