package com.desafio.votacao.scheduler;

import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;
import com.desafio.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessaoScheduler {

    private final PautaRepository pautaRepository;

    /**
     * Verifica a cada 1 minuto se existem pautas com sessões expiradas que ainda não foram encerradas.
     * O delay fixo de 60000ms garante que a tarefa rode sequencialmente.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void fecharSessoesExpiradas() {
        log.debug("Iniciando verificação de sessões expiradas...");
        
        OffsetDateTime agora = OffsetDateTime.now();
        List<Pauta> pautasExpiradas = pautaRepository.findByStatusAndDataFechamentoBefore(
                StatusPauta.EM_VOTACAO, agora);

        if (pautasExpiradas.isEmpty()) {
            return;
        }

        log.info("Encontradas {} pautas expiradas para fechamento automático.", pautasExpiradas.size());

        for (Pauta pauta : pautasExpiradas) {
            try {
                pauta.setStatus(StatusPauta.ENCERRADA);
                pautaRepository.save(pauta);
                log.info("Pauta {} encerrada automaticamente pelo Scheduler.", pauta.getId());
            } catch (Exception e) {
                log.error("Erro ao encerrar pauta {} automaticamente: {}", pauta.getId(), e.getMessage());
            }
        }
    }
}
