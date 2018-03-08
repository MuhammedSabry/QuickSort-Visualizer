package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class Main extends Application {

    private static double width = 800, height = 600;
    private static final double MAX_WIDTH = width - 150;
    private static final double MAX_HEIGHT = height - 100;
    private static final int SLEEP_PERIOD = 150;
    private static final int FIRST_SLEEP_PERIOD = 500;
    private static final TextArea textArea = new TextArea();
    private static double MAX;
    private static GraphicsContext context;
    private static TimerTask task;
    private static List<Double> list = new ArrayList<>();
    private static double triangleWidth;
    private static double factor;
    private static int WIDTH_OFFSET = 75;
    private static ExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void start(Stage primaryStage) {


        //Setting up the screen
        primaryStage.setTitle("QuickSort Visualizer");
        Canvas canvas = new Canvas(width, height);
        context = canvas.getGraphicsContext2D();
        Group rootGroup = new Group();

        //setting boundaries for the textArea
        textArea.setLayoutX(8);
        textArea.setLayoutY(8);
        textArea.setMaxWidth(60);
        textArea.setMinHeight(MAX_HEIGHT);

        //first time launching the app we random some values and print them
        random();
        print();

        //setting the Sort button
        Button button = new Button();
        button.setLayoutX(width / 2);
        button.setLayoutY(height - 30);
        button.setText("Start");

        //Showing everything
        rootGroup.getChildren().addAll(canvas, button, textArea);
        primaryStage.setScene(new Scene(rootGroup));
        primaryStage.show();

        //Responding to clicks on the Sort button
        button.setOnMouseClicked(event -> {
            if (button.getText().equals("Start")) {

                button.setText("Stop");

                executorService = new ScheduledThreadPoolExecutor(1);
                //reading values from the box
                list = new ArrayList<>();
                read();

                //Things to do before any sort
                max();
                triangleWidth = MAX_WIDTH / list.size();
                factor = (MAX_HEIGHT - 50) / MAX;

                //finally executing the task
                executorService.execute(task);
            } else {
                button.setText("Start");
                //terminating the executorService and initializing a new one
                executorService.shutdownNow();
            }
        });

        //the task to be run
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    quickSort(list, 0, list.size());
                } catch (InterruptedException ignored) {
                } finally {
                    paint(list, list.size(), list.size(), -1, -2);
                }
            }
        };
    }

    /**
     * Randoms 20 integers and puts it in the list to sort
     * just gonna run once when the app first launched
     */
    private static void random() {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            double temp = (double) random.nextInt(100);
            if (temp != 0)
                list.add(temp);
            else
                i--;
        }
    }

    /**
     * Prints the right numbers order from the ArrayList into the textAreay
     */
    private static void print() {

        textArea.clear();
        for (Double aList : list) {
            textArea.appendText(String.valueOf(aList) + "\n");
        }
    }

    /**
     * reads the numbers from the textArea and puts it inside the list
     * if no number in the line
     * it puts 0 instead
     */
    private static void read() {
        Scanner scanner = new Scanner(textArea.getText());
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (isNumeric(line))
                list.add(Double.valueOf(line));
        }
    }

    /**
     * checks if the given string is a number or not
     */
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * the whole visualization and colorful things happens here
     */
    private static void paint(List<Double> integerList, int start, int end, int pivot, int index) {
        context.clearRect(0, 0, width, height);
        for (int i = 1; i <= integerList.size(); i++) {
            int access = i - 1;

            //is it in our range?
            if (access < start || access > end)
                context.setFill(Color.GREY);
                //less than pivot?
            else if (access < pivot)
                context.setFill(Color.ORANGE);
                //pivot?
            else if (access == pivot)
                context.setFill(Color.GREEN);
                //got compared to pivot ?
            else if (access <= index)
                context.setFill(Color.BLUE);
                //is if it's inside our current searching set
            else if (access <= end)
                context.setFill(Color.BLACK);

            //Drawing the columns and it's borders
            context.fillRect(i * triangleWidth + WIDTH_OFFSET, MAX_HEIGHT - factor * integerList.get(access), triangleWidth, factor * integerList.get(access));
            context.strokeRect(i * triangleWidth + WIDTH_OFFSET, MAX_HEIGHT - factor * integerList.get(access), triangleWidth, factor * integerList.get(access));
        }

    }

    /**
     * The quick sort method implemented by me
     * and it automatically calls paint() method after each iteration
     */
    private static void quickSort(List<Double> integersList, int start, int end)
            throws InterruptedException {

        if (end - start == 1 || start == integersList.size())
            return;

        Double pivot = integersList.get(start);
        int pivotIndex = start;

        for (int i = pivotIndex + 1; i < end; i++) {

            if (i == start + 1) {
                paint(integersList, start, end, pivotIndex, pivotIndex);
                Thread.sleep(FIRST_SLEEP_PERIOD);
            }

            if (integersList.get(i) < pivot) {
                Double temp = integersList.get(i);
                integersList.remove(i);
                integersList.add(pivotIndex, temp);
                pivotIndex++;
            }

            paint(integersList, start, end, pivotIndex, i);

            Thread.sleep(SLEEP_PERIOD);
        }

        print();

        if (pivotIndex != start)
            quickSort(integersList, start, pivotIndex);
        if (end != pivotIndex + 1)
            quickSort(integersList, pivotIndex + 1, end);

    }

    /**
     * gets the maximum of the elements in the array
     * to give proper heights to each column in the visualizer
     */
    private static void max() {
        if (list.size() <= 0)
            return;
        double temp = list.get(0);
        for (Double doub : list)
            if (doub > temp)
                temp = doub;
        MAX = temp;
    }

    public static void main(String[] args) {
        launch(args);
    }
}