package com.example.demo;

import com.example.demo.entity.ListaTarefa;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class DesafioListatarefasApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testUpdateListaTarefaSuccess() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Criar uma tarefa válida
		var tarefaExistente = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaExistente)
				.exchange()
				.expectStatus().isOk();

		// Atualizar a tarefa, mantendo o mesmo ID e ordem de apresentação
		var tarefaAtualizada = new ListaTarefa("Pagar aluguel", "Atualizar descrição",
				false, new BigDecimal("200.00"), dataLimite);
		tarefaAtualizada.setId(1L); // Manter o mesmo ID
		tarefaAtualizada.setOrdemApresentacao(1); // Manter a ordem

		webTestClient
				.put()
				.uri("/tarefas/1")
				.bodyValue(tarefaAtualizada)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ListaTarefa.class)
				.consumeWith(response -> {
					var tarefas = response.getResponseBody();
					assert tarefas != null;
					assert tarefas.size() > 0;
					var tarefaRetornada = tarefas.get(0); // Pega a primeira tarefa da lista
					assert tarefaRetornada.getNome().equals(tarefaAtualizada.getNome());
					assert tarefaRetornada.getDescricao().equals(tarefaAtualizada.getDescricao());
					assert tarefaRetornada.getOrdemApresentacao() == 1;
				});
	}


	@Test
	void testUpdateListaTarefaIDAlterado() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Criar uma tarefa válida
		var tarefaExistente = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaExistente)
				.exchange()
				.expectStatus().isOk();

		// Tentar atualizar com um ID diferente
		var tarefaAtualizada = new ListaTarefa("Pagar aluguel", "Descrição diferente",
				false, new BigDecimal("200.00"), dataLimite);
		tarefaAtualizada.setId(2L); // Tentar alterar o ID

		webTestClient
				.put()
				.uri("/tarefas/1") // Mantenha a URI da tarefa existente
				.bodyValue(tarefaAtualizada)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.error").isEqualTo("Não é permitido alterar o ID da tarefa.");
	}

	@Test
	void testUpdateListaTarefaDuplicateName() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Criar uma tarefa válida
		var tarefaExistente = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaExistente)
				.exchange()
				.expectStatus().isOk();

		// Criar uma segunda tarefa com um nome diferente
		var tarefaOutra = new ListaTarefa("Pagar água", "Pagamento da conta de água",
				false, new BigDecimal("150.00"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaOutra)
				.exchange()
				.expectStatus().isOk();

		// Tentar atualizar a primeira tarefa para ter o mesmo nome da segunda
		var tarefaAtualizada = new ListaTarefa("Pagar água", "Descrição diferente",
				false, new BigDecimal("200.00"), dataLimite);
		tarefaAtualizada.setId(1L); // Manter o mesmo ID
		tarefaAtualizada.setOrdemApresentacao(1); // Tentar mudar a ordem de apresentação

		webTestClient
				.put()
				.uri("/tarefas/1")
				.bodyValue(tarefaAtualizada)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.error").isEqualTo("Uma tarefa com o nome 'Pagar água' já existe.");
	}

	@Test
	void testUpdateListaTarefaChangeOrder() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Criar uma tarefa válida
		var tarefaExistente = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaExistente)
				.exchange()
				.expectStatus().isOk();

		// Tentar atualizar a ordem de apresentação
		var tarefaAtualizada = new ListaTarefa("Pagar aluguel", "Descrição diferente",
				false, new BigDecimal("200.00"), dataLimite);
		tarefaAtualizada.setId(1L); // Manter o mesmo ID
		tarefaAtualizada.setOrdemApresentacao(2); // Alterar a ordem

		webTestClient
				.put()
				.uri("/tarefas/1")
				.bodyValue(tarefaAtualizada)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.error").isEqualTo("A ordem de apresentação não pode ser alterada.");
	}

	@Test
	void testCreateListaTarefaWithNegativeValue() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Tentar criar uma tarefa com valor negativo
		var tarefaInvalida = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("-100.00"), dataLimite);

		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaInvalida)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.error").isEqualTo("O custo da tarefa não pode ser inferior a zero.");
	}

	@Test
	void testUpdateListaTarefaWithNegativeValue() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		// Criar uma tarefa válida
		var tarefaExistente = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite);
		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(tarefaExistente)
				.exchange()
				.expectStatus().isOk();

		// Tentar atualizar a tarefa para ter um valor negativo
		var tarefaAtualizada = new ListaTarefa("Pagar aluguel", "Descrição diferente",
				false, new BigDecimal("-50.00"), dataLimite);
		tarefaAtualizada.setId(1L); // Manter o mesmo ID
		tarefaAtualizada.setOrdemApresentacao(1); // Manter a ordem

		webTestClient
				.put()
				.uri("/tarefas/1")
				.bodyValue(tarefaAtualizada)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.error").isEqualTo("O custo da tarefa não pode ser inferior a zero.");
	}
}
