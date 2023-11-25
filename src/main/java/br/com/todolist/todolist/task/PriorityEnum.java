package br.com.todolist.todolist.task;

import lombok.Getter;

@Getter
public enum PriorityEnum {
    
    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String description;

    PriorityEnum(String description) {
        this.description = description;
    }
}
