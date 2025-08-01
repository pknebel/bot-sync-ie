package br.com.botcnpj.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record ApiCnpjResponse(
    @SerializedName("estabelecimento") Estabelecimento estabelecimento
) {

    public record Estabelecimento(
        @SerializedName("inscricoes_estaduais") List<InscricaoEstadual> inscricoesEstaduais
    ) {
    }

    public record InscricaoEstadual(
        @SerializedName("inscricao_estadual") String inscricaoEstadual,
        @SerializedName("ativo") boolean ativo
    ) {
    }
}