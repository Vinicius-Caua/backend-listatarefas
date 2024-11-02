package com.example.demo.service;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.repository.ListaTarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ListaTarefaService {
    private ListaTarefaRepository listaTarefaRepository;

    // Injection via Metodo construtor
    public ListaTarefaService(ListaTarefaRepository listaTarefaRepository) {
        this.listaTarefaRepository = listaTarefaRepository;
    }

    public List<ListaTarefa> create(ListaTarefa listaTarefa) {
        // Verifica se o nome da tarefa já existe
        if (isNomeTarefaExistente(listaTarefa.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma tarefa com o nome '" + listaTarefa.getNome() + "' já existe.");
        }

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

    public List<ListaTarefa> update(ListaTarefa listaTarefa) {
        // Verifica se a tarefa existe
        if (!listaTarefaRepository.existsById(listaTarefa.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada.");
        }

        // Recupera a tarefa existente
        ListaTarefa tarefaExistente = listaTarefaRepository.findById(listaTarefa.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada."));

        // Verifica se a ordem de apresentacao esta sendo alterada
        if (tarefaExistente.getOrdemApresentacao() != listaTarefa.getOrdemApresentacao()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A ordem de apresentação não pode ser alterada.");
        }

        // Verifica se o nome da tarefa já existe, excluindo a tarefa atual
        if (!tarefaExistente.getNome().equalsIgnoreCase(listaTarefa.getNome()) &&
                isNomeTarefaExistente(listaTarefa.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma tarefa com o nome '" + listaTarefa.getNome() + "' já existe.");
        }

        // Mantém o ID e a ordem de apresentação da tarefa existente
        listaTarefa.setOrdemApresentacao(tarefaExistente.getOrdemApresentacao());
        listaTarefa.setId(tarefaExistente.getId()); // Garante que o ID não será alterado

        // Salva a tarefa atualizada
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

    private boolean isNomeTarefaExistente(String nome) {
        // Recupera todas as tarefas existentes e verifica se alguma delas tem o mesmo nome
        List<ListaTarefa> tarefas = listaTarefaRepository.findAll();
        return tarefas.stream().anyMatch(tarefa -> tarefa.getNome().equalsIgnoreCase(nome));
    }
}
