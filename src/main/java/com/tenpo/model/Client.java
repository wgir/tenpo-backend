package com.tenpo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_seq")
    @SequenceGenerator(name = "clients_seq", sequenceName = "clients_client_id_seq", allocationSize = 1)
    @Column(name = "client_id")
    private Integer id;

    @Column(name = "client_name", nullable = false)
    private String name;

    @Column(name = "client_rut", nullable = false, unique = true)
    private String rut;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees;
}
