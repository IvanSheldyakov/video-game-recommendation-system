package nsu.sheldyakov.epicmatch.mapper;

import nsu.sheldyakov.epicmatch.domain.KeyWord;
import nsu.sheldyakov.epicmatch.domain.KeyWordCandidate;
import org.mapstruct.Mapper;

@Mapper
public interface KeyWordMapper {

  KeyWord map(KeyWordCandidate candidate);
}
