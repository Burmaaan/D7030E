package src.main.domain;


import src.main.data.CardData;
import src.main.domain.card.*;

public class CardFactory {

    public static Card createCard(CardData data) {
        // 1. Check for Center Cards (by exact name)
        if (data.name != null) {
            if (data.name.equalsIgnoreCase("Road")) {
                return new RoadCard(data);
            }
            if (data.name.equalsIgnoreCase("Settlement")) {
                return new SettlementCard(data);
            }
            if (data.name.equalsIgnoreCase("City")) {
                return new CityCard(data);
            }
        }

        // 2. Check for Regions (by type)
        if (data.type != null && data.type.equalsIgnoreCase("Region")) {
            return new RegionCard(data);
        }

        // 3. Check for Events (by placement)
        if (data.placement != null && data.placement.equalsIgnoreCase("Event")) {
            return new EventCard(data);
        }

        // 4. Check for Actions (by type)
        if (data.type != null && data.type.toLowerCase().contains("action")) {
            return new ActionCard(data);
        }

        // 5. Check for Expansions (Settlement/City placement)
        if (data.placement != null &&
                data.placement.toLowerCase().contains("settlement/city")) {

            // 5a. Buildings
            if (data.type != null && data.type.equalsIgnoreCase("Building")) {
                return new BuildingCard(data);
            }

            // 5b. Units
            if (data.type != null && data.type.toLowerCase().contains("unit")) {
                return new UnitCard(data);
            }
        }

        // Default fallback: treat as ActionCard
        return new ActionCard(data);
    }
}
