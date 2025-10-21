package org.example.sistemasrecetasbd_v.Data;

import org.example.sistemasrecetasbd_v.Model.Clases.Farmaceuta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmaceutaDatos {

    public Farmaceuta insert(Farmaceuta farmaceuta) throws SQLException {
        String sql = "INSERT INTO farmaceuta (identificacion, nombre, clave) VALUES (?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, farmaceuta.getIdentificacion());
            ps.setString(2, farmaceuta.getNombre());
            ps.setString(3, farmaceuta.getClave());
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

    public Farmaceuta findById(int id) throws SQLException {
        String sql = "SELECT * FROM farmaceuta WHERE id = ?";
        Farmaceuta farmaceuta = null;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    farmaceuta = new Farmaceuta(
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("clave")
                    );
                }
            }
        }
        return farmaceuta;
    }

    public List<Farmaceuta> findAll() throws SQLException {
        List<Farmaceuta> lista = new ArrayList<>();
        String sql = "SELECT * FROM farmaceuta ORDER BY id";

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Farmaceuta farmaceuta = new Farmaceuta(
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getString("clave")
                );
                lista.add(farmaceuta);
            }
        }
        return lista;
    }

    public Farmaceuta update(Farmaceuta farmaceuta) throws SQLException {
        String sql = "UPDATE farmaceuta SET nombre=?, clave=? WHERE identificacion=?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, farmaceuta.getNombre());
            ps.setString(2, farmaceuta.getIdentificacion());
            ps.setString(3, farmaceuta.getClave());
            ps.executeUpdate();
        }
        return farmaceuta;
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM farmaceuta WHERE id = " + id;
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}
