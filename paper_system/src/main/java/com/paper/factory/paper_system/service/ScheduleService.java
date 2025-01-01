package com.paper.factory.paper_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paper.factory.paper_system.model.Schedule;
import com.paper.factory.paper_system.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

   public List<Map<String, Object>> getSchedulesByMonth(int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    List<Schedule> schedules = scheduleRepository.findByStartDateBetween(start, end);
    List<Map<String, Object>> result = new ArrayList<>();

    for (Schedule schedule : schedules) {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("scheduleId", schedule.getScheduleId());
        scheduleData.put("orderId", schedule.getOrder().getOrderId()); // 返回 orderId
        scheduleData.put("stage", schedule.getStage());
        scheduleData.put("startDate", schedule.getStartDate());
        scheduleData.put("endDate", schedule.getEndDate());
        result.add(scheduleData);
    }
    return result;
}


    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Integer scheduleId, Schedule updatedSchedule) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("排程不存在"));
        existingSchedule.setStage(updatedSchedule.getStage());
        existingSchedule.setStartDate(updatedSchedule.getStartDate());
        existingSchedule.setEndDate(updatedSchedule.getEndDate());
        return scheduleRepository.save(existingSchedule);
    }

    public void deleteSchedule(Integer scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
