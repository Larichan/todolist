package br.com.todolist.todolist.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.todolist.todolist.user.UserModel;
import br.com.todolist.todolist.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                if(request.getServletPath().contains("/tasks/")) {

                    String authorization = request.getHeader("Authorization").substring("Basic".length()).trim();
    
                    String[] credentials = new String(Base64.getDecoder().decode(authorization)).split(":");
                    String username = credentials[0];
                    String password = credentials[1];
    
                    Optional<UserModel> user = userRepository.findByUsername(username);
    
                    if(user.isPresent()) {
                        Result passwordVerification = BCrypt.verifyer().verify(password.toCharArray(), user.get().getPassword());
                        if(passwordVerification.verified) {
                            request.setAttribute("userId", user.get().getId());
                            filterChain.doFilter(request, response);
                        } else {
                            response.sendError(401, "Senha inválida");
                        }
                    } else {
                        response.sendError(401, "Usuário não encontrado");
                    }
                } else {
                    filterChain.doFilter(request, response);
                }

    }
    
}
