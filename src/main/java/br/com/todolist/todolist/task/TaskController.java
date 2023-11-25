package br.com.todolist.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.todolist.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    
    @PostMapping("/create")
    public ResponseEntity<TaskModel> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId((UUID) request.getAttribute("userId"));
        LocalDateTime currenDateTime = LocalDateTime.now();
        if(taskModel.getEndAt().isBefore(currenDateTime)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().body(taskRepository.save(taskModel));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        List<TaskModel> resultados = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));
        
        if(resultados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok().body(resultados);
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<TaskModel> update(@PathVariable UUID taskId, @RequestBody TaskModel taskModel, HttpServletRequest request) {

        Optional<TaskModel> objeto = this.taskRepository.findById(taskId);

        if(objeto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        if(objeto.get().getUserId().equals((UUID) request.getAttribute("userId"))) {
            Utils.copyNonNullProperties(taskModel, objeto.get());
            return ResponseEntity.ok().body(this.taskRepository.save(objeto.get()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
