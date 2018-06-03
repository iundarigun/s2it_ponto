package br.com.devcave.s2it.ponto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne
    private Day day;

}
