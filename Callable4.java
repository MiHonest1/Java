import java.util.concurrent.Callable;

class Callable4 implements Callable<Double> {
    volatile double a;
    volatile double b;
    volatile double h;
    volatile double area = 0.0;

    public Callable4(double a, double b, double h) {
        this.a = a;
        this.b = b;
        this.h = h;
    }

    public synchronized Double call() {

        for (int i = 0; i < (b - a) / h; i++) {

            if (a + (i + 1) * h <= b) {
                area += h * (0.5 * (Math.tan(a + i * h) + Math.tan(a + (i + 1) * h)));
            } else {
                area += h * (0.5 * (Math.tan(a + i * h) + Math.tan(b)));
            }

        }
        return area;
    }

}