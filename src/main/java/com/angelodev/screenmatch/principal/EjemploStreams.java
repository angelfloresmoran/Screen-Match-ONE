package com.angelodev.screenmatch.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EjemploStreams {

    public void muestrasEjemplo(){
        List <String> nombres = Arrays.asList("Brenda", "Luis", "Andrea", "Pedro", "Angel");

        nombres.stream()
                .sorted()
                .limit(3)
                .filter(n -> n.startsWith("A"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);
    }

}
