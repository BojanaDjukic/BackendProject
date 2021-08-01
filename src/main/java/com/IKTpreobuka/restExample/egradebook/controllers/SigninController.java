package com.IKTpreobuka.restExample.egradebook.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.IKTpreobuka.restExample.egradebook.entities.UserEntity;
import com.IKTpreobuka.restExample.egradebook.entities.DTO.SigninDTO;
import com.IKTpreobuka.restExample.egradebook.repositories.UserRepository;
import com.IKTpreobuka.restExample.egradebook.utils.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@RestController
@RequestMapping(path= "/egradebook/user/login")
public class SigninController {

	@Autowired
	private UserRepository userRepository;
	
	@Value("${spring.security.secret-key}")
	private String secretKey;
	
	@Value("${spring.security.token-duration}")
	private Integer tokenDuration;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

   /* @Secured("ADMIN")
	@RequestMapping( method = RequestMethod.GET)
	public ResponseEntity<?> listUsers() {
		return new ResponseEntity<List<UserEntity>>((List<UserEntity>) userRepository.findAll(), HttpStatus.OK);
	}*/
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
		UserEntity userEntity = userRepository.findByUsername(username);
		if (userEntity != null && Encryption.validatePassword(password, userEntity.getPassword())) {
			String token = getJWTToken(userEntity);
			SigninDTO user = new SigninDTO();
			user.setUsername(username);
			user.setToken(token);
			
			logger.info(user.getUsername() + " : logged in.");
			return new ResponseEntity<>(user, HttpStatus.OK);
			
		}
		return new ResponseEntity<>("Wrong credentials", HttpStatus.UNAUTHORIZED);
	}
	//@RequestMapping(method = RequestMethod.GET, value= "/token")
	private String getJWTToken(UserEntity user) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().toString());
		String token = Jwts.builder().setId("softtekJWT").setSubject(user.getUsername())
		.claim("authorities", grantedAuthorities.stream()
		.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
		.setIssuedAt(new Date(System.currentTimeMillis()))
		.setExpiration(new Date(System.currentTimeMillis() + tokenDuration))
		.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();
		
		logger.info(user.toString() + " : JWTToken granted.");
		return "Bearer " + token;
		}
	
	

}
