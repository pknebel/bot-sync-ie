package br.com.botcnpj.service;

import br.com.botcnpj.model.LocalRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Objects;

public class FileReaderService {

    public List<LocalRecord> readRecordsFromFile(String filepath){

        Path path = Paths.get(filepath);

        if(!Files.exists(path)){
            System.err.println("Erro: Arquivo não encontrado em " + filepath);
            return Collections.emptyList();

        }

        try (Stream<String> lines = Files.lines(path)){
            return lines
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Erro: Não foi possível ler o arquivo " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private LocalRecord parseLine(String line){
        try {

            if (line == null || line.trim().isEmpty() || line.startsWith("#")) {
                return null;
            }

            String[] parts = line.split("\t");
            if (parts.length != 4){
                System.err.println("Aviso: Linha inválida ignorada: " + line);
                return null;

            }

            String cnpj = parts[0].trim();

            while (cnpj.length() < 14) {
                cnpj = "0" + cnpj;
            }


            String cnpjFormatado = cnpj;
            long codigo = Long.parseLong(parts[1].trim());
            String ie = parts[2].trim();
            int status = Integer.parseInt(parts[3].trim());

            return new LocalRecord(cnpjFormatado, codigo, ie, status);

        } catch (Exception e) {
            System.err.println("Aviso: Erro de formato numérico na linha, ignorado: " + line);
            return null;
        }

    }

}
