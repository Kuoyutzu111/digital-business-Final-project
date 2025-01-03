package com.paper.factory.paper_system.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paper.factory.paper_system.model.Schedule;
import com.paper.factory.paper_system.service.ScheduleService;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    @GetMapping
public List<Map<String, Object>> getSchedules(@RequestParam int year, @RequestParam int month) {
    return scheduleService.getSchedulesByMonth(year, month);
}


    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }

    @PutMapping("/{scheduleId}")
    public Schedule updateSchedule(@PathVariable Integer scheduleId, @RequestBody Schedule schedule) {
        return scheduleService.updateSchedule(scheduleId, schedule);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable Integer scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }
}
