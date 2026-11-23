package cl.duoc.llanquihuetour.ui;

import cl.duoc.llanquihuetour.data.GestorDatos;
import cl.duoc.llanquihuetour.model.Tour;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * INTERFAZ SWING: administra el catálogo de PuertoGames.
 * Toda la lógica de negocio vive en {@link GestorDatos}.
 */
public class VentanaPrincipal extends JFrame {

    private static final String TODOS = "(Todos)";

    private final GestorDatos gestor = new GestorDatos();
    private final ModeloTabla modelo = new ModeloTabla();
    private final JTable tabla = new JTable(modelo);
    private final JTextField txtBuscar = new JTextField(18);
    private final JComboBox<String> cmbGenero = new JComboBox<>();
    private final JLabel lblEstado = new JLabel(" ");
    private final JLabel lblResumen = new JLabel(" ");
    private final NumberFormat formatoPrecio = NumberFormat.getIntegerInstance(Locale.of("es", "CL"));
    private boolean actualizandoCombo = false;

    public VentanaPrincipal() {
        super("PuertoGames — Gestión de Videojuegos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelFiltros(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        cargarInicial();
        setMinimumSize(new Dimension(720, 420));
        setSize(860, 520);
        setLocationRelativeTo(null);
    }

    // ---------- construcción de la UI ----------

    private JPanel crearPanelFiltros() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.add(new JLabel("Nombre:"));
        p.add(txtBuscar);
        p.add(new JLabel("Género:"));
        p.add(cmbGenero);
        JButton limpiar = new JButton("Limpiar filtros");
        limpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cmbGenero.setSelectedItem(TODOS);
        });
        p.add(limpiar);

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
        });
        cmbGenero.addActionListener(e -> { if (!actualizandoCombo) refrescar(); });
        return p;
    }

    private JScrollPane crearTabla() {
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(2).setCellRenderer(derecha);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editar();
            }
        });
        return new JScrollPane(tabla);
    }

    private JPanel crearPanelInferior() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.add(boton("Agregar", e -> agregar()));
        botones.add(boton("Editar", e -> editar()));
        botones.add(boton("Eliminar", e -> eliminar()));
        botones.add(boton("Recargar Excel", e -> recargar()));

        JPanel resumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        resumen.add(lblResumen);

        JPanel fila = new JPanel(new BorderLayout());
        fila.add(botones, BorderLayout.WEST);
        fila.add(resumen, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.add(fila, BorderLayout.NORTH);
        p.add(lblEstado, BorderLayout.SOUTH);
        return p;
    }

    private JButton boton(String texto, java.awt.event.ActionListener accion) {
        JButton b = new JButton(texto);
        b.addActionListener(accion);
        return b;
    }

    // ---------- operaciones de negocio ----------

    private void cargarInicial() {
        try {
            int n = gestor.cargarDesdeExcel();
            estado("Cargados " + n + " tours desde Excel.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al leer Excel", JOptionPane.ERROR_MESSAGE);
            estado("No se pudo cargar el Excel.");
        }
        actualizarGeneros();
        refrescar();
    }

    private void recargar() {
        int r = JOptionPane.showConfirmDialog(this,
                "Se descartarán los cambios actuales y se recargará el Excel. ¿Continuar?",
                "Recargar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            gestor.limpiar();
            cargarInicial();
        }
    }

    private void agregar() {
        Tour nuevo = pedirDatos(null);
        if (nuevo != null) {
            gestor.agregar(nuevo);
            actualizarGeneros();
            refrescar();
            estado("Agregado: " + nuevo.getNombre());
        }
    }

    private void editar() {
        Tour actual = seleccionado();
        if (actual == null) { return; }
        Tour nuevo = pedirDatos(actual);
        if (nuevo != null) {
            gestor.reemplazar(actual, nuevo);
            actualizarGeneros();
            refrescar();
            estado("Modificado: " + nuevo.getNombre());
        }
    }

    private void eliminar() {
        Tour actual = seleccionado();
        if (actual == null) { return; }
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + actual.getNombre() + "\"?",
                "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            gestor.eliminar(actual);
            actualizarGeneros();
            refrescar();
            estado("Eliminado: " + actual.getNombre());
        }
    }

    private Tour seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un videojuego de la tabla.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.get(tabla.convertRowIndexToModel(fila));
    }

    /** Muestra el formulario; retorna el videojuego validado o null si se cancela. */
    private Tour pedirDatos(Tour base) {
        JTextField txtnombre = new JTextField(base == null ? "" : base.getNombre(), 20);
        JComboBox<String> genero = new JComboBox<>(gestor.listaDeGenero().toArray(new String[0]));
        genero.setEditable(true);
        genero.setSelectedItem(base == null ? "" : base.getGenero());
        JTextField precio = new JTextField(base == null ? "" : String.valueOf((long) base.getPrecio()), 10);
        JTextField txtCupos = new JTextField(base == null ? "" : String.valueOf(base.getCupos()), 10);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre:")); form.add(txtnombre);
        form.add(new JLabel("Género:"));  form.add(genero);
        form.add(new JLabel("Precio:"));  form.add(precio);
        form.add(new JLabel("Cupos: ")); form.add(txtCupos);

        while (true) {
            int r = JOptionPane.showConfirmDialog(this, form,
                    base == null ? "Nuevo tour" : "Editar tour",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) { return null; }
            try {
                String nombre = txtnombre.getText().trim();
                double p = Double.parseDouble(precio.getText().trim().replace(",", "."));
                String g = String.valueOf(genero.getEditor().getItem()).trim();
                int cupos = Integer.parseInt(txtCupos.getText().trim());

                return new Tour(nombre, g, (int) p, cupos);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Completa nombre y género; el precio no puede ser negativo.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ---------- refresco ----------

    private void actualizarGeneros() {
        actualizandoCombo = true;
        Object previo = cmbGenero.getSelectedItem();
        cmbGenero.removeAllItems();
        cmbGenero.addItem(TODOS);
        for (String g : gestor.listaDeGenero()) { cmbGenero.addItem(g); }
        cmbGenero.setSelectedItem(previo != null ? previo : TODOS);
        if (cmbGenero.getSelectedIndex() < 0) { cmbGenero.setSelectedItem(TODOS); }
        actualizandoCombo = false;
    }

    /** Aplica los filtros usando los métodos del gestor y actualiza tabla y totales. */
    private void refrescar() {
        List<Tour> lista;
        String genero = (String) cmbGenero.getSelectedItem();
        if (genero != null && !genero.equals(TODOS)) {
            lista = gestor.buscarPorGenero(genero);
        } else {
            lista = gestor.listar();
        }
        String texto = txtBuscar.getText().trim();
        if (!texto.isEmpty()) {
            List<Tour> porTitulo = gestor.buscarPorNombre(texto);
            List<Tour> interseccion = new ArrayList<>();
            for (Tour t : lista) {
                if (porTitulo.contains(t)) { interseccion.add(t); }
            }
            lista = interseccion;
        }
        modelo.setDatos(lista);

        double subtotal = 0;
        for (Tour t : lista) { subtotal += t.getPrecio(); }
        lblResumen.setText("Mostrando " + lista.size() + " de " + gestor.contarTours()
                + "  |  Valor mostrado: $" + formatoPrecio.format(subtotal)
                + "  |  Valor catálogo: $" + formatoPrecio.format(gestor.calcularValorTotal()));
    }

    private void estado(String msg) { lblEstado.setText(msg); }

    // ---------- modelo de tabla ----------

    private class ModeloTabla extends AbstractTableModel {
        private final String[] columnas = {"Título", "Género", "Precio", "Cupos"};
        private List<Tour> datos = new ArrayList<>();

        void setDatos(List<Tour> d) { datos = d; fireTableDataChanged(); }
        Tour get(int i) { return datos.get(i); }

        @Override public int getRowCount() { return datos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int c) { return columnas[c]; }

        @Override public Class<?> getColumnClass(int c) { return (c == 2 || c == 3) ? Double.class : String.class; }
        @Override public Object getValueAt(int r, int c) {
            Tour t = datos.get(r);
            return switch (c) {
                case 0 -> t.getNombre();
                case 1 -> t.getGenero();
                case 2 -> t.getPrecio();
                default -> t.getCupos();
            };
        }
    }
}
