package cl.duoc.llanquihuetour.model;

public class Tour {

    private final String nombre;
    private final String genero;
    private final int precio;
    private final int cupos;



    public Tour(String nombre, String genero, int precio, int cupos) {
        if(nombre == null || nombre.isBlank() || genero == null || genero.isBlank() || precio < 0 || cupos < 0){
            throw new IllegalArgumentException("Los datos del tour no son válidos");
        }
        this.nombre = nombre;
        this.genero = genero;
        this.precio = precio;
        this.cupos = cupos;
    }

    public String getNombre() {
        return nombre;
    }

    public String getGenero() {
        return genero;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCupos() {
        return cupos;
    }

    @Override
    public String toString() {
        return "Nombre='" + nombre + '\'' +
                "| Género='" + genero + '\'' +
                "| Precio=" + precio +
                "| Cupos=" + cupos;
    }
}
