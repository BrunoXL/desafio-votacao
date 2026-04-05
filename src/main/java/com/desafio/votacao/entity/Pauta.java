package com.desafio.votacao.entity;

import com.desafio.votacao.entity.enums.StatusPauta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pauta", indexes = {
        @Index(name = "idx_pauta_status", columnList = "status")
})
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPauta status = StatusPauta.CRIADA;

    @Column(name = "data_abertura")
    private OffsetDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private OffsetDateTime dataFechamento;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private OffsetDateTime criadaEm;

    @OneToMany(mappedBy = "pauta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (criadaEm == null) {
            criadaEm = OffsetDateTime.now();
        }
    }
}
