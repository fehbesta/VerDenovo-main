package com.verdenovo.api.service;

public final class CnpjUtils {
    private CnpjUtils() {}

    public static String normalizar(String cnpj) {
        if (cnpj == null) return "";
        return cnpj.replaceAll("\\D", "");
    }

    public static boolean isValido(String cnpj) {
        String digits = normalizar(cnpj);
        if (digits.length() != 14 || digits.chars().distinct().count() == 1) return false;

        int primeiro = calcularDigito(digits.substring(0, 12), new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int segundo = calcularDigito(digits.substring(0, 12) + primeiro, new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

        return digits.equals(digits.substring(0, 12) + primeiro + segundo);
    }

    private static int calcularDigito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
