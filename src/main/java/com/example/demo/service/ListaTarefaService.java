package com.example.demo.service;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.repository.ListaTarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaTarefaService {
    private ListaTarefaRepository listaTarefaRepository;

    // Injection via Metodo construtor
    public ListaTarefaService(ListaTarefaRepository listaTarefaRepository) {
        this.listaTarefaRepository = listaTarefaRepository;
    }

    public List<ListaTarefa> create(ListaTarefa listaTarefa){
        listaTarefa.setOrdemApresentacao(getNextOrdemApresentacao());
        listaTarefaRepository.save(listaTarefa);
        return list();
    }

    public List<ListaTarefa> list(){
        // Mecanismo de ordenação das tarefas
        Sort.by("ordemApresentacao").descending().
                and(Sort.by("nome").ascending());
        return listaTarefaRepository.findAll();
    }

    public List<ListaTarefa> update(ListaTarefa listaTarefa){
        listaTarefaRepository.save(listaTarefa);
        return list();
    }

    public List<ListaTarefa> delete(Long id){
        listaTarefaRepository.deleteById(id);
        reordenarOrdemApresentacao();
        return list();
    }

    private int getNextOrdemApresentacao() {
        return (int) listaTarefaRepository.count() + 1;
    }

    private void reordenarOrdemApresentacao() {
        List<ListaTarefa> tarefas = listaTarefaRepository.findAll(Sort.by("ordemApresentacao"));
        for (int i = 0; i < tarefas.size(); i++) {
            ListaTarefa tarefa = tarefas.get(i);
            tarefa.setOrdemApresentacao(i + 1);
            listaTarefaRepository.save(tarefa);
        }
    }
}
