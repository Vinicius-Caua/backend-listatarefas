package com.example.demo.service;

import com.example.demo.entity.ListaTarefa;
import com.example.demo.repository.ListaTarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ListaTarefaService {
    private ListaTarefaRepository listaTarefaRepository;

    // Injection via Metodo construtor
    public ListaTarefaService(ListaTarefaRepository listaTarefaRepository) {
        this.listaTarefaRepository = listaTarefaRepository;
    }

    public List<ListaTarefa> create(ListaTarefa listaTarefa) {
        // Validação do custo inferior a zero
        if (listaTarefa.getCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O custo da tarefa não pode ser inferior a zero.");
        }

        // Verifica se o nome da tarefa já existe
        if (isNomeTarefaExistente(listaTarefa.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma tarefa com o nome '" + listaTarefa.getNome() + "' já existe.");
        }

        listaTarefa.setOrdemApresentacao(getNextOrdemApresentacao());
        listaTarefaRepository.save(listaTarefa);
        return list();
    }

    public List<ListaTarefa> list() {
        // Mecanismo de ordenação das tarefas
        return listaTarefaRepository.findAll(Sort.by("ordemApresentacao").ascending());
    }


    public List<ListaTarefa> update(ListaTarefa listaTarefa) {
        // Validação do custo inferior a zero
        if (listaTarefa.getCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O custo da tarefa não pode ser inferior a zero.");
        }

        // Verifica se a tarefa existe
        if (!listaTarefaRepository.existsById(listaTarefa.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada.");
        }

        // Recupera a tarefa existente
        ListaTarefa tarefaExistente = listaTarefaRepository.findById(listaTarefa.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada."));


        // Verifica se o nome da tarefa já existe, excluindo a tarefa atual
        if (!tarefaExistente.getNome().equalsIgnoreCase(listaTarefa.getNome()) &&
                isNomeTarefaExistente(listaTarefa.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uma tarefa com o nome '" + listaTarefa.getNome() + "' já existe.");
        }

        // Garante que o ID não será alterado
        listaTarefa.setId(tarefaExistente.getId());

        // Salva a tarefa atualizada
        listaTarefaRepository.save(listaTarefa);
        reordenarOrdemApresentacao();
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
