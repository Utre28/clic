package org.example.clic.controller;


import org.example.clic.model.AccessLog;
import org.example.clic.service.AccessLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @GetMapping
    public List<AccessLog> getAllAccessLogs() {
        return accessLogService.getAllAccessLogs();
    }

    @GetMapping("/{id}")
    public AccessLog getAccessLogById(@PathVariable Long id) {
        return accessLogService.getAccessLogById(id);
    }

    @PostMapping
    public AccessLog createAccessLog(@RequestBody AccessLog accessLog) {
        return accessLogService.createAccessLog(accessLog);
    }

    @DeleteMapping("/{id}")
    public void deleteAccessLog(@PathVariable Long id) {
        accessLogService.deleteAccessLog(id);
    }
}


