package cl.duoc.llanquihuetour.data;

import cl.duoc.llanquihuetour.model.Tour;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class GestorDatos {

    private final List<Tour> listaTours = new ArrayList<>();

    public void agregar(Tour tour){
        listaTours.add(tour);
    }

    public List<Tour> listar(){
        return listaTours;
    }

    public List<Tour> buscarPorGenero(String genero){
        List<Tour> encontrados = new ArrayList<>();
        for (Tour tour: listaTours){
            if(tour.getGenero().equalsIgnoreCase(genero)){
                encontrados.add(tour);
            }
        }
        return encontrados;
    }

    public List<Tour> buscarPorNombre(String nombre){
        List<Tour> encontrados = new ArrayList<>();
        String buscado = nombre.trim().toLowerCase();
        for(Tour tour: listaTours){
            if(tour.getNombre().toLowerCase().contains(buscado)){
                encontrados.add(tour);
            }
        }
        return encontrados;
    }

    public int contarTours(){
        return listaTours.size();
    }

    public boolean eliminar(Tour tour){
        return listaTours.removeIf(t -> t == tour);
    }

    public boolean reemplazar(Tour antiguo, Tour nuevo){
        for(int i = 0; i < listaTours.size(); i++){
            if (listaTours.get(i) == antiguo){
                listaTours.set(i,nuevo);
                return true;
            }
        }
        return false;
    }

    public List<String> listaDeGenero(){
        TreeSet<String> generos = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Tour tour: listaTours){
            generos.add(tour.getGenero());
        }
        return new ArrayList<>(generos);
    }

    public void limpiar(){
        listaTours.clear();
    }

    public double calcularValorTotal(){
        double total = 0;
        for(Tour tour: listaTours){
            total += tour.getPrecio();
        }
        return total;
    }

    public int cargarDesdeExcel() throws IOException{

        try(InputStream archivo = GestorDatos.class.getResourceAsStream("/Tours.xlsx")) {
            if (archivo == null){
                throw new IOException("No se encontró el archivo excel Tours.xlsx en resources");
            }

            int cargados = 0;
            try (Workbook libro = WorkbookFactory.create(archivo)) {
                Sheet hoja = libro.getSheetAt(0);
                for (int i = 1; i <= hoja.getLastRowNum(); i++){
                    Row fila = hoja.getRow(i);
                    if (fila == null || fila.getCell(0) == null){
                        continue;
                    }
                    String nombre = fila.getCell(0).getStringCellValue();
                    String genero = fila.getCell(1).getStringCellValue();
                    double precio = fila.getCell(2).getNumericCellValue();
                    int cupos = (int) fila.getCell(3).getNumericCellValue();
                    agregar(new Tour(nombre, genero, (int) precio, cupos));
                    cargados++;
                }
            }
            return cargados;
        } catch (RuntimeException e){
            throw new IOException("El excel contiene celdas inválidas" + e.getMessage());
        }
    }



}
