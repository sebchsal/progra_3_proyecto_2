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
                    codigo_medicamento, identificacion_paciente,
                    fecha_confeccion, fecha_entrega
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, receta.getEstado());
            ps.setInt(2, receta.getCantidad());
            ps.setString(3, receta.getDetalle());
            ps.setString(4, receta.getMedicamento().getCodigo());
            ps.setString(5, receta.getPaciente().getIdentificacion());
            ps.setDate(6, Date.valueOf(receta.getFechaConfeccion()));
            if (receta.getFechaEntrega() != null)
                ps.setDate(7, Date.valueOf(receta.getFechaEntrega()));
            else
                ps.setNull(7, Types.DATE);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return findById(rs.getInt(1));
            }
        }
        return null;
    }

    public Receta findById(int id) throws SQLException {
        String sql = """
            SELECT r.*, 
                   p.nombre AS nombre_paciente, p.fecha_nacimiento, p.telefono,
                   m.nombre AS nombre_medicamento, m.tipo_presentacion
            FROM receta r
            JOIN paciente p ON r.identificacion_paciente = p.identificacion
            JOIN medicamento m ON r.codigo_medicamento = m.codigo
            WHERE r.id = ?
            """;

        Receta receta = null;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    receta = new Receta();
                    receta.setId(rs.getInt("id"));
                    receta.setEstado(rs.getString("estado"));
                    receta.setCantidad(rs.getInt("cantidad"));
                    receta.setDetalle(rs.getString("detalle"));

                    Medicamento med = new Medicamento();
                    med.setCodigo(rs.getString("codigo_medicamento"));
                    med.setNombre(rs.getString("nombre_medicamento"));
                    med.setTipoPresentacion(rs.getString("tipo_presentacion"));
                    receta.setMedicamento(med);

                    Paciente pac = new Paciente();
                    pac.setIdentificacion(rs.getString("identificacion_paciente"));
                    pac.setNombre(rs.getString("nombre_paciente"));
                    Date fn = rs.getDate("fecha_nacimiento");
                    pac.setFechaNacimiento(fn != null ? fn.toLocalDate() : null);
                    pac.setTelefono(rs.getString("telefono"));
                    receta.setPaciente(pac);

                    receta.setFechaConfeccion(rs.getDate("fecha_confeccion").toLocalDate());
                    Date fe = rs.getDate("fecha_entrega");
                    if (fe != null) receta.setFechaEntrega(fe.toLocalDate());
                }
            }
        }
        return receta;
    }

    public List<Receta> findAll() throws SQLException {
        List<Receta> lista = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   p.nombre AS nombre_paciente, p.fecha_nacimiento, p.telefono,
                   m.nombre AS nombre_medicamento, m.tipo_presentacion
            FROM receta r
            JOIN paciente p ON r.identificacion_paciente = p.identificacion
            JOIN medicamento m ON r.codigo_medicamento = m.codigo
            ORDER BY r.id
            """;

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Receta receta = new Receta();
                receta.setId(rs.getInt("id"));
                receta.setEstado(rs.getString("estado"));
                receta.setCantidad(rs.getInt("cantidad"));
                receta.setDetalle(rs.getString("detalle"));

                Medicamento med = new Medicamento();
                med.setCodigo(rs.getString("codigo_medicamento"));
                med.setNombre(rs.getString("nombre_medicamento"));
                med.setTipoPresentacion(rs.getString("tipo_presentacion"));
                receta.setMedicamento(med);

                Paciente pac = new Paciente();
                pac.setIdentificacion(rs.getString("identificacion_paciente"));
                pac.setNombre(rs.getString("nombre_paciente"));
                Date fn = rs.getDate("fecha_nacimiento");
                pac.setFechaNacimiento(fn != null ? fn.toLocalDate() : null);
                pac.setTelefono(rs.getString("telefono"));
                receta.setPaciente(pac);

                receta.setFechaConfeccion(rs.getDate("fecha_confeccion").toLocalDate());
                Date fe = rs.getDate("fecha_entrega");
                if (fe != null) receta.setFechaEntrega(fe.toLocalDate());

                lista.add(receta);
            }
        }
        return lista;
    }

    public Receta update(Receta receta) throws SQLException {
        String sql = """
                UPDATE receta SET
                    estado=?, cantidad=?, detalle=?,
                    codigo_medicamento=?, identificacion_paciente=?,
                    fecha_confeccion=?, fecha_entrega=?
                WHERE id=?
                """;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, receta.getEstado());
            ps.setInt(2, receta.getCantidad());
            ps.setString(3, receta.getDetalle());
            ps.setString(4, receta.getMedicamento().getCodigo());
            ps.setString(5, receta.getPaciente().getIdentificacion());
            ps.setDate(6, Date.valueOf(receta.getFechaConfeccion()));
            if (receta.getFechaEntrega() != null)
                ps.setDate(7, Date.valueOf(receta.getFechaEntrega()));
            else
                ps.setNull(7, Types.DATE);
            ps.setInt(8, receta.getId());
            ps.executeUpdate();
        }
        return receta;
    }

    public int obtenerUltimoId() throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT MAX(id) AS max_id FROM receta");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("max_id") : 0;
        }
    }

    public int delete(int id) throws SQLException {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM receta WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
