package org.example.sistemasrecetasbd_v.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sistemasrecetasbd_v.Model.Clases.Administrador;
import org.example.sistemasrecetasbd_v.Model.Listas.ListaMedicos;
import org.example.sistemasrecetasbd_v.Model.Listas.ListaFarmaceutas;
import org.example.sistemasrecetasbd_v.Model.Clases.Usuario;

import java.util.Optional;

public class CambiarContrasenaController {
    // Controles de CambiarContrasena.fxml
    @FXML private TextField txtIdentificacionCambioContrasena;
    @FXML private PasswordField pwdContrasenaActual;
    @FXML private PasswordField pwdContrasenaNueva;
    @FXML private PasswordField pwdConfirmarContrasenaNueva;
    @FXML private Button btnCambiarContrasena;
    @FXML private Button btnVolver;

    // Listas y administrador único
    private Administrador administrador;
    private ListaMedicos listaMedicos;
    private ListaFarmaceutas listaFarmaceutas;

    // Clase para encontrar el origen de la identificacion y de esta manera mantenerse en esa lista
    private enum Origen { ADMIN, MEDICO, FARMACEUTA }
    private static class Hallazgo {
        final Usuario usuario;
        final Origen origen;
        Hallazgo(Usuario u, Origen o) { this.usuario = u; this.origen = o; }
    }

    // recibe las listas y opcionalmente pre-carga la identificación
    public void init(Administrador admin, ListaMedicos medicos, ListaFarmaceutas farmas, String identificacionPreCargada) {
        this.administrador = admin;
        this.listaMedicos = medicos;
        this.listaFarmaceutas = farmas;
        if (identificacionPreCargada != null && !identificacionPreCargada.isBlank() && txtIdentificacionCambioContrasena != null) {
            txtIdentificacionCambioContrasena.setText(identificacionPreCargada);
        }
    }
    // asegura que los botones tengan acciones si no se definieron en el FXML
    @FXML
    private void initialize() {
        if (btnCambiarContrasena != null && btnCambiarContrasena.getOnAction() == null) {
            btnCambiarContrasena.setOnAction(e -> cambiarContrasena());
        }
        if (btnVolver != null && btnVolver.getOnAction() == null) {
            btnVolver.setOnAction(e -> volver());
        }
    }
    // busca la identificacion, hace las validaciones y hace el cambio de contraseña
    @FXML
    private void cambiarContrasena() {
        if (administrador == null && listaMedicos == null && listaFarmaceutas == null) {
            error("No se configuraron los usuarios.");
            return;
        }
        String identificacion = safe(txtIdentificacionCambioContrasena);
        String actual = safe(pwdContrasenaActual);
        String nueva = safe(pwdContrasenaNueva);
        String confirma = safe(pwdConfirmarContrasenaNueva);

        // Validaciones de entrada
        if (identificacion.isBlank()) {
            error("Debe digitar la identificación.");
            return;
        }
        if (actual.isBlank() || nueva.isBlank() || confirma.isBlank()) {
            error("Debe completar todos los campos.");
            return;
        }
        if (!nueva.equals(confirma)) {
            error("La nueva contraseña y su confirmación no coinciden.");
            return;
        }
        if (nueva.equals(actual)) {
            error("La nueva contraseña no puede ser igual a la actual.");
            return;
        }
        // Buscar usuario
        Hallazgo h = buscarHallazgoPorIdentificacion(identificacion).orElse(null);
        if (h == null) {
            error("No se encontró un usuario con identificación: " + identificacion);
            return;
        }

        int userId = h.usuario.getId();
        boolean actualOk = switch (h.origen) {
            case ADMIN -> administrador != null && administrador.getClave().equals(actual);
            case MEDICO -> listaMedicos != null && listaMedicos.verificarClave(userId, actual);
            case FARMACEUTA -> listaFarmaceutas != null && listaFarmaceutas.verificarClave(userId, actual);
        };
        if (!actualOk) {
            error("La contraseña actual no es correcta.");
            return;
        }
        // Actualizar la clave
        boolean actualizado = switch (h.origen) {
            case ADMIN -> {
                if (administrador != null) {
                    administrador.setClave(nueva);
                    yield true;
                }
                yield false;
            }
            case MEDICO -> listaMedicos != null && listaMedicos.actualizarClave(userId, nueva);
            case FARMACEUTA -> listaFarmaceutas != null && listaFarmaceutas.actualizarClave(userId, nueva);
        };
        if (!actualizado) {
            error("No fue posible actualizar la contraseña.");
            return;
        }
        info("Contraseña actualizada correctamente.");
        cerrar();
    }

    // btnVolver
    @FXML
    private void volver() {
        cerrar();
    }
    // localiza al usuario por identificación
    private Optional<Hallazgo> buscarHallazgoPorIdentificacion(String id) {
        // Buscar en administrador
        if (administrador != null && administrador.getIdentificacion().equals(id)) {
            return Optional.of(new Hallazgo(administrador, Origen.ADMIN));
        }
        // Buscar en médicos
        if (listaMedicos != null) {
            var m = listaMedicos.buscarPorIdentificacion(id);
            if (m.isPresent()) return Optional.of(new Hallazgo(m.get(), Origen.MEDICO));
        }
        // Buscar en farmacéuticos
        if (listaFarmaceutas != null) {
            var f = listaFarmaceutas.buscarPorIdentificacion(id);
            if (f.isPresent()) return Optional.of(new Hallazgo(f.get(), Origen.FARMACEUTA));
        }
        return Optional.empty();
    }

    // cierra el stage
    private void cerrar() {
        Stage stage = (Stage)((btnVolver != null ? btnVolver : btnCambiarContrasena).getScene().getWindow());
        if (stage != null) stage.close();
    }
    // devuelve el texto limpio de un TextField si esta en blanco
    private static String safe(TextField tf) { return tf == null || tf.getText() == null ? "" : tf.getText().trim(); }
    private static String safe(PasswordField pf) { return pf == null || pf.getText() == null ? "" : pf.getText().trim(); }
    // muestra una alerta de error con el mensaje del error cometido
    private static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setTitle("Error"); a.setContentText(msg); a.showAndWait();
    }
    // muestra una alerta de informacion sobre la accion que se ha hecho con el mensaje */
    private static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setTitle("Actualización"); a.setContentText(msg); a.showAndWait();
    }
}