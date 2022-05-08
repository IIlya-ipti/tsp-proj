package Project.modules.evolution.genome;

/**
 * @param p - период [1 * 10^-3, 10]
 * @param o - отклонение [-pi, pi]
 * @param m - нижняя амплитуда [-piR/2, M], где R - длинна ноги
 * @param M - вверхняя амплитуда [-piR/2, piR/2] где R - длинна ноги
 *          M > m
 */

public class GenomeConfig {
    public static final Double MAX_VALUE_P=10.;
    public static final Double MIN_VALUE_P=1.;

    public static final Double MAX_VALUE_O=Math.PI;
    public static final Double MIN_VALUE_O=-Math.PI;

    public static final Double MAX_VALUE_m=0.;
    public static final Double MIN_VALUE_m=-0.11;

    public static final Double MAX_VALUE_M=0.11;

    public static final int POPULATION_SIZE = 1;
    public static final int MAX_EPOCH = 10;

    public static final Double H_DIFF_WEIGHT = 0.1;
    public static final Double MAX_TIME =  3600.;
    public static final Double TIME_WEIGHT = 0.1;

    public static final Integer LEG_LENGTH = 5;

    public static final int PARAMS_COUNTS = 8;
    public static final int LEG_PARAMS_COUNTS = 4;

    public static Character[] LEG_LIST_PARAMS = {'p', 'o', 'M', 'm'};

}