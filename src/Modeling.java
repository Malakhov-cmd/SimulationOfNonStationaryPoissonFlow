import java.util.Arrays;
import java.util.LinkedList;

public class Modeling {
    private double a;
    private double t;
    private final int N = 1000;
    private int countStep;
    private boolean hiSquare;
    private LinkedList<Integer> listRV = new LinkedList<>();
    private int nIntervals = 14;
    private Interval[] intervals = new Interval[nIntervals];
    private double[] theory;
    private double[] array_of_height;
    private double[] intervalTime;

    public Modeling(double a) {
        this.a = a;
        for (int i = 0; i < nIntervals; i++) {
            Interval interval = new Interval();
            intervals[i] = interval;
        }
    }


    public Modeling(LinkedList<Integer> listRV) {
        this.listRV = listRV;
        for (int i = 0; i < nIntervals; i++) {
            Interval interval = new Interval();
            intervals[i] = interval;
        }
    }

    public Modeling(LinkedList<Integer> listRV, double a) {
        this.listRV = listRV;
        for (int i = 0; i < nIntervals; i++) {
            Interval interval = new Interval();
            intervals[i] = interval;
        }
        this.a = a;
    }

    public LinkedList<Integer> getArrayRV() {
        return listRV;
    }

    public Interval[] getIntervals() {
        return intervals;
    }

    public double[] getArray_of_height() {
        return array_of_height;
    }

    public double[] getTheory() {
        return theory;
    }

    public boolean getHiSquade() {
        return hiSquare;
    }

    public double[] getIntervalTime() {
        return intervalTime;
    }

    public double getT() {
        return t;
    }

    private int poissonRandomNumber() {
        double L = Math.exp(-a);
        int k = 0;
        double p = 1;
        do {
            k = k + 1;
            double u = (Math.random());
            p = p * u;
        } while (p > L);
        return k - 1;
    }

    public void modelingProccess() {
        for (int i = 0; i < N; i++) {
            listRV.add(poissonRandomNumber());
        }
    }

    private int getMin() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < listRV.size(); i++) {
            if (listRV.get(i) < min) {
                min = listRV.get(i);
            }
        }
        return min;
    }

    private int getMax() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < listRV.size(); i++) {
            if (listRV.get(i) > max) {
                max = listRV.get(i);
            }
        }
        return max;
    }

    public void devideToIntervals() {
        int min = getMin();
        int max = getMax();
        int difference = max - min;
        double length_count_intervals = (double) difference / nIntervals;
        for (int index = 0; index < nIntervals; index++) {
            intervals[index].leftBorder = min + index * length_count_intervals;
            intervals[index].rightBorder = min + (index + 1) * length_count_intervals;
            for (int number : listRV) {
                if (number >= intervals[index].leftBorder && number < intervals[index].rightBorder) {
                    ++intervals[index].count;
                } else {
                    if (number >= intervals[index].leftBorder && number <= intervals[index].rightBorder && index + 1 == nIntervals) {
                        ++intervals[index].count;
                    }
                }
            }
        }
    }

    private void groupLowFrequencyInterval() {
        Interval[] spisok_intervals = Arrays.copyOf(intervals, intervals.length);
        for (int index1 = intervals.length - 1; index1 >= 0; --index1) {
            //перераспределение границ интервалов членов которых < 5
            if (spisok_intervals[index1].count < 17) {
                if (index1 > 0) {
                    //правой границы
                    spisok_intervals[index1 - 1].count += spisok_intervals[index1].count;
                    spisok_intervals[index1 - 1].rightBorder = spisok_intervals[index1].rightBorder;
                    spisok_intervals[index1].count = 0;
                } else {
                    for (int index2 = 0; index2 < intervals.length && spisok_intervals[index2].count < 17; ++index2) {
                        //левой границы
                        spisok_intervals[index2 + 1].count += spisok_intervals[index2].count;
                        spisok_intervals[index2 + 1].leftBorder = spisok_intervals[index2].leftBorder;
                        spisok_intervals[index2].count = 0;
                    }
                }
            }
        }
        intervals = Arrays.copyOf(spisok_intervals, spisok_intervals.length);

        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].count == 0) {
                Interval[] newIntevals = new Interval[intervals.length - 1];
                System.arraycopy(intervals, 0, newIntevals, 0, i);
                System.arraycopy(intervals, i + 1, newIntevals, i, intervals.length - i - 1);
                intervals = Arrays.copyOf(newIntevals, newIntevals.length);
                i = 0;
            }
        }

        array_of_height = new double[intervals.length];
        for (int index = 0; index < intervals.length; index++) {
            double hiegth = (double) intervals[index].count / N;
            array_of_height[index] = hiegth;
        }
        double sum = 0;
        for (double num : array_of_height) {
            sum += num;
        }
        System.out.println("Practical probability:" + sum);
    }

    private static int calculateFactorial(int n) {
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result = result * i;
        }
        return result;
    }

    public void getTimeToIntervals() {
        double min = getMin();
        double max = getMax();
        double len = intervals.length;
        double dif = Math.round((max - min) / len);

        intervalTime = new double[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            intervalTime[i] = getTimeToInterval(intervals[i].count, (int) dif);
            countStep = countStep + intervals[i].count;
            System.out.println("Interval: " + i + " Value: " + intervalTime[i]);
        }
    }

    private double getTimeToInterval(int count, int dif) {
        double timeInterval = 0;
        for (int i = 0; i < count; i++) {
            double intervalT = (-timeInterval) + (1 / a) * Math.sqrt(Math.pow(a * timeInterval, 2) - 2 * a * Math.log(getProbability(listRV.get(i + countStep), dif)));
            timeInterval = timeInterval + intervalT;
        }
        t = t + timeInterval;
        return timeInterval;
    }

    private double getProbability(int example, int dif) {
        double probability = 0;
        for (int i = 0; i < intervals.length; i++) {
            if (i * dif >= example) {
                return probability = array_of_height[i];
            }
        }
        return probability = array_of_height[intervals.length - 1];
    }

    public double getTime() {
        double min = getMin();
        double max = getMax();
        double len = intervals.length;
        double dif = Math.round((max - min) / len);
        t = 0;
        for (int i = 0; i < N; i++) {
            double intervalT = (-t) + (1 / a) * Math.sqrt(Math.pow(a * t, 2) - 2 * a * Math.log(getProbability(listRV.get(i), (int) dif)));
            t = t + intervalT;
        }
        return t;
    }

    private strictfp void theoryH() {
        System.out.println("total time is: " + t);
        theory = new double[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            theory[i] = (Math.pow(a, i) * Math.pow(Math.E, -a)) / calculateFactorial(i);
        }
    }

    private boolean HiSquare() {
        double sum = 0;
        System.out.println("len " + intervals.length);
        for (int i = 0; i < intervals.length; i++) {
            sum = sum + (Math.pow(intervals[i].count - N * theory[i], 2)) / (N * theory[i]);
            double sumI = (Math.pow(intervals[i].count - N * theory[i], 2)) / (N * theory[i]);
            System.out.println("sum i: " + sumI);
        }
        System.out.println("sum: " + sum);
        if (sum < 21.4) {
            hiSquare = true;
        } else {
            hiSquare = false;
        }
        return hiSquare;
    }

    public void finalGrupped() {
        devideToIntervals();
        groupLowFrequencyInterval();
        getTimeToIntervals();
        theoryH();
        HiSquare();
    }
}
