package com.alex.web.node.pdm.service.impl;


import com.alex.web.node.pdm.model.LogMessage;
import com.alex.web.node.pdm.repository.LogMessageRepository;
import com.alex.web.node.pdm.service.LogMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LogMessageServiceImpl implements LogMessageService {
    private final LogMessageRepository logMessageRepository;

    @Override
    public void save(String message) {
        logMessageRepository.save(LogMessage.builder().message(message).build());
    }
}
