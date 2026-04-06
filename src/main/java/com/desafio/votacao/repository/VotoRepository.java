package com.desafio.votacao.repository;

import com.desafio.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VotoRepository extends JpaRepository<Voto, UUID> {

    boolean existsByPautaIdAndAssociadoId(UUID pautaId, UUID associadoId);

    List<Voto> findAllByPautaId(UUID pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId")
    long countByPautaId(@Param("pautaId") UUID pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = com.desafio.votacao.entity.enums.VotoEscolha.SIM")
    long countSimByPautaId(@Param("pautaId") UUID pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = com.desafio.votacao.entity.enums.VotoEscolha.NAO")
    long countNaoByPautaId(@Param("pautaId") UUID pautaId);
}

