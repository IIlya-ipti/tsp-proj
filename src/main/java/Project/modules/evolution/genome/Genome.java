package Project.modules.evolution.genome;

import Project.modules.utils.CustomRandom;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
public class Genome {
    LegGenome left;
    LegGenome right;

    private static LegGenome setupLeg() {
        double mValue = CustomRandom.inRange(
                GenomeConfig.MIN_VALUE_m * GenomeConfig.LEG_LENGTH,
                GenomeConfig.MAX_VALUE_m * GenomeConfig.LEG_LENGTH
        );
        return new LegGenome(
                CustomRandom.inRange(GenomeConfig.MIN_VALUE_P, GenomeConfig.MAX_VALUE_P),
                CustomRandom.inRange(GenomeConfig.MIN_VALUE_O, GenomeConfig.MAX_VALUE_O),
                CustomRandom.inRange(mValue, GenomeConfig.MAX_VALUE_M * GenomeConfig.LEG_LENGTH),
                mValue
        );
    }

    public Genome() {
        left = setupLeg();
        right = setupLeg();
    }

    public Map<String, LegGenome> params() {
        Map<String, LegGenome> params = new HashMap<>();
        params.put("left", left);
        params.put("right", right);
        return params;
    }
}