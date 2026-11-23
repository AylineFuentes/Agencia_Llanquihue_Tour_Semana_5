package cl.duoc.llanquihuetour.ui;

import cl.duoc.llanquihuetour.data.GestorDatos;
import cl.duoc.llanquihuetour.model.Tour;

import java.io.IOException;

public class MainTerminal {

    public static void main(String[] args){
        GestorDatos gestor = new GestorDatos();

        try {

            int cantidad = gestor.cargarDesdeExcel();
            System.out.println("=========================================================");
            System.out.println("========== CATÁLOGO COMPLETO - LLANQUIHUE TOUR ==========");
            System.out.println("=========================================================");

            System.out.println("\n Datos cargados desde el excel " + cantidad);
            System.out.println("===CATALOGO COMPLETO===");
            for (Tour tour: gestor.listar()){
                System.out.println("- " + tour);
            }
            System.out.println("===FILTRAR POR TIPO 'Aventura' : ");
            for (Tour tour : gestor.buscarPorGenero("Aventura")){
                System.out.println("- " + tour);
            }

            System.out.println("Valor total del catálogo: " + gestor.calcularValorTotal());
        } catch (IOException e){
            System.err.println("Error al leer el excel: " + e.getMessage());
        }
    }
}
