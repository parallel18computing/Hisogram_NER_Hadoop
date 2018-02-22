package Histogram;


public class HistogramDrawer {
    private final double[] freq;
    private final String[] data;
    private double max;
    private int tab;

    public HistogramDrawer(int n) {
        freq = new double[n];
        data = new String[n];
    }

    public void addData(int i, String n, double value) {
        freq[i]+=value;
        data[i] = n;

        if(freq[i] > max) max = freq[i];
        if(n.length() > tab) tab = n.length();
    }

    public void draw() {

        StdDraw.setPenColor(1,0,0);

        StdDraw.setYscale(-1, max + 1);
        StdDraw.setXscale(-1, tab * freq.length + 1);

        for(int i=0; i < freq.length; i++) {
            StdDraw.filledRectangle(i*tab + ((double)tab)/2 ,freq[i]/2,((double)tab)/2 , freq[i] / 2);
        }

    }

}
