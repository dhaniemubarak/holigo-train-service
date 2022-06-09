package id.holigo.services.holigotrainservice.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "stations")
public class Station {

    @Id
    @Column(length = 3, columnDefinition = "varchar(3)", nullable = false)
    private String id;

    @Column(length = 50, columnDefinition = "varchar(50)", nullable = false)
    private String name;

    @Column(length = 50, columnDefinition = "varchar(50)", nullable = false)
    private String city;
}
