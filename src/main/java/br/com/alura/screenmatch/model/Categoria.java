package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    OTHER("Other");

    private String categoriaOmdb;

    Categoria(String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }

    public static Categoria fromString(String text){
        for (Categoria categoria : values()){
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        return OTHER;
        //throw new IllegalArgumentException("Nenhuma categoria encontrada para a string");
    }
}
