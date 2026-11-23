package cl.duoc.llanquihuetour.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada: abre la interfaz gráfica Swing. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // se usa el look and feel por defecto
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}