# Agencia_Llanquihue_Tour_Semana_5

## Descripción
Proyecto en Java en donde el sistema permite leer la información de los tours desde un archivo excel externo (`Tours.xlsx`), instanciar objetos de tipo `Tour` dentro de una colección dinámica(`ArrayList`) y aplicar operaciones de recorrido y filtrado por categoría y cupos.

## Estructura
# `cl.duoc.llanquihuetour.model`
- `Tour`: Clase modelo con atributos, getters, setters y toString().

# `cl.duoc.llanquihuetour.data`
- `Gestor datos`: Lectura de archivo `.xlsx` y carga de ArrayList().

# `cl.duoc.llanquihuetour.ui`
- `Main`: Ejecución, recorrido y filtrado de la colección dentro de una ventana emergente.
- `VentanaPrincipal`: Clase con la estructura completa de la ventana emergente.
- `MainTerminal`: Ejecución, recorrido y filtrado de la colección.

# `resources/`
- `Tours.xlsx`: Archivo de datos en excel.

## Instrucciones de uso 
- Abre el proyecto en IntelliJ IDEA y ejecuta la clase `Main` para ejecutar la ventana emergente.

## Autor
Ayline Fuentes.
