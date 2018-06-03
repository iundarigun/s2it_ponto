package br.com.devcave.s2it.ponto.mapper;

import br.com.devcave.s2it.ponto.domain.Holiday;
import br.com.devcave.s2it.ponto.dto.HolidayDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HolidayMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "date", source = "date", dateFormat = "dd/MM/yyyy"),
            @Mapping(target = "description", source = "name")
    })
    Holiday fromDTO(HolidayDTO holiday);

    default List<Holiday> fromDTOList(List<HolidayDTO> holidayDTOList) {
        return holidayDTOList.stream()
                .map(holidayDTO-> fromDTO(holidayDTO))
                .collect(Collectors.toList());
    }
}
