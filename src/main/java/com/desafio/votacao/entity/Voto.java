package com.desafio.votacao.entity;

import com.desafio.votacao.entity.enums.VotoEscolha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voto", uniqueConstraints = {
        @UniqueConstraint(name = "voto_pauta_id_associado_id_key", columnNames = {"pauta_id", "associado_id"})
}, indexes = {
        @Index(name = "idx_voto_pauta_id", columnList = "pauta_id")
})
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private VotoEscolha voto;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto;

    @PrePersist
    protected void onCreate() {
        if (dataVoto == null) {
            dataVoto = LocalDateTime.now();
        }
    }
}
