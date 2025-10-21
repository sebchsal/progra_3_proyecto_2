package org.example.sistemasrecetasbd_v.Data;

import org.example.sistemasrecetasbd_v.Model.Clases.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDatos {

    public Medicamento insert(Medicamento medicamento) throws SQLException {
        String sql = "INSERT INTO medicamento (codigo, nombre, tipo_presentacion) VALUES (?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medicamento.getCodigo());
            ps.setString(2, medicamento.getNombre());
            ps.setString(3, medicamento.getTipoPresentacion());
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

    public Medicamento findById(int id) throws SQLException {
        String sql = "SELECT * FROM medicamento WHERE id = ?";
        Medicamento medicamento = null;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medicamento = new Medicamento(
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("tipo_presentacion")
                    );
                }
            }
        }
        return medicamento;
    }

    public List<Medicamento> findAll() throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamento";
        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Medicamento medicamento = new Medicamento(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("tipo_presentacion")
                );
                lista.add(medicamento);
            }
        }
        return lista;
    }

    public Medicamento update(Medicamento medicamento) throws SQLException {
        String sql = "UPDATE medicamento SET nombre=?, tipo_presentacion=? WHERE codigo=?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getTipoPresentacion());
            ps.setString(3, medicamento.getCodigo());
            ps.executeUpdate();
        }
        return medicamento;
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM medicamento WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}

