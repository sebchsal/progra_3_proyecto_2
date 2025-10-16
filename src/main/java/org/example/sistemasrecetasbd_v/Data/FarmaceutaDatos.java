package org.example.sistemasrecetasbd_v.Data;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.sistemasrecetasbd_v.Data.Conector.FarmaceutaConector;
import org.example.sistemasrecetasbd_v.Data.Entity.FarmaceutaEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FarmaceutaDatos {
    private final Path xmlPath;
    private final JAXBContext ctx;
    private FarmaceutaConector cache;

    public FarmaceutaDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(FarmaceutaConector.class, FarmaceutaEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando JAXBContext", e);
        }
    }

    public synchronized FarmaceutaConector load() {
        try {
            if (cache != null) return cache;

            if (!Files.exists(xmlPath)) {
                cache = new FarmaceutaConector();
                save(cache);
                return cache;
            }
            Unmarshaller u = ctx.createUnmarshaller();
            cache = (FarmaceutaConector) u.unmarshal(xmlPath.toFile());
            if (cache.getFarmaceutas() == null) cache.setFarmaceutas(new java.util.ArrayList<>());
            return cache;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando XML: " + xmlPath, e);
        }
    }

    public synchronized void save(FarmaceutaConector data) {
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
