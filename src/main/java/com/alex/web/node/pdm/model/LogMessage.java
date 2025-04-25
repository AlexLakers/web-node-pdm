package com.alex.web.node.pdm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * This class is an object model for database table 'log_message'.
 */

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "log_message")
public class LogMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String message;
}
