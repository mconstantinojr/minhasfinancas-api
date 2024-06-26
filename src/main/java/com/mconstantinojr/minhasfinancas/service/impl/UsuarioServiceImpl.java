package com.mconstantinojr.minhasfinancas.service.impl;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mconstantinojr.minhasfinancas.exception.ErroAutenticacao;
import com.mconstantinojr.minhasfinancas.exception.RegraDeNegocioException;
import com.mconstantinojr.minhasfinancas.model.entity.Usuario;
import com.mconstantinojr.minhasfinancas.model.repository.UsuarioRepository;
import com.mconstantinojr.minhasfinancas.service.UsuarioService;

import jakarta.transaction.Transactional;


@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	
	@Autowired
	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}
	
	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe =  repository.existsByEmail(email);
		if (existe) {
			throw new RegraDeNegocioException("Já existe um usuário cadastrado com este email.");
		}
		
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {

		Optional<Usuario> retorno = repository.findById(id);
		
		return retorno;
	}

}
