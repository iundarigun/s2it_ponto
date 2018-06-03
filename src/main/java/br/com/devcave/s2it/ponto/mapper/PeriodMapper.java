package br.com.devcave.s2it.ponto.mapper;

import br.com.devcave.s2it.ponto.domain.Holiday;
import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.dto.HolidayDTO;
import br.com.devcave.s2it.ponto.dto.PeriodDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PeriodMapper {

    @Mappings({
            @Mapping(target = "startTime", source = "startTime"),
            @Mapping(target = "endTime", source = "endTime")
    })
    PeriodDTO toDTO(Period period);

    default List<PeriodDTO> toDTOList(List<Period> periodList) {
        return periodList.stream()
                .map(period-> toDTO(period))
                .collect(Collectors.toList());
    }
}
