package com.example.demo;

import com.example.demo.entity.ListaTarefa;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class DesafioListatarefasApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	// Formatação que possibilita o teste da "dataLimite"
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDate dataLimite;

	@Test
	void testCreateListaTarefasSuccess() {
		var dataLimite = LocalDateTime.of(2024, 10, 15, 0, 0, 0);

		var listatarefas = new ListaTarefa("Pagar aluguel", "Pagamento da conta de luz",
				false, new BigDecimal("345.89"), dataLimite, 1 );

		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(listatarefas)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(1)
				.jsonPath("$[0].nome").isEqualTo(listatarefas.getNome())
				.jsonPath("$[0].descricao").isEqualTo(listatarefas.getDescricao())
				.jsonPath("$[0].realizada").isEqualTo(listatarefas.isRealizada())
				.jsonPath("$[0].custo").isEqualTo(listatarefas.getCusto().toString())
				.jsonPath("$[0].dataLimite").isEqualTo("2024-10-15T00:00:00")
				.jsonPath("$[0].ordemApresentacao").isEqualTo(listatarefas.getOrdemApresentacao());
	}

	@Test
	void testCreateListaTarefasFailure() {
		var dataLimite = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

		webTestClient
				.post()
				.uri("/tarefas")
				.bodyValue(new ListaTarefa("", "", false, new BigDecimal("0"), dataLimite, 0))
				.exchange()
				.expectStatus().isBadRequest();

	}

}
