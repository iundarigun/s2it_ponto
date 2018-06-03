package br.com.devcave.s2it.ponto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes= {@Index(name = "dayIndex01", columnList="month,user_id")})
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate day;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate month;

    @ManyToOne
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL)
    private List<Period> periodList = new ArrayList<>();

}
