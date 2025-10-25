package org.example.sistemasrecetasbd_v.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.example.sistemasrecetasbd_v.Logica.*;
import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;
import org.example.sistemasrecetasbd_v.Model.Listas.*;
import org.example.sistemasrecetasbd_v.Model.Clases.Administrador;
import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

import java.sql.SQLException;

public class LoginController {

    String claveGuardada;

    @FXML private TextField txtIDLogin;
    @FXML private PasswordField pwdContrasenaLogin;
    @FXML private Button btnIngresarLogin, btnregistrarseLogin;
    @FXML private ProgressIndicator progress;

    private Administrador administrador = new Administrador("1234","User", "1234");
    private static ListaMedicos listaMedicos = new ListaMedicos();
    private static ListaFarmaceutas listaFarmaceutas = new ListaFarmaceutas();
    private static ListaPacientes listaPacientes = new ListaPacientes();
    private static CatalogoMedicamentos catalogoMedicamentos = new CatalogoMedicamentos();
    private static HistoricoRecetas historicoRecetas = new HistoricoRecetas();

    // Almacenar el tipo de usuario actual después de iniciar sesión con éxito
    private static String currentUserType = "";
    private static Object currentUser = null;

    @FXML private void funcionIniciarSesion() {
        String usuario = (txtIDLogin == null || txtIDLogin.getText() == null) ? "" : txtIDLogin.getText().trim();
        String pass = (pwdContrasenaLogin == null || pwdContrasenaLogin.getText() == null) ? "" : pwdContrasenaLogin.getText().trim();

        // Mostrar progress y ocultar botón
        btnIngresarLogin.setVisible(false);
        progress.setVisible(true);

        // Simular un pequeño retardo como si validara contra un servidor
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simula procesamiento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                // Ocultar el progress y restaurar botón
                progress.setVisible(false);
                btnIngresarLogin.setVisible(true);

                if (usuario.isBlank() || pass.isBlank()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de autenticación");
                    alert.setHeaderText(null);
                    alert.setContentText("Usuario o contraseña incorrectos.");
                    alert.showAndWait();
                    return;
                }
                // Administrador fijo para el manejo
                Administrador admin = administrador;
                claveGuardada = admin.getClave() == null ? "" : admin.getClave();
                if (claveGuardada.equals(pass)) {
                    currentUserType = "admin";
                    currentUser = admin;
                    sincronizarSecuencias();//Sincronizar secuencias de id
                    loadInicioScreen();
                    return;
                }
                // Chequea medico
                var medicoOpt = listaMedicos.buscarPorIdentificacion(usuario);
                if (medicoOpt.isPresent()) {
                    Medico medico = medicoOpt.get();
                    claveGuardada = medico.getClave() == null ? "" : medico.getClave();
                    if (claveGuardada.equals(pass)) {
                        currentUserType = "medico";
                        currentUser = medico;
                        sincronizarSecuencias();//Sincronizar secuencias de id
                        loadInicioScreen();
                        return;
                    }
                }
                // Chequea farmaceuta
                var farmaOpt = listaFarmaceutas.buscarPorIdentificacion(usuario);
                if (farmaOpt.isPresent()) {
                    Farmaceuta farma = farmaOpt.get();
                    claveGuardada = farma.getClave() == null ? "" : farma.getClave();
                    if (claveGuardada.equals(pass)) {
                        currentUserType = "farmacéuta";
                        currentUser = farma;
                        sincronizarSecuencias();//Sincronizar secuencias de id
                        loadInicioScreen();
                        return;
                    }
                }
                new Alert(Alert.AlertType.ERROR, "Credenciales inválidas. Verifique su identificación y contraseña.").showAndWait();
            });
        }).start();
    }

    private void loadInicioScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/Inicio.fxml"));
            Parent root = loader.load();
            InicioController inicioController = loader.getController();
            inicioController.setListas(listaMedicos, listaFarmaceutas, listaPacientes, catalogoMedicamentos, historicoRecetas);
            inicioController.setUserType(currentUserType, currentUser);

            Stage stage = (Stage) txtIDLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pantalla de Inicio");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de sistema");
            alert.setHeaderText("Error al cargar la interfaz");
            alert.setContentText("No fue posible iniciar sesión: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML private void cambioContrasena() {
        btnIngresarLogin.setVisible(false);
        progress.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simula procesamiento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                // Ocultar el progress y restaurar botón
                progress.setVisible(false);
                btnIngresarLogin.setVisible(true);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sistemasrecetasbd_v/CambiarContrasena.fxml"));
                    Parent root = loader.load();
                    CambiarContrasenaController ctrl = loader.getController();
                    String identificacionPreCargada =
                            (txtIDLogin != null && txtIDLogin.getText() != null && !txtIDLogin.getText().isBlank())
                                    ? txtIDLogin.getText().trim()
                                    : null;
                    ctrl.init(administrador, listaMedicos, listaFarmaceutas, identificacionPreCargada);
                    Stage owner = (Stage) (btnregistrarseLogin != null ? btnregistrarseLogin.getScene().getWindow()
                            : txtIDLogin.getScene().getWindow());
                    Stage dialog = new Stage();
                    dialog.initOwner(owner);
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.setTitle("Cambiar contraseña");
                    dialog.setScene(new Scene(root));
                    dialog.showAndWait();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de cambio de contraseña.\n" + e.getMessage()).showAndWait();
                }
            });
        }).start();
    }

    private void sincronizarSecuencias() {
        try {
            MedicoLogica logicaMedico = new MedicoLogica();
            PacienteLogica logicaPaciente = new PacienteLogica();
            RecetaLogica logicaReceta = new RecetaLogica();
            MedicamentoLogica logicaMedicamento = new MedicamentoLogica();
            FarmaceutaLogica logicaFarma = new FarmaceutaLogica();

            logicaMedico.reiniciarSecuencia();
            logicaPaciente.reiniciarSecuencia();
            logicaReceta.reiniciarSecuencia();
            logicaMedicamento.reiniciarSecuencia();
            logicaFarma.reiniciarSecuencia();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,
                    "No se pudieron sincronizar los contadores de ID con la base de datos.\n" + e.getMessage()
            ).showAndWait();
        }
    }

    // Deja en "blanco" el tipo de usuario al cerrar sesion
    public static void logout() {
        currentUserType = "";
        currentUser = null;
    }

    // set para el manejo de listas entre los controllers
    public void setListas(ListaMedicos medicos, ListaFarmaceutas farmaceutas, ListaPacientes pacientes,
                          CatalogoMedicamentos catMedicamentos, HistoricoRecetas recetas) {
        if (medicos != null) listaMedicos = medicos;
        if (pacientes != null) listaPacientes = pacientes;
        if (farmaceutas != null) listaFarmaceutas = farmaceutas;
        if(catMedicamentos != null) catalogoMedicamentos = catMedicamentos;
        if(recetas != null) historicoRecetas = recetas;
    }
}
