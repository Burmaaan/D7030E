package src.main.data;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class CardLoader {

    public List<CardData> loadCardsFromJson(String jsonPath) throws IOException {
        List<CardData> cards = new ArrayList<>();

        try (FileReader fr = new FileReader(jsonPath)) {
            JsonElement root = JsonParser.parseReader(fr);
            if (!root.isJsonArray()) {
                throw new IOException("Expected JSON array");
            }

            JsonArray arr = root.getAsJsonArray();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;

                JsonObject o = el.getAsJsonObject();
                CardData card = parseCard(o);

                int copies = getInt(o, "number", 1);
                for (int i = 0; i < copies; i++) {
                    cards.add(card);
                }
            }
        }

        return cards;
    }

    public List<CardData> filterByTheme(List<CardData> cards, String theme) {
        List<CardData> filtered = new ArrayList<>();
        for (CardData card : cards) {
            if (card.theme != null &&
                    card.theme.toLowerCase().contains(theme.toLowerCase())) {
                filtered.add(card);
            }
        }
        return filtered;
    }

    private CardData parseCard(JsonObject o) {
        return new CardData(
                getString(o, "name"),
                getString(o, "theme"),
                getString(o, "type"),
                getString(o, "germanName"),
                getString(o, "placement"),
                getString(o, "oneOf"),
                getString(o, "cost"),
                getString(o, "victoryPoints"),
                getString(o, "CP"),
                getString(o, "SP"),
                getString(o, "FP"),
                getString(o, "PP"),
                getString(o, "LP"),
                getString(o, "KP"),
                getString(o, "Requires"),
                getString(o, "cardText"),
                getString(o, "protectionOrRemoval"),
                getInt(o, "number", 1),
                getInt(o, "sunSymbol", 0)
        );
    }

    private String getString(JsonObject o, String key) {
        if (!o.has(key)) return null;
        JsonElement e = o.get(key);
        return (e == null || e.isJsonNull()) ? null : e.getAsString();
    }

    private int getInt(JsonObject o, String key, int def) {
        if (!o.has(key)) return def;
        try {
            return o.get(key).getAsInt();
        } catch (Exception e) {
            return def;
        }
    }
}
