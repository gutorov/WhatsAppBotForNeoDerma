package com.ivan_degtev.whatsappbotforneoderma.controller.llm;

import com.ivan_degtev.whatsappbotforneoderma.service.ai.FineTuningService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/llm")
public class FineTuningController {

    private final FineTuningService fineTuningService;
    private static final String FILE_PATH_FOR_TRAINING = "src/main/resources/assistant-training.jsonl";


    @PostMapping(path = "/upload_training_file")
    public ResponseEntity<String> uploadTrainingFile() {
        String  fileIdForTuningCustomModel = fineTuningService.uploadTrainingFile(FILE_PATH_FOR_TRAINING);
        return ResponseEntity.ok("Создание файла на опен аи для дальнейшей настройки с id " + fileIdForTuningCustomModel);
    }

    @PostMapping(path = "/create_fine_tuning_job")
    public ResponseEntity<?> createFineTuningJob(@RequestParam String fileId) {
        try {
            ResponseEntity<Map> response = fineTuningService.createFineTuningJob(fileId);

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
            ResponseEntity<Map> response = fineTuningService.getStatusFineTuningJob(fineTuningJobId);
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
