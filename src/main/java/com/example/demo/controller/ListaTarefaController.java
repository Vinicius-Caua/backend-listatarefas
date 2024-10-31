package com.example.demo.controller;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.service.ListaTarefaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class ListaTarefaController {
    private ListaTarefaService listaTarefaService;

    // Injection via Metodo construtor
    public ListaTarefaController(ListaTarefaService listaTarefaService) {
        this.listaTarefaService = listaTarefaService;
    }

    @PostMapping
    public List<ListaTarefa> create(@RequestBody ListaTarefa listaTarefa){
        return listaTarefaService.create(listaTarefa);
    }

    @GetMapping
    List<ListaTarefa> list(){
        return listaTarefaService.list();
    }

    @PutMapping
    List<ListaTarefa> update(@RequestBody ListaTarefa listaTarefa){
        return listaTarefaService.update(listaTarefa);
    }

    // Para deletar, necessario {id} da tarefa
    @DeleteMapping("{id}")
    List<ListaTarefa> delete(@PathVariable("id") Long id){
        return listaTarefaService.delete(id);
    }
}
