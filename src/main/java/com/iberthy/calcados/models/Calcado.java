package com.iberthy.calcados.models;

import com.iberthy.calcados.mensage.Mensagem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Calcado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    Long id;

    @NotBlank(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    String nome;

    @NotBlank(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    String marca;

    @NotNull(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    Integer tamanho;

    @NotNull(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    @Min(value = 0, message = Mensagem.ERRO_VALORES_MINIMOS)
    Double Valor;

    @NotNull(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    @Min(value = 0, message = Mensagem.ERRO_VALORES_MINIMOS)
    Integer quantidade;

    @Column
    @Size(min = 15, max = 200, message = Mensagem.ERRO_VALORES_TAMANHO)
    String descricao;

    @Column
    Date delete;

    @NotBlank(message = Mensagem.ERRO_CAMPO_VAZIO)
    @Column
    String imagemUri;
}
