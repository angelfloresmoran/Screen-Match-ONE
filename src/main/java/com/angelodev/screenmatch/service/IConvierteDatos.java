package com.angelodev.screenmatch.service;

public interface IConvierteDatos {
    //A continuación se declara el metodo
    <T> T obtenerDatos(String json, Class<T> clase);
}
