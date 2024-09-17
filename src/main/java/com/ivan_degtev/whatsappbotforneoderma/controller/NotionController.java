package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.service.impl.NotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/notion")
public class NotionController {

    private final NotionService notionService;

    /**
     * Для поиска всех необходимых файлов, id нужнызх страниц указаны в сервисе
     * @return
     */
    @GetMapping(path = "/all_files")
    public ResponseEntity<String> getFilesFromAllPages() {
        String response = notionService.getFilesFromAllPages().toString();
        return ResponseEntity
                .ok()
                .body(response);
    }

    /**
     * Ручка для комплексного скачивания всех файлов из заданной в txt файле страниц notion - сначала происходит парсинг их id и url, далее - скачивание в
     * локлаьную папку resources/files
     */
    @PostMapping(path = "/download_all_files")
    public ResponseEntity<String> downloadAllFiles() {
        List<String> results = notionService.downloadAllFiles();
        String responseBody = String.join("\n", results);
        return ResponseEntity
                .ok()
                .body("Результаты загрузки файлов:\n" + responseBody);
    }
}
