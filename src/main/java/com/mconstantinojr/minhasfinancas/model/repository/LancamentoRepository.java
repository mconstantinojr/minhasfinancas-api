package com.mconstantinojr.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mconstantinojr.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
