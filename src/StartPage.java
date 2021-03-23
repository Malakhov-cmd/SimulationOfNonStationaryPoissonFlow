import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartPage extends Application {
    private double a;
    private Label status = new Label();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Button createHistogram;
    private Button getSolution;
    private Button getTime;
    private Task<Integer> task;
    private TextArea textArea = new TextArea();
    private File file;
    private Modeling calk;
    private LinkedList<Integer> listRV;

    public void start(Stage stage) {
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> {
            write(stage);
        });
        saveItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));

        fileMenu.getItems().addAll(saveItem, new SeparatorMenuItem(), exitItem);

        MenuItem aboutProgramItem = new MenuItem("_About this program");
        aboutProgramItem.setOnAction(event ->
        {
            TextArea areaInfo = new TextArea("The program calculates the Poisson distribution " + "\n" +
                    "Divides into intervals and groups them together" + "\n" +
                    "Builds histograms and makes a decision about the" + "\n" +
                    "permissibility using the Pearson Chi square criterion");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Work information");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });

        MenuItem aboutProgramer = new MenuItem("About _programer");
        aboutProgramer.setOnAction(event ->
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Malakhov Georgey, 6302");
            alert.showAndWait();
        });

        Menu helpMenu = new Menu("_Help", null, aboutProgramItem, aboutProgramer);

        Button enterParametr = new Button("Enter parametrs");
        enterParametr.setMinSize(125, 35);
        enterParametr.setOnAction(
                event -> {
                    ChoiceDialog<Double> dialog = new ChoiceDialog<Double>(0.5, 2.0, 5.0, 7.5, 10.0);
                    dialog.setHeaderText("Pick a parametr.");
                    dialog.showAndWait().ifPresentOrElse(
                            result -> a = dialog.getSelectedItem(),
                            () -> textArea.appendText("Canceled\n"));
                    calk = new Modeling(a);
                    calk.modelingProccess();
                    listRV = calk.getArrayRV();
                    for (int i = 0; i < listRV.size(); i++) {
                        textArea.appendText(listRV.get(i) + "\n");
                    }
                    createHistogram.setDisable(false);
                    getSolution.setDisable(false);
                    getTime.setDisable(false);
                });
        Button clearTextArea = new Button("Clear text area");
        clearTextArea.setMinSize(125, 35);
        clearTextArea.setOnAction(event -> {
            textArea.setText("");
        });

        createHistogram = new Button("Create histogram");
        createHistogram.setMinSize(125, 35);
        createHistogram.setDisable(true);
        createHistogram.setOnAction(event -> {
            if (Objects.equals(textArea.getText(), "")) {
                createHistogram.setDisable(true);
            } else {
                BarChartData chart = new BarChartData(listRV, a);
                myLaunch(chart);
            }
        });

        getSolution = new Button("Get Solution");
        getSolution.setMinSize(125, 35);
        getSolution.setDisable(true);
        getSolution.setOnAction(event -> {
            calk.finalGrupped();
            if (calk.getHiSquade()) {
                textArea.appendText("Accepted");
            } else {
                textArea.appendText("Disproved");
            }
        });

        getTime = new Button("Get time info");
        getTime.setMinSize(125, 35);
        getTime.setDisable(true);
        getTime.setOnAction(event -> {
            if (Objects.equals(textArea.getText(), "")) {
                getTime.setDisable(true);
            } else {
                TimeInterval chart = new TimeInterval(listRV, a);
                myLaunch(chart);
            }
        });

        final double rem = new Text("").getLayoutBounds().getHeight();
        VBox buttons = new VBox(0.8 * rem, enterParametr, clearTextArea, createHistogram, getTime, getSolution);
        buttons.setPadding(new Insets(0.8 * rem));

        MenuBar bar = new MenuBar(fileMenu, helpMenu);
        HBox horizontal = new HBox(textArea, buttons);
        VBox root = new VBox(bar, horizontal, status);
        stage.setScene(new Scene(root));
        stage.setTitle("MenuTest");
        stage.show();
    }

    private void write(Stage stage) {
        if (task != null) return;
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("E:\\MIS"));
        file = chooser.showOpenDialog(stage);
        if ((file == null) || (Objects.equals(textArea.getText(), ""))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This looks really bad.");
            alert.showAndWait();
        }
        task = new Task<>() {
            public Integer call() {
                int lines = 0;
                try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file))) {
                    String line = textArea.getText();
                    out.write(line);
                } catch (IOException e) {
                    throw new UncheckedIOException(null, e);
                }
                return lines;
            }
        };
        executor.execute(task);
        task.setOnRunning(event ->
        {
            status.setText("Running");
            status.textProperty().bind(task.messageProperty());
        });
        task.setOnFailed(event ->
        {
            status.textProperty().unbind();
            status.setText("Failed due to " + task.getException());
            task = null;
        });
        task.setOnCancelled(event ->
        {
            status.textProperty().unbind();
            status.setText("Canceled");
            task = null;
        });
        task.setOnSucceeded(event ->
        {
            status.textProperty().unbind();
            status.setText("Done writing");
            task = null;
        });
    }

    public static void myLaunch(Application applicationClass) {
        Platform.runLater(() -> {
            try {
                Application application = applicationClass;
                Stage primaryStage = new Stage();
                application.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
