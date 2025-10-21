package org.example.sistemasrecetasbd_v.Data;

import org.example.sistemasrecetasbd_v.Model.Clases.Receta;
import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDatos {

    public Receta insert(Receta receta) throws SQLException {
        String sql = """
                INSERT INTO receta (
                    estado, cantidad, detalle,
                    codigo_medicamento, nombre_medicamento, tipo_presentacion,
                    identificacion_paciente, nombre_paciente, fecha_nacimiento_paciente, telefono_paciente,
                    fecha_confeccion, fecha_entrega
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, receta.getEstado());
            ps.setInt(2, receta.getCantidad());
            ps.setString(3, receta.getDetalle());

            ps.setString(4, receta.getMedicamento().getCodigo());
            ps.setString(5, receta.getMedicamento().getNombre());
            ps.setString(6, receta.getMedicamento().getTipoPresentacion());

            ps.setString(7, receta.getPaciente().getIdentificacion());
            ps.setString(8, receta.getPaciente().getNombre());
            if (receta.getPaciente().getFechaNacimiento() != null)
                ps.setDate(9, Date.valueOf(receta.getPaciente().getFechaNacimiento()));
            else
                ps.setNull(9, Types.DATE);
            ps.setString(10, receta.getPaciente().getTelefono());

            ps.setDate(11, Date.valueOf(receta.getFechaConfeccion()));
            if (receta.getFechaEntrega() != null)
                ps.setDate(12, Date.valueOf(receta.getFechaEntrega()));
            else
                ps.setNull(12, Types.DATE);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return findById(id);
                }
            }
        }
        return null;
    }

    public Receta findById(int id) throws SQLException {
        String sql = "SELECT * FROM receta WHERE id = ?";
        Receta receta = null;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    receta = new Receta(
                            rs.getString("estado"),
                            rs.getInt("cantidad"),
                            rs.getString("detalle"),
                            new Medicamento(
                                    rs.getString("codigo_medicamento"),
                                    rs.getString("nombre_medicamento"),
                                    rs.getString("tipo_presentacion")
                            ),
                            new Paciente(
                                    rs.getString("identificacion_paciente"),
                                    rs.getString("nombre_paciente"),
                                    rs.getDate("fecha_nacimiento_paciente") != null ?
                                            rs.getDate("fecha_nacimiento_paciente").toLocalDate() : null,
                                    rs.getString("telefono_paciente")
                            ),
                            rs.getDate("fecha_confeccion").toLocalDate(),
                            rs.getDate("fecha_entrega") != null ?
                                    rs.getDate("fecha_entrega").toLocalDate() : null
                    );
                }
            }
        }
        return receta;
    }

    public List<Receta> findAll() throws SQLException {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta";

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Receta receta = new Receta(
                        rs.getString("estado"),
                        rs.getInt("cantidad"),
                        rs.getString("detalle"),
                        new Medicamento(
                                rs.getString("codigo_medicamento"),
                                rs.getString("nombre_medicamento"),
                                rs.getString("tipo_presentacion")
                        ),
                        new Paciente(
                                rs.getString("identificacion_paciente"),
                                rs.getString("nombre_paciente"),
                                rs.getDate("fecha_nacimiento_paciente") != null ?
                                        rs.getDate("fecha_nacimiento_paciente").toLocalDate() : null,
                                rs.getString("telefono_paciente")
                        ),
                        rs.getDate("fecha_confeccion").toLocalDate(),
                        rs.getDate("fecha_entrega") != null ?
                                rs.getDate("fecha_entrega").toLocalDate() : null
                );
                lista.add(receta);
            }
        }
        return lista;
    }

    public Receta update(Receta receta) throws SQLException {
        String sql = """
                UPDATE receta SET
                    estado=?, cantidad=?, detalle=?,
                    codigo_medicamento=?, nombre_medicamento=?, tipo_presentacion=?,
                    identificacion_paciente=?, nombre_paciente=?, fecha_nacimiento_paciente=?, telefono_paciente=?,
                    fecha_confeccion=?, fecha_entrega=?
                WHERE id=?
                """;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, receta.getEstado());
            ps.setInt(2, receta.getCantidad());
            ps.setString(3, receta.getDetalle());

            ps.setString(4, receta.getMedicamento().getCodigo());
            ps.setString(5, receta.getMedicamento().getNombre());
            ps.setString(6, receta.getMedicamento().getTipoPresentacion());

            ps.setString(7, receta.getPaciente().getIdentificacion());
            ps.setString(8, receta.getPaciente().getNombre());
            if (receta.getPaciente().getFechaNacimiento() != null)
                ps.setDate(9, Date.valueOf(receta.getPaciente().getFechaNacimiento()));
            else
                ps.setNull(9, Types.DATE);
            ps.setString(10, receta.getPaciente().getTelefono());

            ps.setDate(11, Date.valueOf(receta.getFechaConfeccion()));
            if (receta.getFechaEntrega() != null)
                ps.setDate(12, Date.valueOf(receta.getFechaEntrega()));
            else
                ps.setNull(12, Types.DATE);

            ps.setInt(13, receta.getId());
            ps.executeUpdate();
        }
        return receta;
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM receta WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}
