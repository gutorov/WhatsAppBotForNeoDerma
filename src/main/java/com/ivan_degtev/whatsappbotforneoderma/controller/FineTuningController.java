package com.ivan_degtev.whatsappbotforneoderma.controller;

import com.ivan_degtev.whatsappbotforneoderma.service.ai.FineTuning;
import dev.langchain4j.data.message.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/llm")
public class FineTuningController {

    private final FineTuning fineTuning;
    private static final String filePath = "testTuning2.jsonl";


    @PostMapping(path = "/upload_training_file")
    public ResponseEntity<String> uploadTrainingFile() {
        String  fileIdForTuningCustomModel = fineTuning.uploadTrainingFile(filePath);
        return ResponseEntity.ok("Создание файла на опен аи для дальнейшей настройки с id " + fileIdForTuningCustomModel);
    }

    @PostMapping(path = "/create_fine_tuning_job")
    public ResponseEntity<?> createFineTuningJob(@RequestParam String fileId) {
        try {
            ResponseEntity<Map> response = fineTuning.createFineTuningJob(fileId);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Ошибка создания задачи по тонкой настройке: " + response.getBody());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка: " + e.getMessage());
        }
    }
    @GetMapping(path = "/get_status_fine_tuning_job")
    public ResponseEntity<?> getStatusFineTuningJob(@RequestParam String fineTuningJobId) {
        try {
            ResponseEntity<Map> response = fineTuning.getStatusFineTuningJob(fineTuningJobId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Ошибка получения данных о задачи по тонкой настройке: " + response.getBody());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка: " + e.getMessage());
        }
    }
}
