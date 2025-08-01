package br.com.botcnpj.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import br.com.botcnpj.model.Divergence;

public class ReportWriterService {

    public void writeReportToConsole(List<Divergence> divergences) {
        System.out.println("\n--- Relatório de Divergência ---");

        if(divergences.isEmpty()){
            System.out.println("Nenhuma divergência encontrada.");
        } else {

            System.out.println("Total de " + divergences.size() + " divergências encontradas:");
            System.out.println("--------------------------------------------------------------------------");
            System.out.printf("%-15s %-20s %-20s %-20s%n", "Código Local", "Inscrição Estadual", "Status Divergente", "CNPJ");

            for(Divergence d : divergences) {
                System.out.printf("%-15d %-20s %-20s %-20s%n", d.codigo(), d.ie(), d.status(), d.cnpj());
            }
        }
        System.out.println("--- Fim do relatório ---\n");
    }

    public void writeReportToFile(List<Divergence> divergences, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("--- Relatório de Divergência ---\n\n");

            if (divergences.isEmpty()) {
                writer.write("Nenhuma divergência encontrada.\n");
            } else {
                writer.write("Total de " + divergences.size() + " divergências encontradas:\n");
                writer.write("--------------------------------------------------------------------------\n");
                writer.write(String.format("%-15s %-20s %-20s %-18s%n", "Código Local", "Inscrição Estadual", "Status Divergente", "CNPJ"));
                writer.write("--------------------------------------------------------------------------\n");

                for (Divergence d : divergences) {
                    writer.write(String.format("%-15d %-20s %-20s %-18s%n", d.codigo(), d.ie(), d.status(), d.cnpj()));
                }
            }
            System.out.println("Relatório gravado com sucesso no arquivo: " + filePath);
        } catch (IOException e) {
            System.err.println("Erro: Não foi possível escrever o relatório no arquivo " + filePath);
            e.printStackTrace();
        }
    }

    public void writeDivergentCodesToFile(List<Divergence> divergences, String filePath) {
        if (divergences.isEmpty()) {
            return;
        }

        try {
            String codesAsString = divergences.stream()
                    .map(d -> String.valueOf(d.codigo()))
                    .collect(Collectors.joining(", "));

            Path path = Paths.get(filePath);
            Files.writeString(path, codesAsString);

            System.out.println("Relatório de códigos divergentes gravado com sucesso em: " + filePath);

        } catch (IOException e) {
            System.err.println("Erro: Não foi possível escrever o relatório de códigos no arquivo " + filePath);
            e.printStackTrace();
        }
    }
}
