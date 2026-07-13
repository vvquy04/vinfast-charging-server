package com.vanquy.evcserver.service;

import com.vanquy.evcserver.model.ChargingSession;

import java.util.List;

public interface ChargingSessionService {
    ChargingSession startSession(Long userId, Long stationId, String connectorType, Double powerKw);
    ChargingSession stopSession(Long sessionId, Double energyCharged);
    ChargingSession getActiveSession(Long userId);
    List<ChargingSession> getSessionHistory(Long userId);
}
