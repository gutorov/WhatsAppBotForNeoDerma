package com.ivan_degtev.whatsappbotforneoderma.config.interfaces;

import com.ivan_degtev.whatsappbotforneoderma.component.DailyScheduler;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartedData {
    private DailyScheduler dailyScheduler;

    @Tool("У вас есть первичные данные об именах текущих сотрудников компании, вы можете использовать" +
            "эту информацию в разговоре с клиентом")
    public void readInfoForEmployee() {
        dailyScheduler.getEmployeeDTOList();
    }
}
