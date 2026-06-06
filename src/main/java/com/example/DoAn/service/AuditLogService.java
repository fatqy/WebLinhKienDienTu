package com.example.DoAn.service;

import com.example.DoAn.model.AuditLog;
import com.example.DoAn.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String action, String entityName, String entityId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "system";
        
        String ipAddress = "unknown";
        String userAgent = "unknown";
        ServletRequestAttributes attribs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attribs != null) {
            HttpServletRequest request = attribs.getRequest();
            ipAddress = request.getRemoteAddr();
            userAgent = request.getHeader("User-Agent");
        }
        
        AuditLog log = new AuditLog(username, action, entityName, entityId, details, ipAddress, userAgent, "success");
        auditLogRepository.save(log);
    }

    public void logActionWithValues(String action, String entityName, String entityId, String details, String oldValue, String newValue, String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "system";
        
        String ipAddress = "unknown";
        String userAgent = "unknown";
        ServletRequestAttributes attribs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attribs != null) {
            HttpServletRequest request = attribs.getRequest();
            ipAddress = request.getRemoteAddr();
            userAgent = request.getHeader("User-Agent");
        }
        
        AuditLog log = new AuditLog(username, action, entityName, entityId, details, ipAddress, userAgent, status);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}
