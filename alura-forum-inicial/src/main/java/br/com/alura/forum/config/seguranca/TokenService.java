package br.com.alura.forum.config.seguranca;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	// @Value busca no arquivo application.properties a chave
	
	@Value("${forum.jwt.expiration}")
	private String expiration;
	
	@Value("${forum.jwt.secret}")
	private String secret;

	public String gerarToken(Authentication authentication) {
		Usuario logado = (Usuario) authentication.getPrincipal();
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));
		
		return Jwts.builder()
				.setIssuer("API do Forum da Alura") 	// Quem fez a geracao do Token
				.setSubject(logado.getId().toString()) 	// Usuario dono do token
				.setIssuedAt(hoje) 						// Data de criacao
				.setExpiration(dataExpiracao)			// Data expiracao
				.signWith(SignatureAlgorithm.HS256, secret) // Senha com criptografia
				.compact(); 
	}

	public boolean isTokenValido(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getUsuarioId(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		String idUsuario = claims.getSubject();
		return Long.parseLong(idUsuario);
	}

}
