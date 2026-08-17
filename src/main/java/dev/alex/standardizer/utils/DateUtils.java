package dev.alex.standardizer.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;

public class DateUtils {

    private static final DateTimeFormatter DIA_MES_ANO2_BARRA = new DateTimeFormatterBuilder()
            .appendPattern("dd/MM/")
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
            .toFormatter();

    private static final DateTimeFormatter DIA_MES_ANO2_TRACO = new DateTimeFormatterBuilder()
            .appendPattern("dd-MM-")
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
            .toFormatter();

    private static final List<DateTimeFormatter> FORMATOS_ENTRADA = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DIA_MES_ANO2_BARRA,
            DIA_MES_ANO2_TRACO,
            DateTimeFormatter.ofPattern("ddMMyyyy"),
            DateTimeFormatter.ofPattern("ddMMyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    public static String padronizarData(String dataOriginal) {
        if (dataOriginal == null || dataOriginal.trim().isEmpty()) {
            return "";
        }

        String dataLimpa = dataOriginal.trim();

        for (DateTimeFormatter formatter : FORMATOS_ENTRADA) {
            try {
                LocalDate date = LocalDate.parse(dataLimpa, formatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
            }
        }

        System.out.println("Não foi possível formatar a data: " + dataOriginal);
        return dataLimpa;
    }
}