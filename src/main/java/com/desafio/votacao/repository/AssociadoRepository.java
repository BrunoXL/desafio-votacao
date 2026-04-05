package com.desafio.votacao.repository;

import com.desafio.votacao.entity.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssociadoRepository extends JpaRepository<Associado, UUID> {

    Optional<Associado> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
