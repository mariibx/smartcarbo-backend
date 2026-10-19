package com.smartcarbo.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcarbo.model.entity.SeguimentoUsuario;

@Repository
public interface SeguimentoUsuarioRepository
        extends JpaRepository<SeguimentoUsuario, Long> {

    boolean existsBySeguidorIdAndSeguidoId(Long seguidorId, Long seguidoId);

    List<SeguimentoUsuario> findAllBySeguidorId(Long seguidorId);

    void deleteBySeguidorIdAndSeguidoId(Long seguidorId, Long seguidoId);
}
