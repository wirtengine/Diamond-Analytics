package com.diamondanalytics.backend.utils;

import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.model.Odds;
import com.diamondanalytics.backend.repository.OddsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    @Autowired
    private OddsRepository oddsRepository;

    public String buildPredictionPrompt(Game game) {
        StringBuilder sb = new StringBuilder();

        sb.append("Eres un experto analista de béisbol y apuestas deportivas. ");
        sb.append("Analiza el siguiente partido de MLB y proporciona una predicción estructurada.\n\n");

        sb.append("PARTIDO:\n");
        sb.append("- Visitante: ").append(game.getAwayTeam().getName()).append("\n");
        sb.append("- Local: ").append(game.getHomeTeam().getName()).append("\n");
        sb.append("- Hora de inicio (UTC): ").append(game.getStartTime()).append("\n\n");

        // Obtener las odds más recientes (promedio de las principales casas)
        List<Odds> oddsList = oddsRepository.findByGameOrderByTimestampDesc(game);
        if (!oddsList.isEmpty()) {
            Odds latestOdds = oddsList.get(0);
            sb.append("CUOTAS ACTUALES (Moneyline):\n");
            if (latestOdds.getHomeMoneyline() != null) {
                sb.append("- ").append(game.getHomeTeam().getName())
                        .append(": ").append(String.format("%.2f", latestOdds.getHomeMoneyline())).append("\n");
            }
            if (latestOdds.getAwayMoneyline() != null) {
                sb.append("- ").append(game.getAwayTeam().getName())
                        .append(": ").append(String.format("%.2f", latestOdds.getAwayMoneyline())).append("\n");
            }
        } else {
            sb.append("(No hay cuotas disponibles)\n");
        }

        sb.append("\n");
        sb.append("Basándote en conocimiento general de MLB, tendencias recientes (si las conoces) ");
        sb.append("y las cuotas proporcionadas, genera un análisis en el siguiente formato JSON:\n");
        sb.append("{\n");
        sb.append("  \"homeWinProbability\": <número entre 0 y 1>,\n");
        sb.append("  \"awayWinProbability\": <número entre 0 y 1>,\n");
        sb.append("  \"recommendedBet\": \"HOME\" o \"AWAY\",\n");
        sb.append("  \"confidenceScore\": <número entre 0 y 1>,\n");
        sb.append("  \"analysis\": \"<explicación breve en español, máximo 150 palabras>\"\n");
        sb.append("}\n");
        sb.append("Responde ÚNICAMENTE con el JSON, sin texto adicional.");

        return sb.toString();
    }
}