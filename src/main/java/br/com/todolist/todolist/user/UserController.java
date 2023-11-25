package br.com.todolist.todolist.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody UserModel userModel) {

        Optional<UserModel> user = this.userRepository.findByUsername(userModel.getUsername());

        if(user.isEmpty()) {

            String encryptedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
            userModel.setPassword(encryptedPassword);
            this.userRepository.save(userModel);
            return ResponseEntity.ok().body("Usuário criado com sucesso");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existente");
    }
}
