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
                    medico = new Medico();
                    medico.setId(rs.getInt("id"));
                    medico.setIdentificacion(rs.getString("identificacion"));
                    medico.setNombre(rs.getString("nombre"));
                    medico.setClave(rs.getString("clave"));
                    medico.setEspecialidad(rs.getString("especialidad"));
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
                Medico medico = new Medico();
                medico.setId(rs.getInt("id"));
                medico.setIdentificacion(rs.getString("identificacion"));
                medico.setNombre(rs.getString("nombre"));
                medico.setClave(rs.getString("clave"));
                medico.setEspecialidad(rs.getString("especialidad"));
                lista.add(medico);
            }
        }
        return lista;
    }

    public Medico update(Medico medico) throws SQLException {
        String sql = "UPDATE medico SET identificacion=?, nombre=?, clave=?, especialidad=? WHERE id=? ";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medico.getIdentificacion());
            ps.setString(2, medico.getNombre());
            ps.setString(3, medico.getClave());
            ps.setString(4, medico.getEspecialidad());
            ps.setInt(5, medico.getId());
            ps.executeUpdate();
        }
        return medico;
    }

    public int obtenerUltimoId() throws SQLException {
        String sql = "SELECT MAX(id) AS max_id FROM medico";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0; // si la tabla está vacía
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM medico WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}
