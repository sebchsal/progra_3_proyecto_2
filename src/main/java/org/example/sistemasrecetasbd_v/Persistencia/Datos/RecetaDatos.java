package org.example.sistemasrecetasbd_v.Persistencia.Datos;

import jakarta.xml.bind.*;
import org.example.sistemasrecetasbd_v.Persistencia.Conector.RecetaConector;
import org.example.sistemasrecetasbd_v.Persistencia.Entity.RecetaEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class RecetaDatos {
    private final Path xmlPath;
    private final JAXBContext ctx;
    private RecetaConector cache;

    public RecetaDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(RecetaConector.class, RecetaEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando JAXBContext", e);
        }
    }

    public synchronized RecetaConector load() {
        try {
            if (cache != null) return cache;

            if (!Files.exists(xmlPath)) {
                cache = new RecetaConector();
                save(cache);
                return cache;
            }
            Unmarshaller u = ctx.createUnmarshaller();
            cache = (RecetaConector) u.unmarshal(xmlPath.toFile());
            if (cache.getRecetas() == null) cache.setRecetas(new java.util.ArrayList<>());
            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando XML: " + xmlPath, e);
        }
    }

    public synchronized void save(RecetaConector data) {
        try {
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            File out = xmlPath.toFile();
            File parent = out.getParentFile();
            if (parent != null) parent.mkdirs();

            m.marshal(data, out);
            cache = data;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando XML: " + xmlPath, e);
        }
    }

    public Path getXmlPath() { return xmlPath; }
}