package com.example.apiprodutosmaven.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

//Entre parenteses sao os atributos do produto que serao mandados pelo usuario (name e value)
public record ProductRecordDto(@NotBlank String name, @NotNull BigDecimal value) {
}
