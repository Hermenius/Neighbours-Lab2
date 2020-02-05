import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.*;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color, boolean bool) {      // Constructor to initialize
            isSatisfied = bool;
            this.color = color;
        }
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors
    final Random rand = new Random();
    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;
        int[] nullindex = getnullindex(world);
        nullindex = shufflearri(nullindex);
        world = makesatisfied(world, threshold);
        world = moveunsat(world, nullindex);

        // TODO update world
    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 900;   // Should also try 90 000
        Actor[] actorarr = new Actor[nLocations];
        // TODO initialize the world
        Actor[][] m = new Actor[(int)sqrt(nLocations)][(int)sqrt(nLocations)];

        for (int i = 0; i < nLocations; i++){
            if (i < nLocations*dist[0]){
                actorarr[i] = new Actor(Color.BLUE, true);
            }
            else if (i < nLocations*(dist[0]+dist[1])){
                actorarr[i] = new Actor(Color.RED, true);
            }
            else {
                actorarr[i] = null;
            }
        }

        actorarr = shufflearr(actorarr);

        int index = 0;
        for (int i = 0; i < sqrt(nLocations); i++) {
            for (int j = 0; j < sqrt(nLocations); j++) {
                if(actorarr[index] != null) {
                    m[i][j] = actorarr[index];
                }
                index++;
            }
        }
        world = m;
        // Should be last
        fixScreenSize(nLocations);
    }

    // ---------------  Methods ------------------------------

    // TODO Many ...

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------
    int[] getnullindex(Actor[][] matrix){
        int index = 0;
        int count = 0;
        for(int row = 0; row < matrix.length; row++){
                for (int col = 0; col < matrix.length; col++){
                    if(matrix[row][col] == null){
                        count++;
                    }
                }
            }
        int[] arr = new int[count];
        for(int row = 0; row < matrix.length; row++){
            for (int col = 0; col < matrix.length; col++){
                if(matrix[row][col] == null){
                    arr[index] = row*matrix.length + col;
                    index++;
                }
            }
        }
        return arr;
    }

    Actor[][] makesatisfied(Actor[][] matrix, double satis){
        int mtrxSize = matrix.length;
        for(int row = 0; row < matrix.length; row++){
            for (int col = 0; col < matrix.length; col++){
                if(matrix[row][col] != null){
                    double tempblu = 0;
                    double tempred = 0;
                    for(int i = -1; i < 2; i++){
                        for(int j = -1; j < 2; j++){
                            if(isValidLocation(mtrxSize, row+i, col+j)){
                                if(matrix[row+i][col+j] != null && i != 0 && j != 0){
                                    if(matrix[row+i][col+j].color == Color.BLUE){
                                        tempblu++;
                                    }
                                    else {
                                        tempred++;
                                    }
                                }
                            }
                        }
                    }
                    if(matrix[row][col].color == Color.BLUE){
                        if(tempblu/(tempred + tempblu) >= satis){
                            matrix[row][col].isSatisfied = true;
                        }
                        else
                            matrix[row][col].isSatisfied = false;
                    }
                    else {
                        if(tempred/(tempblu + tempred) >= satis){
                            matrix[row][col].isSatisfied = true;
                        }
                        else
                            matrix[row][col].isSatisfied = false;
                    }
                }
            }
        }
        return matrix;
    }

    Actor[][] moveunsat(Actor[][] matrix, int[] nullindexlist){
        nullindexlist = shufflearri(nullindexlist);
        int index = 0;
        for(int row = 0; row < matrix.length; row++){
            for (int col = 0; col < matrix.length; col++){
                if (matrix[row][col] != null){
                    if(!(matrix[row][col].isSatisfied)){
                        if(matrix[row][col].color == Color.BLUE){
                            matrix[(nullindexlist[index]/matrix.length)][(nullindexlist[index]%matrix.length)] = new Actor(Color.BLUE, true);
                        }
                        else{
                            matrix[(nullindexlist[index]/matrix.length)][(nullindexlist[index]%matrix.length)] = new Actor(Color.RED, true);
                        }
                        matrix[row][col] = null;
                    }
                }
            }
        }
        return matrix;
    }


    // TODO (general method possible reusable elsewhere)
    Actor[] shufflearr(Actor[] arr){
        for(int i = arr.length; i > 0; i--){
            int ran = rand.nextInt(i);
            Actor tmp = arr[ran];
            arr[ran] = arr[i - 1];
            arr[i-1] = tmp;
        }
        return arr;
    }
    int[] shufflearri(int[] arr){
        for(int i = arr.length; i > 0; i--){
            int ran = rand.nextInt(i);
            int tmp = arr[ran];
            arr[ran] = arr[i - 1];
            arr[i-1] = tmp;
        }
        return arr;
    }
    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED, true), new Actor(Color.RED,true ), null},
                {null, new Actor(Color.BLUE, true), null},
                {new Actor(Color.RED, true), null, new Actor(Color.BLUE, true)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));

        // TODO

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
