import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class BarChartData extends Application {
    Modeling calk;
    double a;
    LinkedList<Integer> listRV;
    private Label status = new Label();

    public BarChartData(LinkedList<Integer> listRV, double a) {
        this.a = a;
        this.listRV = listRV;
        this.calk = new Modeling(this.listRV, this.a);
        calk.finalGrupped();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Intervals");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("H");

        CategoryAxis xAxis2 = new CategoryAxis();
        xAxis.setLabel("Intervals");

        NumberAxis yAxis2 = new NumberAxis();
        yAxis.setLabel("H");

        BarChart<String, Number> barChart1 = new BarChart<>(xAxis, yAxis);
        BarChart<String, Number> barChart2 = new BarChart<>(xAxis2, yAxis2);
        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<String, Number>();
        double[] height = calk.getArray_of_height();
        double[] heightTheory = calk.getTheory();
        for (int i = 0; i < calk.getIntervals().length; i++) {
            dataSeries1.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), height[i]));
        }

        for (int i = 0; i < calk.getIntervals().length; i++) {
            dataSeries2.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), heightTheory[i]));
        }

        barChart1.getData().add(dataSeries1);
        barChart2.getData().add(dataSeries2);
        barChart1.setTitle("Poisson distribution");
        barChart2.setTitle("Theory Poisson distribution");
        HBox vbox = new HBox(barChart1, barChart2);

        primaryStage.setTitle("Histogram");
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.setHeight(450);
        primaryStage.setWidth(700);

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
