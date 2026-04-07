package com.desafio.votacao.repository;

import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface PautaRepository extends JpaRepository<Pauta, UUID> {

    List<Pauta> findByStatus(StatusPauta status);

    List<Pauta> findByStatusAndDataFechamentoBefore(StatusPauta status, OffsetDateTime data);

}
