package org.example.sistemasrecetasbd_v.Controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import org.example.sistemasrecetasbd_v.Data.GsonProvider;
import org.example.sistemasrecetasbd_v.Servicios.UserSocketService;
import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;

public class AgregarFarmaceutasController {
    @FXML private TextField txtIdentificacionAgregarFarmaceuta, txtNombreAgregarFarmaceuta;
    @FXML private Button btnRegistrarFarmaceuta, btnVolverFarmaceuta;
    @FXML private ProgressIndicator progFarmaceuta;

    private final Gson gson = GsonProvider.get();
    private Farmaceuta farmaceuta;
    private boolean modoEdicion = false;


    private TableView<Farmaceuta> tablaDestino;
    public void setTablaDestino(TableView<Farmaceuta> t) {
        this.tablaDestino = t;
    }

    public void setFarmaceuta( Farmaceuta farmaceuta, boolean editar) {
        this.farmaceuta = farmaceuta;
        this.modoEdicion = editar;

        if (editar && farmaceuta != null) {
            txtIdentificacionAgregarFarmaceuta.setText(farmaceuta.getIdentificacion());
            txtNombreAgregarFarmaceuta.setText(farmaceuta.getNombre());
        }
    }

    @FXML private void registrarFarmaceuta() {
        try{
            String identificacion = txtIdentificacionAgregarFarmaceuta.getText().trim();
            String nombre = txtNombreAgregarFarmaceuta.getText().trim();
            if (identificacion.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor, complete todos los campos del formulario.");
                return;
            }

            if (!modoEdicion) {
                farmaceuta = new Farmaceuta(identificacion, nombre, identificacion);
            }else {
                farmaceuta.setIdentificacion(identificacion);
                farmaceuta.setNombre(nombre);
            }

           guardarFarmaceutaAsync(farmaceuta, tablaDestino);
        }catch(Exception e){
            mostrarAlerta("Error al guardar los datos", e.getMessage());

        }
    }

    @FXML private void volverFarmaceuta() {
        try
        {
            Stage stage = (Stage) txtNombreAgregarFarmaceuta.getScene().getWindow();
            stage.setUserData(null);
            stage.close();
        }
        catch (Exception error) {
            mostrarAlerta("Error al guardar los datos", error.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void guardarFarmaceutaAsync(Farmaceuta f, TableView<Farmaceuta> tablaFarmaceuta) {
        btnRegistrarFarmaceuta.setDisable(true);
        btnVolverFarmaceuta.setDisable(true);
        progFarmaceuta.setVisible(true);

        Async.run(
                () -> {
                    try {
                        UserSocketService servicio = new UserSocketService();
                        String json = """
                    {
                      "tipo": "farmaceuta",
                      "op": "%s",
                      "data": %s
                    }
                    """.formatted(modoEdicion ? "update" : "create", gson.toJson(f));

                        String respuesta = servicio.enviar(json);
                        Farmaceuta guardado = gson.fromJson(respuesta, Farmaceuta.class);

                        if (!modoEdicion && guardado != null)
                            f.setId(guardado.getId());

                        return f;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                guardado -> {
                    progFarmaceuta.setVisible(false);
                    btnRegistrarFarmaceuta.setDisable(false);
                    btnVolverFarmaceuta.setDisable(false);

                    if (tablaFarmaceuta != null) {
                        if (modoEdicion) tablaFarmaceuta.refresh();
                        else tablaFarmaceuta.getItems().add(guardado);
                    }

                    new Alert(Alert.AlertType.INFORMATION,
                            (modoEdicion ? "Farmaceuta actualizado (ID: " : "Farmaceuta guardado (ID: ")
                                    + guardado.getId() + ")").showAndWait();

                    ((Stage) txtNombreAgregarFarmaceuta.getScene().getWindow()).close();
                },
                ex -> {
                    progFarmaceuta.setVisible(false);
                    btnRegistrarFarmaceuta.setDisable(false);
                    new Alert(Alert.AlertType.ERROR, "No se pudo guardar el farmaceuta: " + ex.getMessage()).showAndWait();
                }
        );
    }

}
