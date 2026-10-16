
package com.smartcarbo.model.repository;

import com.smartcarbo.model.entity.RegistroAgua;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroAguaRepository
        extends JpaRepository<RegistroAgua, Long> {

    List<RegistroAgua> findAllByUsuarioIdOrderByDataHoraDesc(
            Long usuarioId);

    List<RegistroAgua> findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim);

    List<RegistroAgua> findAllByUsuarioIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim);

    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
