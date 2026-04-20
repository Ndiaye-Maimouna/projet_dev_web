package com.brt.fleetmanagementservice.entity;

import com.brt.fleetmanagementservice.enums.ConducteurStatut;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "conducteurs")
public class Conducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private String userId;

    private String nom;

    private String prenom;

    @Column(unique = true, nullable = false)
    private String numeroPermis;

    private String telephone;

    private String email;

    private LocalDate dateEmbauche;

    @Enumerated(EnumType.STRING)
    private ConducteurStatut statut;

    @OneToMany(mappedBy = "conducteur", cascade = CascadeType.ALL)
    private List<Affectation> affectations;
}