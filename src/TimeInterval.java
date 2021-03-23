import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class TimeInterval extends Application {
    Modeling calk;
    double a;
    LinkedList<Integer> listRV;
    private Label status = new Label();

    public TimeInterval(LinkedList<Integer> listRV, double a) {
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
        yAxis.setLabel("Sec");


        BarChart<String, Number> barChart1 = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();

        double[] timeInterval = calk.getIntervalTime();
        for (int i = 0; i < calk.getIntervals().length; i++) {
            dataSeries1.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), timeInterval[i]));
        }

        status.setText("" +
                "                   Average duration of system operation: " + calk.getTime() + " sec");
        status.setMinHeight(55);

        barChart1.getData().add(dataSeries1);
        barChart1.setTitle("Time on value");
        VBox vbox = new VBox(barChart1, status);

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
