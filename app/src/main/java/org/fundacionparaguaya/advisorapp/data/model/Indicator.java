package org.fundacionparaguaya.advisorapp.data.model;

import android.util.Log;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

/**
 * An Indicator is asked during a survey. Each indicator has a red, yellow, and green level. When the family takes the
 * survey, they will choose one of those levels.
 */

public class Indicator {
    private String name;
    private String dimension;
    private List<IndicatorOption> options;

    public Indicator(String name, String dimension) {
        this(name, dimension, null);
    }

    public Indicator(String name, String dimension, List<IndicatorOption> options) {
        this.name = name;
        this.dimension = dimension;
        setOptions(options);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return IndicatorNameResolver.resolve(name);
    }

    public String getDimension() {
        return dimension;
    }

    public List<IndicatorOption> getOptions() {
        return options;
    }

    public void setOptions(List<IndicatorOption> options) {
        this.options = options;
        if (options == null) {
            return;
        }

        for (IndicatorOption option : options) { // add references to this indicator
            option.setIndicator(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Indicator that = (Indicator) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(dimension, that.dimension)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(67, 19)
                .append(name)
                .append(dimension)
                .append(options)
                .toHashCode();
    }

    /**
     * A temporary utility for resolving indicator names.
     */
    private static class IndicatorNameResolver {
        private static final String TAG = "IndicatorNameResolver";

        private static Map<String, String> mNames = new HashMap<>();
        static {
            if (Locale.getDefault().getLanguage().equals("es")) {
                map("income", "Nivel de Ingreso");
                map("properKitchen", "Cocina Adecuada");
                map("awarenessOfNeeds", "Conocimiento de las Necesidades");
                map("documentation", "Documentación");
                map("separateBed", "Cama Separada");
                map("alimentation", "Alimentación");
                map("separateBedrooms", "Habitaciones Separadas");
                map("selfEsteem", "Autoestima");
                map("security", "Seguridad");
                map("autonomyDecisions", "Autonomía de Decisión");
                map("phone", "Teléfono");
                map("influenceInPublicSector", "Influencia Pública");
                map("socialCapital", "Capital Social");
                map("safeBathroom", "Baño Seguro");
                map("informationAccess", "Acceso a la Información");
                map("middleEducation", "Educación Media");
                map("drinkingWaterAccess", "Acceso al Agua Potable");
                map("safeHouse", "Casa Segura");
                map("readAndWrite", "Lee y Escribe");
                map("refrigerator", "Refrigerador");
                map("nearbyHealthPost", "Centro Médico Cercano");
                map("electricityAccess", "Acceso a la Electricidad");
                map("garbageDisposal", "Basurero");
            } else {
                map("income", "Income");
                map("properKitchen", "Proper Kitchen");
                map("awarenessOfNeeds", "Awareness Of Needs");
                map("documentation", "Documentation");
                map("separateBed", "Separate Bed");
                map("alimentation", "Alimentation");
                map("separateBedrooms", "Separate Bedrooms");
                map("selfEsteem", "Self-Esteem");
                map("security", "Security");
                map("autonomyDecisions", "Decision Autonomy");
                map("phone", "Phone");
                map("influenceInPublicSector", "Influence in Public Sector");
                map("socialCapital", "Social Capital");
                map("safeBathroom", "Safe Bathroom");
                map("informationAccess", "Information Access");
                map("middleEducation", "Middle Education");
                map("drinkingWaterAccess", "Drinking Water Access");
                map("safeHouse", "Safe House");
                map("readAndWrite", "Read and Write");
                map("refrigerator", "Refrigerator");
                map("nearbyHealthPost", "Nearby Health Post");
                map("electricityAccess", "Electricity Access");
                map("garbageDisposal", "Garbage Disposal");
            }
        }

        private static void map(String indicator, String name) {
            mNames.put(indicator, name);
        }

        private static String resolve(String indicator) {
            if (mNames.containsKey(indicator)) {
                return mNames.get(indicator);
            }
            Log.w(TAG, format("resolve: Don't have a mapping for %s!", indicator));
            String generated = titleCase(indicator);
            map(indicator, generated);
            return generated;
        }

        /**
         * Maps a indicator to a "pretty" name.
         */
        private static String titleCase(String indicator) {
            StringBuilder result = new StringBuilder();
            CharacterIterator iterator = new StringCharacterIterator(indicator);
            for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(c));
                c = iterator.next();
                while (Character.isLowerCase(c)) {
                    result.append(c);
                    c = iterator.next();
                }
                iterator.previous();
            }
            return result.toString();
        }
    }
}
