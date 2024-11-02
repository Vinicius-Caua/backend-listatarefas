package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tarefas")
public class ListaTarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nome;
    private String descricao;
    private boolean realizada;
    @NotNull
    private BigDecimal custo;
    @NotNull
    private LocalDateTime dataLimite;
    private int ordemApresentacao;

    public ListaTarefa(String nome, String descricao, boolean realizada, BigDecimal custo, LocalDateTime dataLimite) {
        this.nome = nome;
        this.descricao = descricao;
        this.realizada = realizada;
        this.custo = custo;
        this.dataLimite = dataLimite;
    }

    // Construtor sem parametros para funcionamento do JPA
    public ListaTarefa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isRealizada() {
        return realizada;
    }

    public void setRealizada(boolean realizada) {
        this.realizada = realizada;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    public LocalDateTime getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDateTime dataLimite) {
        this.dataLimite = dataLimite;
    }

    public int getOrdemApresentacao() {
        return ordemApresentacao;
    }

    public void setOrdemApresentacao(int ordemApresentacao) {
        this.ordemApresentacao = ordemApresentacao;
    }
}
