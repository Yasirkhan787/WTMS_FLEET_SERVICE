package com.yasirkhan.fleet.models.entities;

import com.yasirkhan.fleet.models.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "WTMS_TEHSIL")
@Data
public class Tehsil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tehsilId;

    @Column(nullable = false, unique = true)
    private String tehsilName;

    @Enumerated(EnumType.STRING)
    private Status status;

    // One Tehsil has multiple yards (Usually 1 TCP and 1 Dump Site)
    @OneToMany(mappedBy = "tehsil", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Yard> yards;
}