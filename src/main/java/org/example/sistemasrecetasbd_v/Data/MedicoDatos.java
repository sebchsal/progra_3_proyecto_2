package org.example.sistemasrecetasbd_v.Data;

import org.example.sistemasrecetasbd_v.Model.Clases.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDatos {

    public Medico insert(Medico medico) throws SQLException {
        String sql = "INSERT INTO medico (identificacion, nombre, clave, especialidad) VALUES (?, ?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medico.getIdentificacion());
            ps.setString(2, medico.getNombre());
            ps.setString(3, medico.getClave());
            ps.setString(4, medico.getEspecialidad());
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

    public Medico findById(int id) throws SQLException {
        String sql = "SELECT * FROM medico WHERE id = ?";
        Medico medico = null;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medico = new Medico(
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("clave"),
                            rs.getString("especialidad")
                    );
                }
            }
        }
        return medico;
    }

    public List<Medico> findAll() throws SQLException {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico ORDER BY id";

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Medico medico = new Medico(
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getString("clave"),
                        rs.getString("especialidad")
                );
                lista.add(medico);
            }
        }
        return lista;
    }

    public Medico update(Medico medico) throws SQLException {
        String sql = "UPDATE medico SET nombre=?, clave=?, especialidad=? WHERE identificacion=?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medico.getNombre());
            ps.setString(2, medico.getIdentificacion());
            ps.setString(3, medico.getClave());
            ps.setString(4, medico.getEspecialidad());
            ps.executeUpdate();
        }
        return medico;
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM medico WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}
