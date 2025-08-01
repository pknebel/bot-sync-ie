package br.com.botcnpj;

import java.util.List;

import br.com.botcnpj.model.Divergence;
import br.com.botcnpj.model.LocalRecord;
import br.com.botcnpj.service.CnpjApiClientService;
import br.com.botcnpj.service.FileReaderService;
import br.com.botcnpj.service.ReportWriterService;
import br.com.botcnpj.service.StatusComparatorService;

public class Main {
    public static void main(String[] args) {
        
        System.out.println("Iniciando bot de verificacao de CNPJ...");

            var apiClient = new CnpjApiClientService();
            var fileReader = new FileReaderService();
            var comparator = new StatusComparatorService(apiClient);
            var reportWriter = new ReportWriterService();

            String filePath = "src\\main\\resources\\registros.txt";

            List<LocalRecord> localRecords = fileReader.readRecordsFromFile(filePath);

            if (localRecords.isEmpty()) {
                System.out.println("Nenhum registro encontrado no arquivo: " + filePath + ". Encerrando.");
                return;
            }

            System.out.println("Total de registros lidos: " + localRecords.size());

            List<Divergence> divergences = comparator.findDivergences(localRecords);

            reportWriter.writeReportToConsole(divergences);

            String reportPath = "src\\main\\resources\\relatorio_divergencias.txt";

            reportWriter.writeReportToFile(divergences, reportPath);

            String codesReportPath = "src\\main\\resources\\relatorio_codigos.txt";

            reportWriter.writeDivergentCodesToFile(divergences, codesReportPath);

            System.out.println("Processamento concluído.");

        }

    }
