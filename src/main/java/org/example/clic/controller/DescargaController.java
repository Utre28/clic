package org.example.clic.controller;

import org.example.clic.service.PhotoService;
import org.example.clic.model.Photo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class DescargaController {
    private final PhotoService photoService;

    public DescargaController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/descargar-fotos")
    public ResponseEntity<InputStreamResource> descargarFotos(@RequestParam("fotoIds") String fotoIds) throws IOException {
        String[] ids = fotoIds.split(",");
        List<Photo> fotos = photoService.findAllById(ids);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Photo foto : fotos) {
                File file = new File("uploads" + File.separator + new File(foto.getUrl()).getName());
                if (file.exists()) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    Files.copy(file.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=fotos.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(baos.size())
                .body(resource);
    }
}