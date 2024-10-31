package com.example.demo.service;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.repository.ListaTarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaTarefaService {
    private ListaTarefaRepository listaTarefaRepository;

    // Metodo construtor
    public ListaTarefaService(ListaTarefaRepository listaTarefaRepository) {
        this.listaTarefaRepository = listaTarefaRepository;
    }

    List<ListaTarefa> create(ListaTarefa listaTarefa){
        listaTarefaRepository.save(listaTarefa);
        return list();
    }

    List<ListaTarefa> list(){
        // Mecanismo de ordenação das tarefas
        Sort.by("ordemApresentacao").descending().
                and(Sort.by("nome").ascending());
        return listaTarefaRepository.findAll();
    }

    List<ListaTarefa> update(ListaTarefa listaTarefa){
        listaTarefaRepository.save(listaTarefa);
        return list();
    }

    List<ListaTarefa> delete(Long id){
        listaTarefaRepository.deleteById(id);
        return list();
    }
}
