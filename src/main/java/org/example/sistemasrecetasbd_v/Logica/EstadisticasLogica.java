package org.example.sistemasrecetasbd_v.Logica;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import org.example.sistemasrecetasbd_v.Model.Clases.Receta;

public class EstadisticasLogica {
    // Gráfico de Medicamentos
    public void cargarGraficoMedicamentos(TableView<Receta> historicoRecetas, TableView<Medicamento> tblMedicamentosDashboard,
                                          BarChart<String, Number> graficoMedicamentos) {
        if (historicoRecetas == null || historicoRecetas.getItems().isEmpty()) {
            graficoMedicamentos.getData().clear();
            return;
        }

        var seleccionados = tblMedicamentosDashboard.getSelectionModel().getSelectedItems();
        if (seleccionados == null || seleccionados.isEmpty()) {
            graficoMedicamentos.getData().clear();
            return;
        }

        graficoMedicamentos.getData().clear();

        for (Medicamento med : seleccionados) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(med.getNombre());

            var mesesUsados = historicoRecetas.getItems().stream()
                    .filter(r -> r.getMedicamento() != null && r.getMedicamento().getNombre() != null)
                    .filter(r -> med.getNombre().equalsIgnoreCase(r.getMedicamento().getNombre()))
                    .filter(r -> r.getFechaConfeccion() != null)
                    .map(r -> r.getFechaConfeccion().getYear() + "-" +
                            String.format("%02d", r.getFechaConfeccion().getMonthValue()))
                    .distinct().sorted().toList();

            for (String mes : mesesUsados) {
                long cantidad = historicoRecetas.getItems().stream()
                        .filter(r -> r.getMedicamento() != null && r.getMedicamento().getNombre() != null)
                        .filter(r -> med.getNombre().equalsIgnoreCase(r.getMedicamento().getNombre()))
                        .filter(r -> {
                            if (r.getFechaConfeccion() == null) return false;
                            String ym = r.getFechaConfeccion().getYear() + "-" +
                                    String.format("%02d", r.getFechaConfeccion().getMonthValue());
                            return ym.equals(mes);
                        })
                        .mapToLong(Receta::getCantidad)
                        .sum();

                serie.getData().add(new XYChart.Data<>(mes, cantidad));
            }

            graficoMedicamentos.getData().add(serie);
        }
    }

    public void cargarGraficoRecetas(TableView<Receta> historicoRecetas, PieChart graficoRecetas,
                                     Label lblCantidadRecetas, Label lblRecetasConfeccion,
                                     Label lblRecetasProceso, Label lblRecetasListas, Label lblRecetasEntregadas) {
        if (historicoRecetas == null || historicoRecetas.getItems().isEmpty()) {
            graficoRecetas.getData().clear();
            lblCantidadRecetas.setText("0");
            lblRecetasConfeccion.setText("0");
            lblRecetasProceso.setText("0");
            lblRecetasListas.setText("0");
            lblRecetasEntregadas.setText("0");
            return;
        }

        long confeccionadas = historicoRecetas.getItems().stream()
                .filter(r -> "confeccionada".equalsIgnoreCase(r.getEstado())).count();
        long proceso = historicoRecetas.getItems().stream()
                .filter(r -> "proceso".equalsIgnoreCase(r.getEstado())).count();
        long listas = historicoRecetas.getItems().stream()
                .filter(r -> "lista".equalsIgnoreCase(r.getEstado())).count();
        long entregadas = historicoRecetas.getItems().stream()
                .filter(r -> "entregada".equalsIgnoreCase(r.getEstado())).count();

        long total = confeccionadas + proceso + listas + entregadas;
        if (total == 0) return;

        lblCantidadRecetas.setText(String.valueOf(total));
        lblRecetasConfeccion.setText(String.valueOf(confeccionadas));
        lblRecetasProceso.setText(String.valueOf(proceso));
        lblRecetasListas.setText(String.valueOf(listas));
        lblRecetasEntregadas.setText(String.valueOf(entregadas));

        graficoRecetas.getData().clear();

        if (confeccionadas > 0) {
            double porcentaje = (confeccionadas * 100.0) / total;
            PieChart.Data d1 = new PieChart.Data(String.format("Confeccionada (%.1f%%)", porcentaje), confeccionadas);
            graficoRecetas.getData().add(d1);
            d1.getNode().setStyle("-fx-pie-color: #3498db;");
        }
        if (proceso > 0) {
            double porcentaje = (proceso * 100.0) / total;
            PieChart.Data d2 = new PieChart.Data(String.format("Proceso (%.1f%%)", porcentaje), proceso);
            graficoRecetas.getData().add(d2);
            d2.getNode().setStyle("-fx-pie-color: #e74c3c;");
        }
        if (listas > 0) {
            double porcentaje = (listas * 100.0) / total;
            PieChart.Data d3 = new PieChart.Data(String.format("Lista (%.1f%%)", porcentaje), listas);
            graficoRecetas.getData().add(d3);
            d3.getNode().setStyle("-fx-pie-color: #f1c40f;");
        }
        if (entregadas > 0) {
            double porcentaje = (entregadas * 100.0) / total;
            PieChart.Data d4 = new PieChart.Data(String.format("Entregada (%.1f%%)", porcentaje), entregadas);
            graficoRecetas.getData().add(d4);
            d4.getNode().setStyle("-fx-pie-color: #2ecc71;");
        }
    }
}
