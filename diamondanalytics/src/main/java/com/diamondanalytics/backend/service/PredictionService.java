package com.diamondanalytics.backend.service;

import com.diamondanalytics.backend.dto.prediction.GamePredictionDto;
import com.diamondanalytics.backend.model.Game;

public interface PredictionService {
    GamePredictionDto getPredictionForGame(Game game);
    void generatePredictionsForTodaysGames();
}