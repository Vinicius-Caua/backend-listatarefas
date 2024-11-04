package com.example.demo.controller;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.service.ListaTarefaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://litastarefas.netlify.app")
@RestController
@RequestMapping("/tarefas")
public class ListaTarefaController {
    private ListaTarefaService listaTarefaService;

    // Injection via Metodo construtor
    public ListaTarefaController(ListaTarefaService listaTarefaService) {
        this.listaTarefaService = listaTarefaService;
    }

    @PostMapping
    public List<ListaTarefa> create(@RequestBody @Valid ListaTarefa listaTarefa){
        return listaTarefaService.create(listaTarefa);
    }

    @GetMapping
    List<ListaTarefa> list(){
        return listaTarefaService.list();
    }

    @PutMapping("/{id}")
    List<ListaTarefa> update(@PathVariable Long id, @RequestBody @Valid ListaTarefa listaTarefa){
        // Lógica para garantir que o ID não pode ser alterado
        if (!id.equals(listaTarefa.getId())) {
            throw new IllegalArgumentException("Não é permitido alterar o ID da tarefa.");
        }
        return listaTarefaService.update(listaTarefa);
    }

    // Para deletar, necessario {id} da tarefa
    @DeleteMapping("{id}")
    List<ListaTarefa> delete(@PathVariable("id") Long id){
        return listaTarefaService.delete(id);
    }
}
