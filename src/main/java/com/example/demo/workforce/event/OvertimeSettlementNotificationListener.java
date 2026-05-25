package com.example.demo.workforce.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OvertimeSettlementNotificationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OvertimeSettlementNotificationListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendSettlementNotification(OvertimeSettledEvent event) {
        try {
            LOGGER.info(
                    "SMS queued for worker={} phone={} month={} amount={}",
                    event.getWorkerId(),
                    event.getPhone(),
                    event.getMonth(),
                    event.getTotalAmount()
            );
        } catch (RuntimeException exception) {
            LOGGER.warn("Settlement committed but SMS notification failed; queueing retry for worker={}", event.getWorkerId(), exception);
        }
    }
}
