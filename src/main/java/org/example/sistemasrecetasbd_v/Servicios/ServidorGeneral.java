package org.example.sistemasrecetasbd_v.Servicios;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.example.sistemasrecetasbd_v.Data.GsonProvider;
import org.example.sistemasrecetasbd_v.Logica.*;
import org.example.sistemasrecetasbd_v.Model.Clases.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorGeneral {

    private final Gson gson = GsonProvider.get();

    // Instancias de la capa lógica
    private final MedicoLogica medicoLogica = new MedicoLogica();
    private final FarmaceutaLogica farmaceutaLogica = new FarmaceutaLogica();
    private final PacienteLogica pacienteLogica = new PacienteLogica();
    private final MedicamentoLogica medicamentoLogica = new MedicamentoLogica();
    private final RecetaLogica recetaLogica = new RecetaLogica();

    public void iniciar() throws Exception {
        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("ServidorGeneral iniciado en puerto 5000...");
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> manejar(socket)).start();
            }
        }
    }

    private void manejar(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String json = in.readUTF();
            System.out.println("[RX] " + json);

            String respuesta;

            try {
                Peticion p = gson.fromJson(json, Peticion.class);
                if (p == null || p.op == null || p.tipo == null) {
                    throw new IllegalArgumentException("Petición inválida (falta tipo u op)");
                }

                respuesta = procesarPeticion(p);

            } catch (Exception ex) {
                ex.printStackTrace();
                String msg = ex.getMessage() == null ? "" : ex.getMessage().replace("\"", "'");
                respuesta = "{\"error\":\"" + ex.getClass().getSimpleName() + ": " + msg + "\"}";
            }

            System.out.println("[TX] " + respuesta);
            out.writeUTF(respuesta);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String procesarPeticion(Peticion p) throws Exception {
        return switch (p.tipo.toLowerCase()) {
            case "medico" -> manejarMedico(p);
            case "farmaceuta" -> manejarFarmaceuta(p);
            case "paciente" -> manejarPaciente(p);
            case "medicamento" -> manejarMedicamento(p);
            case "receta" -> manejarReceta(p);
            default -> "{\"error\":\"Tipo de entidad no válido\"}";
        };
    }

    // ---------- MÉDICO ----------
    private String manejarMedico(Peticion p) throws Exception {
        return switch (p.op) {
            case "create" -> gson.toJson(medicoLogica.insert(gson.fromJson(p.data, Medico.class)));
            case "update" -> gson.toJson(medicoLogica.update(gson.fromJson(p.data, Medico.class)));
            case "delete" -> {
                boolean ok = medicoLogica.delete(p.id);
                yield "{\"ok\":" + ok + "}";
            }
            case "findAll" -> gson.toJson(medicoLogica.findAll());
            case "findById" -> gson.toJson(medicoLogica.findById(p.id));
            default -> "{\"error\":\"Operación no válida\"}";
        };
    }

    // ---------- FARMACEUTA ----------
    private String manejarFarmaceuta(Peticion p) throws Exception {
        return switch (p.op) {
            case "create" -> gson.toJson(farmaceutaLogica.insert(gson.fromJson(p.data, Farmaceuta.class)));
            case "update" -> gson.toJson(farmaceutaLogica.update(gson.fromJson(p.data, Farmaceuta.class)));
            case "delete" -> {
                boolean ok = farmaceutaLogica.delete(p.id);
                yield "{\"ok\":" + ok + "}";
            }
            case "findAll" -> gson.toJson(farmaceutaLogica.findAll());
            case "findById" -> gson.toJson(farmaceutaLogica.findById(p.id));
            default -> "{\"error\":\"Operación no válida\"}";
        };
    }

    // ---------- PACIENTE ----------
    private String manejarPaciente(Peticion p) throws Exception {
        return switch (p.op) {
            case "create" -> gson.toJson(pacienteLogica.insert(gson.fromJson(p.data, Paciente.class)));
            case "update" -> gson.toJson(pacienteLogica.update(gson.fromJson(p.data, Paciente.class)));
            case "delete" -> {
                boolean ok = pacienteLogica.delete(p.id);
                yield "{\"ok\":" + ok + "}";
            }
            case "findAll" -> gson.toJson(pacienteLogica.findAll());
            case "findById" -> gson.toJson(pacienteLogica.findById(p.id));
            default -> "{\"error\":\"Operación no válida\"}";
        };
    }

    // ---------- MEDICAMENTO ----------
    private String manejarMedicamento(Peticion p) throws Exception {
        return switch (p.op) {
            case "create" -> gson.toJson(medicamentoLogica.insert(gson.fromJson(p.data, Medicamento.class)));
            case "update" -> gson.toJson(medicamentoLogica.update(gson.fromJson(p.data, Medicamento.class)));
            case "delete" -> {
                boolean ok = medicamentoLogica.delete(p.id);
                yield "{\"ok\":" + ok + "}";
            }
            case "findAll" -> gson.toJson(medicamentoLogica.findAll());
            case "findById" -> gson.toJson(medicamentoLogica.findById(p.id));
            default -> "{\"error\":\"Operación no válida\"}";
        };
    }

    // ---------- RECETA ----------
    private String manejarReceta(Peticion p) throws Exception {
        return switch (p.op) {
            case "create" -> gson.toJson(recetaLogica.insert(gson.fromJson(p.data, Receta.class)));
            case "update" -> gson.toJson(recetaLogica.update(gson.fromJson(p.data, Receta.class)));
            case "delete" -> {
                boolean ok = recetaLogica.delete(p.id);
                yield "{\"ok\":" + ok + "}";
            }
            case "findAll" -> gson.toJson(recetaLogica.findAll());
            case "findById" -> gson.toJson(recetaLogica.findById(p.id));
            default -> "{\"error\":\"Operación no válida\"}";
        };
    }

    // ---------- Clase auxiliar ----------
    public static class Peticion {
        public String tipo; // medico, farmaceuta, medicamento, receta
        public String op;   // create, update, delete, findAll, findById
        public int id;
        public JsonElement data;
    }

    public static void main(String[] args) throws Exception {
        new ServidorGeneral().iniciar();
    }
}
