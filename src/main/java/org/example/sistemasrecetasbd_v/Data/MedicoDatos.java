package org.example.sistemasrecetasbd_v.Data;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.sistemasrecetasbd_v.Data.Conector.MedicoConector;
import org.example.sistemasrecetasbd_v.Data.Entity.MedicoEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MedicoDatos {
    private final Path xmlPath;
    private final JAXBContext ctx;
    private MedicoConector cache;

    public MedicoDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(MedicoConector.class, MedicoEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando JAXBContext", e);
        }
    }

    public synchronized MedicoConector load() {
        try {
            if (cache != null) return cache;

            if (!Files.exists(xmlPath)) {
                cache = new MedicoConector();
                save(cache); // crea archivo vacío
                return cache;
            }
            Unmarshaller u = ctx.createUnmarshaller();
            cache = (MedicoConector) u.unmarshal(xmlPath.toFile());
            if (cache.getMedicos() == null) cache.setMedicos(new java.util.ArrayList<>());
            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando XML: " + xmlPath, e);
        }
    }

    public synchronized void save(MedicoConector data) {
        try {
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            File out = xmlPath.toFile();
            File parent = out.getParentFile();
            if (parent != null) parent.mkdirs();

            // Debug: mostrar a consola el XML que se va a guardar // Para pruebas
            java.io.StringWriter sw = new java.io.StringWriter();
            m.marshal(data, sw);
            System.out.println("[DEBUG] XML contenido:\n" + sw);

            // Escribir al archivo
            m.marshal(data, out);

            cache = data;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando XML: " + xmlPath, e);
        }
    }

    public Path getXmlPath() { return xmlPath; }

}
