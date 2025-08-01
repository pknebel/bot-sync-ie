package br.com.botcnpj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.botcnpj.model.ApiCnpjResponse;
import br.com.botcnpj.model.Divergence;
import br.com.botcnpj.model.LocalRecord;

public class StatusComparatorService {
    private final CnpjApiClientService apiClient;

    public StatusComparatorService(CnpjApiClientService apiClient) {
        this.apiClient = apiClient;
    }

    public List<Divergence> findDivergences(List<LocalRecord> localRecords){
        List<Divergence> divergences = new ArrayList<>();

        for(LocalRecord localRecord : localRecords){
            Optional<ApiCnpjResponse> apiResponseOpt = apiClient.fetchCnpjData(localRecord.cnpj());

            if(apiResponseOpt.isPresent()){

                ApiCnpjResponse apiResponse = apiResponseOpt.get();

                boolean localStatusIsActive = (localRecord.status() == 1);

                Optional<ApiCnpjResponse.InscricaoEstadual> ieApiOpt = findIeInApiResponse(apiResponse, localRecord.ie());

                if (ieApiOpt.isPresent()) {
                    
                    ApiCnpjResponse.InscricaoEstadual apiIe = ieApiOpt.get();

                    if (localStatusIsActive != apiIe.ativo()) {
                        System.out.println("Divergência de status encontrada para IE: " + localRecord.ie());
                        divergences.add(new Divergence(localRecord.codigo(), localRecord.ie(), localRecord.status(), localRecord.cnpj()));
                    }

                } else {
                    System.out.println("Divergencia: IE " + localRecord.ie() + " não encontrada na API para o CNPJ: " + localRecord.cnpj());
                    divergences.add(new Divergence(localRecord.codigo(), localRecord.ie(), localRecord.status(), localRecord.cnpj()));
                }

            }else {
                System.out.println("CNPJ " + localRecord.cnpj() + " não encontrado na API.");
            }
        }
        return divergences;
    }

    private Optional<ApiCnpjResponse.InscricaoEstadual> findIeInApiResponse(ApiCnpjResponse response, String localIe) {
        if(response.estabelecimento() == null || response.estabelecimento().inscricoesEstaduais().isEmpty()){
            return Optional.empty();
        }

        return response.estabelecimento().inscricoesEstaduais().stream()
                .filter(ieApi -> localIe.equals(ieApi.inscricaoEstadual()))
                .findFirst();

    }

}
