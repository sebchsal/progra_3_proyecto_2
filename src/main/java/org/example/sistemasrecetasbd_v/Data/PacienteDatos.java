package org.example.sistemasrecetasbd_v.Data;

import org.example.sistemasrecetasbd_v.Model.Clases.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDatos {

    public Paciente insert(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO paciente (identificacion, nombre, fecha_nacimiento, telefono) VALUES (?, ?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, paciente.getIdentificacion());
            ps.setString(2, paciente.getNombre());
            if (paciente.getFechaNacimiento() != null)
                ps.setDate(3, Date.valueOf(paciente.getFechaNacimiento()));
            else
                ps.setNull(3, Types.DATE);
            ps.setString(4, paciente.getTelefono());
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

    public Paciente findById(int id) throws SQLException {
        String sql = "SELECT * FROM paciente WHERE id = ?";
        Paciente paciente = null;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente(
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getDate("fecha_nacimiento") != null ?
                                    rs.getDate("fecha_nacimiento").toLocalDate() : null,
                            rs.getString("telefono")
                    );
                }
            }
        }
        return paciente;
    }

    public List<Paciente> findAll() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente ORDER BY id";

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_nacimiento") != null ?
                                rs.getDate("fecha_nacimiento").toLocalDate() : null,
                        rs.getString("telefono")
                );
                lista.add(paciente);
            }
        }
        return lista;
    }

    public Paciente update(Paciente paciente) throws SQLException {
        String sql = "UPDATE paciente SET nombre=?, fecha_nacimiento=?, telefono=? WHERE identificacion=?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, paciente.getNombre());
            if (paciente.getFechaNacimiento() != null)
                ps.setDate(2, Date.valueOf(paciente.getFechaNacimiento()));
            else
                ps.setNull(2, Types.DATE);
            ps.setString(3, paciente.getTelefono());
            ps.setString(4, paciente.getIdentificacion());
            ps.executeUpdate();
        }
        return paciente;
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM paciente WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}

