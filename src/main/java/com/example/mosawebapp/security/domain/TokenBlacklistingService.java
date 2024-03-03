package com.example.mosawebapp.security.domain;

import com.example.mosawebapp.exceptions.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistingService {
  private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistingService.class);

  @Autowired
  private TokenBlacklistRepository tokenBlacklistRepository;

  public void addTokenToBlacklist(String token){
    logger.info("adding {} to blacklist", token);

    TokenBlacklist tokenBlacklist = tokenBlacklistRepository.findByToken(token);

    if(tokenBlacklist != null){
      throw new SecurityException("Token can no longer be used");
    } else {
      if(tokenBlacklistRepository.tokensAreOverThirty()){
        tokenBlacklistRepository.deleteAll();
      }

      tokenBlacklistRepository.save(new TokenBlacklist(token));
    }
  }

  public boolean isTokenBlacklisted(String token) {
    TokenBlacklist status = tokenBlacklistRepository.findByToken(token);

    return status != null;
  }
}
