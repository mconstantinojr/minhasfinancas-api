package com.mconstantinojr.minhasfinancas.service;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mconstantinojr.minhasfinancas.exception.RegraDeNegocioException;
import com.mconstantinojr.minhasfinancas.model.entity.Lancamento;
import com.mconstantinojr.minhasfinancas.model.entity.Usuario;
import com.mconstantinojr.minhasfinancas.model.enums.StatusLancamento;
import com.mconstantinojr.minhasfinancas.model.repository.LancamentoRepository;
import com.mconstantinojr.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.mconstantinojr.minhasfinancas.service.impl.LancamentoServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoSerivceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//exectutacao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraDeNegocioException.class).when(service).validar(lancamentoASalvar);;

		//execucao e verificacao
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraDeNegocioException.class); 
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//exectutacao
		Lancamento lancamento = service.atualizar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		//execucao e verificacao
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class); 
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveDeletarUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		//execucao
		service.deletar(lancamento);
		
		//verificacao
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
				
		//execucao
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
			
		//verificacao
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
				
	}

	@Test
	public void deveFiltrarLancamentos() {
		//cenarii
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execucai
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacoes
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizerOsStatusDeUmLancamento() {
		//cenarii
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//execucao
		service.atualizarStatus(lancamento, novoStatus);
		
		//verificaceos
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
				
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenarii
		Long id = 1L;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioQuandoLancamentoNaoExiste() {
		//cenarii
		Long id = 1L;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe uma Descrição válida.");

		lancamento.setDescricao("");
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe uma Descrição válida.");

		lancamento.setDescricao("salario");
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Mês válido.");
		
		lancamento.setMes(2);

		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Ano válido.");
		
		lancamento.setAno(20201);

		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Ano válido.");
		
		lancamento.setAno(202);

		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1L);
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);

		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um Valor válido.");

		lancamento.setValor(BigDecimal.ONE);
		
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraDeNegocioException.class).hasMessage("informe um tipo de Lançamento.");
		
		
	}

}
