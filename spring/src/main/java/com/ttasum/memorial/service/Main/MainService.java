package com.ttasum.memorial.service.Main;

//import com.ttasum.memorial.domain.repository.Main.HeavenLetterMainRepository;
//import com.ttasum.memorial.domain.repository.Main.RememberanceMainRepository;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterRepository;
import com.ttasum.memorial.domain.repository.memorial.MemorialRepository;
import com.ttasum.memorial.dto.Main.HeavenLetterMainDto;
import com.ttasum.memorial.dto.Main.MainDto;
import com.ttasum.memorial.dto.Main.RememberanceMainDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final HeavenLetterRepository heavenLetterRepository;
    private final MemorialRepository memorialRepository;

    public MainDto getMainDto() {

        List<HeavenLetterMainDto> heavenLetterMainDtoList = heavenLetterRepository.findRecentHeavenLetters(PageRequest.of(0, 10));
        List<RememberanceMainDto> rememberanceMainDtoList = memorialRepository.findRecentRememberance(PageRequest.of(0, 5));
        return new MainDto(heavenLetterMainDtoList, rememberanceMainDtoList);
    }

}
