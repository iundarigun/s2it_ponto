package br.com.devcave.s2it.ponto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayDTO {
    private String date;

    private String name;

    private String link;

    private String type;

    private String description;

    @JsonProperty("type_code")
    private String typeCode;
}
