import javafx.animation.AnimationTimer;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.geometry.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

//slider
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * RegisterStarter - Allows clients to register and find out who is registered.
 * Name: <your name>
 * Course/Section: ISTE-121-<section>
 * Practice Practical #2
 * Date: <today’s date>
 * 
 */

public class ChatClient extends Application implements EventHandler<ActionEvent> {
    // Game attributes
    Canvas canvas = null;
    Canvas graves = null;
    private final static String ICON_IMAGE = "pac1.gif"; // file with icon for a racer
    private final static String ICON_IMAGE2 = "pac2.gif"; // file with icon for a racer

    private int iconWidth; // width (in pixels) of the icon
    private int iconHeight; // height (in pixels) or the icon
    private PacmanRacer racer = null;
    private PacmanRacer racer2 = null;
    private PacmanRacer[] racers = new PacmanRacer[2];
    private GhostRacer[] ghosts = new GhostRacer[12]; // array of ghosts
    private Image carImage = null;
    private Image carImage2 = null;
    private Image[] ghostImages = new Image[4];

    private AnimationTimer timer; // timer to control animation

    private KeyEvent ke = null;

    boolean shooting = false;
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();

    private Image background = null;
    private Image background2 = null;
    private Image cherry = null;
    private Image strawberry = null;

    private ImageView cherryImage1 = null;
    private ImageView cherryImage2 = null;
    private ImageView cherryImage3 = null;

    private ImageView cherryImage4 = null;
    private ImageView cherryImage5 = null;
    private ImageView cherryImage6 = null;

    private ImageView strawberryImage1 = null;
    private ImageView strawberryImage2 = null;
    private ImageView strawberryImage3 = null;

    private ImageView strawberryImage4 = null;
    private ImageView strawberryImage5 = null;
    private ImageView strawberryImage6 = null;

    GraphicsContext dot = null;
    GraphicsContext grave = null;

    private double c1X;
    private double c1Y;
    private double c2X;
    private double c2Y;
    private double c3X;
    private double c3Y;

    private double s1X;
    private double s1Y;
    private double s2X;
    private double s2Y;
    private double s3X;
    private double s3Y;

    private double c4X;
    private double c4Y;
    private double c5X;
    private double c5Y;
    private double c6X;
    private double c6Y;

    private double s4X;
    private double s4Y;
    private double s5X;
    private double s5Y;
    private double s6X;
    private double s6Y;

    private boolean scareNoMore = false;

    private int ghostsKilled = 0;

    private int level = 1;
    private int numPlayers = 2;

    private boolean timeTaken = false;
    long now;
    int fpsCounter = 0;

    // Window objects
    Stage stage = null;
    Stage game_stage = null;
    Scene scene = null;
    VBox root = null;

    Scene game_scene = null;

    // game GUI
    Pane gamePane = null;

    // GUI Components
    private TextField tfServer = new TextField("localhost");
    private TextField tfName = new TextField();
    private Button btnConnect = new Button("Connect");
    private TextArea taForMessages = new TextArea();
    private TextField tfMessageToSend = new TextField("Message to send");

    Slider slider1 = new Slider();
    Slider slider2 = new Slider();

    // socket
    private Socket socket = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private static final int SERVER_PORT = 1234;

    private int currentID;

    private boolean connected = false;
    private boolean begin = false;
    private boolean endGame = true;

    /**
     * Main program ...
     * 
     * @args - command line arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** constructor */
    public void start(Stage _stage) {
        // Window set up
        stage = _stage;
        game_stage = new Stage();
        stage.setTitle("Welcome to Pu(f)ckman");
        game_stage.setTitle("Puck,man!");
        final int WIDTH = 500;
        final int HEIGHT = 400;
        final int X = 50;
        final int Y = 100;
        stage.setX(X);
        stage.setY(Y);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
                System.exit(0);
            }
        });

        // Draw the GUI
        root = new VBox();
        gamePane = new StackPane();
        HBox hbTop = new HBox(10);
        hbTop.getChildren().addAll(new Label("Server"), tfServer, new Label("Name"), tfName, btnConnect);

        root.getChildren().addAll(hbTop, taForMessages, tfMessageToSend, slider1, slider2);
        for (Node n : root.getChildren()) {
            VBox.setMargin(n, new Insets(5));
        }
        btnConnect.setOnAction(this);

        // Set the scene and show the stage
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        // set up canvas
        canvas = new Canvas();
        graves = new Canvas();
        canvas.setHeight(720);
        canvas.setWidth(1080);
        graves.setHeight(720);
        graves.setWidth(1080);
        dot = canvas.getGraphicsContext2D();
        grave = canvas.getGraphicsContext2D();

        // Adjust sizes
        taForMessages.setPrefHeight(HEIGHT - hbTop.getPrefHeight() - tfMessageToSend.getPrefHeight());
        tfServer.setPrefColumnCount(12);
        tfName.setPrefColumnCount(7);

        // ENTER PRESSED // import javafx.scene.input.KeyEvent;
        tfMessageToSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    sendMessage();
                }

            }

        });

        slider1.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                if (currentID == 0)
                    sendSliderStatus(new_val.intValue());

            }
        });
        slider2.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                if (currentID == 1)
                    sendSliderStatus(new_val.intValue());
            }
        });

        // create an array of Racers (Panes) and start
        initializeScene();
    } // end of Start

    // start the race
    public void initializeScene() {

        // int screenWidth = (int)screenBounds.getWidth();
        // int screenHeight = (int) (screenBounds.getHeight()-200);
        int screenWidth = 1080;
        int screenHeight = 720;

        // Set the game scene and show stage
        game_scene = new Scene(gamePane, screenWidth, screenHeight);

        // Make an icon image to find its size
        try {
            carImage = new Image(new FileInputStream(ICON_IMAGE));
            carImage2 = new Image(new FileInputStream(ICON_IMAGE2));
            for (int i = 0; i < 4; i++) {
                ghostImages[i] = new Image(new FileInputStream("ghost" + (i + 1) + ".gif"));
            }
            // background = new Image(new FileInputStream("background.png"),screenWidth,
            // screenHeight, false, false);
            background = new Image(new FileInputStream("back1.png"));
            background2 = new Image(new FileInputStream("motion2.gif"));
            cherry = new Image(new FileInputStream("cherry.png"));
            strawberry = new Image(new FileInputStream("strawberry.png"));
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            System.exit(1);
        }
        cherryImage1 = new ImageView(cherry);
        cherryImage2 = new ImageView(cherry);
        cherryImage3 = new ImageView(cherry);

        cherryImage4 = new ImageView(cherry);
        cherryImage5 = new ImageView(cherry);
        cherryImage6 = new ImageView(cherry);

        strawberryImage1 = new ImageView(strawberry);
        strawberryImage2 = new ImageView(strawberry);
        strawberryImage3 = new ImageView(strawberry);

        strawberryImage4 = new ImageView(strawberry);
        strawberryImage5 = new ImageView(strawberry);
        strawberryImage6 = new ImageView(strawberry);

        PixelReader pix = background.getPixelReader();

        // Get image size
        iconWidth = (int) carImage.getWidth();
        iconHeight = (int) carImage.getHeight();

        racer = new PacmanRacer(game_scene, carImage, pix);
        racer2 = new PacmanRacer(game_scene, carImage2, pix);
        numPlayers = 2;
        racers[0]= racer;
        racers[1] = racer2;


        for (int j = 0; j <= 8; j += 4) {
            for (int i = 0; i < 4; i++) {
                ghosts[j + i] = new GhostRacer(game_scene, ghostImages[i], pix);
                if (j == 4)
                    ghosts[j + i].setRacePos(360, 340);
                if (j == 8)
                    ghosts[j + i].setRacePos(540, 560);
            }
        }
        gamePane.getChildren().addAll(new ImageView(background2), racer2, racer);
        addFruit();

        for (GhostRacer ghost : ghosts) {
            gamePane.getChildren().add(ghost);
        }
        gamePane.getChildren().add(canvas);
        gamePane.setId("pane");

        // display the window
        // scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        game_stage.setScene(game_scene);
        game_stage.show();

        System.out.println("Starting race...");

        // Use an animation to update the screen
        timer = new AnimationTimer() {
            public void handle(long now) {
                fpsCounter++;
                hitDetect();
                fruitDetect();
                if (racer.getFruitsEaten() >= 12 && !scareNoMore) {
                    scareGhosts();
                    scareNoMore = true;
                }
                if (ghostsKilled == 12) {
                    levelUP();
                }
                if (currentID == 0) {
                    racer.update();
                    // if (racer.isMoving()) {
                        if(fpsCounter%5==0){
                            sendPacmanStatus(racer.getImage().getTranslateX(), racer.getImage().getTranslateY(),
                                    racer.getRot());
                        }
                    // }
                }
                if (currentID == 1) {
                    racer2.update();
                    // if (racer2.isMoving()) {
                        if(fpsCounter%5==0){
                            sendPacmanStatus(racer2.getImage().getTranslateX(), racer2.getImage().getTranslateY(),
                                    racer2.getRot());

                        }
                    // }
                }
                if(currentID==0){
                    for (GhostRacer ghost : ghosts) {
                        ghost.update();
                        }
                    if(fpsCounter%7==0){
                    for (int i=0; i<12;i++){
                        sendGhostStatus(ghosts[i].getImage().getTranslateX(), ghosts[i].getImage().getTranslateY(), i);
                    }
                }
                }

                if(currentID == 0){
                    if(racer.isFire() && racer.getFruitsEaten()>=12){
                        drawDot();
                        moveDot();
                        // if(fpsCounter%5==0){
                        sendDotStatus(racer.getImage().getTranslateX(), racer.getImage().getTranslateY(),
                        racer.getRot());
                        // }

                    }
                }
                if(currentID ==1){
                    if(racer2.isFire() && racer.getFruitsEaten()>=12){
                        drawDot();
                        moveDot();
                        // if(fpsCounter%5==0){
                        sendDotStatus(racer2.getImage().getTranslateX(), racer2.getImage().getTranslateY(),
                        racer2.getRot());
                        // }

                    }
                }
            }
        };

        // TimerTask to delay start of race for 2 seconds
        TimerTask task = new TimerTask() {
            public void run() {
                if(begin){
                    timer.start();

                }
            }
        };
        Timer startTimer = new Timer();

        long delay = 1000L; 
        startTimer.schedule(task, delay);
    }// end of initilize scene

    public void drawDot() {
        if(currentID == 0){
            double y = (racer.getImage().getTranslateY() + 12.5);
            double x = (racer.getImage().getTranslateX() + 12.5);
            // sendDotStatus(x, y, racer.getRot());
            
            dot.setFill(Color.BLACK);
            dot.fillRect(x, y, 2, 2);
        }
        if(currentID==1){
            double y = (racer2.getImage().getTranslateY() + 12.5);
            double x = (racer2.getImage().getTranslateX() + 12.5);
            // sendDotStatus(x, y, racer2.getRot());
    
            dot.setFill(Color.BLACK);
            dot.fillRect(x, y, 2, 2);
        }
        // for(PacmanRacer racer: racers){
        //     double y = (racer.getImage().getTranslateY() + 12.5);
        //     double x = (racer.getImage().getTranslateX() + 12.5);
        //     sendDotStatus(x, y, racer.getRot());
    
        //     dot.setFill(Color.BLACK);
        //     dot.fillRect(x, y, 2, 2);
        // }

    }

    public void moveDot() {
        /*
         * TranslateTransition transition = new TranslateTransition();
         * transition.setDuration(Duration.millis(500));
         * transition.set
         */
        if(currentID == 0){
            for (GhostRacer ghost : ghosts) {
                double ghostX = ghost.getImage().getTranslateX();
                double ghostY = ghost.getImage().getTranslateY();
    
                double y = (racer.getImage().getTranslateY() + 12.5);
                double x = (racer.getImage().getTranslateX() + 12.5);
                for (int i = 0; i < 20; i++) {
                    dot.clearRect(x, y, 2, 2);
                    y -= -1 * (Math.sin(Math.toRadians(racer.getRot()))) * 5;
                    x += (Math.cos(Math.toRadians(racer.getRot()))) * 5;
                    dot.fillRect(x, y, 2, 2);
                    // dot.clearRect(x, y, 10, 10);
                    Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
                    Rectangle dotRect = new Rectangle(x, y, 3, 3);
                    if (dotRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive()) {
                        ghost.die();
                        grave.setFill(Color.BROWN);
                        grave.fillRect(ghostX + 12.5, ghostY, 20, 80);
                        grave.fillRect(ghostX - 8, ghostY + 20, 60, 20);
                        gamePane.getChildren().remove(ghost);
                        ghostsKilled++;
                    }
    
                }
    
            }
        }
        if(currentID == 1){
            for (GhostRacer ghost : ghosts) {
                double ghostX = ghost.getImage().getTranslateX();
                double ghostY = ghost.getImage().getTranslateY();
    
                double y = (racer2.getImage().getTranslateY() + 12.5);
                double x = (racer2.getImage().getTranslateX() + 12.5);
                for (int i = 0; i < 20; i++) {
                    dot.clearRect(x, y, 2, 2);
                    y -= -1 * (Math.sin(Math.toRadians(racer2.getRot()))) * 5;
                    x += (Math.cos(Math.toRadians(racer2.getRot()))) * 5;
                    dot.fillRect(x, y, 2, 2);
                    // dot.clearRect(x, y, 10, 10);
                    Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
                    Rectangle dotRect = new Rectangle(x, y, 3, 3);
                    if (dotRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive()) {
                        ghost.die();
                        grave.setFill(Color.BROWN);
                        grave.fillRect(ghostX + 12.5, ghostY, 20, 80);
                        grave.fillRect(ghostX - 8, ghostY + 20, 60, 20);
                        gamePane.getChildren().remove(ghost);
                        ghostsKilled++;
                    }
    
                }
    
            }
        }
        // for(PacmanRacer racer: racers){
        //     for (GhostRacer ghost : ghosts) {
        //         double ghostX = ghost.getImage().getTranslateX();
        //         double ghostY = ghost.getImage().getTranslateY();
    
        //         double y = (racer.getImage().getTranslateY() + 12.5);
        //         double x = (racer.getImage().getTranslateX() + 12.5);
        //         for (int i = 0; i < 20; i++) {
        //             dot.clearRect(x, y, 2, 2);
        //             y -= -1 * (Math.sin(Math.toRadians(racer.getRot()))) * 5;
        //             x += (Math.cos(Math.toRadians(racer.getRot()))) * 5;
        //             dot.fillRect(x, y, 2, 2);
        //             // dot.clearRect(x, y, 10, 10);
        //             Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
        //             Rectangle dotRect = new Rectangle(x, y, 3, 3);
        //             if (dotRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive()) {
        //                 ghost.die();
        //                 grave.setFill(Color.BROWN);
        //                 grave.fillRect(ghostX + 12.5, ghostY, 20, 80);
        //                 grave.fillRect(ghostX - 8, ghostY + 20, 60, 20);
        //                 gamePane.getChildren().remove(ghost);
        //                 ghostsKilled++;
        //             }
    
        //         }
    
        //     }

        // }

    }

    
    /** 
     * @param x
     * @param y
     * @param raceROT
     */
    public void sendDotStatus(double x, double y, int raceROT) {
        if (connected) {
            DotStatus newDotStatus = new DotStatus(this.currentID,x, y, raceROT);
            try {
                oos.writeObject(newDotStatus);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanCanvas() {
        dot.clearRect(0, 0, 1080, 720);
    }

    public void scareGhosts() {
        for (GhostRacer ghost : ghosts) {
            ghost.scare();
        }
    }

    public void unscareGhosts() {
        for (int i = 0; i < 4; i++) {
            try {
                ghosts[i].getImage().setImage(new Image(new FileInputStream("ghost" + (i + 1) + ".gif")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (int j = 0; j <= 8; j += 4) {
            for (int i = 0; i < 4; i++) {
                try {
                    ghosts[j + i].getImage().setImage(new Image(new FileInputStream("ghost" + (i + 1) + ".gif")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void levelUP() {
        numPlayers = 2;
        for (int i = 0; i < 12; i++) {

            if (i < 4)
                ghosts[i].restart(740, 360);
            if (i >= 4 && i < 8)
                ghosts[i].restart(360, 340);
            if (i >= 8)
                ghosts[i].restart(540, 560);
            ghosts[i].incVelocity();
            gamePane.getChildren().add(ghosts[i]);
        }
        unscareGhosts();
        for(PacmanRacer pac: racers){
            pac.incVelocity();
            pac.restart();
        }
        addFruit();
        ghostsKilled = 0;
        scareNoMore = false;
        level++;
        cleanCanvas();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addFruit() {
        c1X = 640;
        c1Y = 460;
        c2X = 690;
        c2Y = 510;
        c3X = 740;
        c3Y = 560;
        s1X = 440;
        s1Y = 260;
        s2X = 390;
        s2Y = 210;
        s3X = 340;
        s3Y = 160;

        c4X = 100;
        c4Y = 160;
        c5X = 100;
        c5Y = 360;
        c6X = 100;
        c6Y = 560;
        s4X = 980;
        s4Y = 160;
        s5X = 980;
        s5Y = 360;
        s6X = 980;
        s6Y = 560;

        gamePane.getChildren().addAll(cherryImage1, cherryImage2, cherryImage3, cherryImage4, cherryImage5,
                cherryImage6,
                strawberryImage1, strawberryImage2, strawberryImage3, strawberryImage4, strawberryImage5,
                strawberryImage6);
        cherryImage1.setTranslateX(c1X - 540);
        cherryImage1.setTranslateY(c1Y - 360);
        cherryImage2.setTranslateX(c2X - 540);
        cherryImage2.setTranslateY(c2Y - 360);
        cherryImage3.setTranslateX(c3X - 540);
        cherryImage3.setTranslateY(c3Y - 360);
        strawberryImage1.setTranslateX(s1X - 540);
        strawberryImage1.setTranslateY(s1Y - 360);
        strawberryImage2.setTranslateX(s2X - 540);
        strawberryImage2.setTranslateY(s2Y - 360);
        strawberryImage3.setTranslateX(s3X - 540);
        strawberryImage3.setTranslateY(s3Y - 360);

        cherryImage4.setTranslateX(c4X - 540);
        cherryImage4.setTranslateY(c4Y - 360);
        cherryImage5.setTranslateX(c5X - 540);
        cherryImage5.setTranslateY(c5Y - 360);
        cherryImage6.setTranslateX(c6X - 540);
        cherryImage6.setTranslateY(c6Y - 360);
        strawberryImage4.setTranslateX(s4X - 540);
        strawberryImage4.setTranslateY(s4Y - 360);
        strawberryImage5.setTranslateX(s5X - 540);
        strawberryImage5.setTranslateY(s5Y - 360);
        strawberryImage6.setTranslateX(s6X - 540);
        strawberryImage6.setTranslateY(s6Y - 360);

    }

    public void fruitDetect() {
        for(PacmanRacer racerPac: racers){

            double racerX = racerPac.getImage().getTranslateX();
        double racerY = racerPac.getImage().getTranslateY();

        Rectangle pacRect = new Rectangle(racerX, racerY, 30, 30);

        Rectangle c1Rect = new Rectangle(c1X - 12.5, c1Y - 12.5, 25, 25);
        Rectangle c2Rect = new Rectangle(c2X - 12.5, c2Y - 12.5, 25, 25);
        Rectangle c3Rect = new Rectangle(c3X - 12.5, c3Y - 12.5, 25, 25);
        Rectangle s1Rect = new Rectangle(s1X - 12.5, s1Y - 12.5, 25, 25);
        Rectangle s2Rect = new Rectangle(s2X - 12.5, s2Y - 12.5, 25, 25);
        Rectangle s3Rect = new Rectangle(s3X - 12.5, s3Y - 12.5, 25, 25);

        Rectangle c4Rect = new Rectangle(c4X - 12.5, c4Y - 12.5, 25, 25);
        Rectangle c5Rect = new Rectangle(c5X - 12.5, c5Y - 12.5, 25, 25);
        Rectangle c6Rect = new Rectangle(c6X - 12.5, c6Y - 12.5, 25, 25);
        Rectangle s4Rect = new Rectangle(s4X - 12.5, s4Y - 12.5, 25, 25);
        Rectangle s5Rect = new Rectangle(s5X - 12.5, s5Y - 12.5, 25, 25);
        Rectangle s6Rect = new Rectangle(s6X - 12.5, s6Y - 12.5, 25, 25);

        if (pacRect.intersects(c1Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage1);
            c1X = 9999;
            c1Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(c2Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage2);
            c2X = 9999;
            c2Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(c3Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage3);
            c3X = 9999;
            c3Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s1Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage1);
            s1X = 9999;
            s1Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s2Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage2);
            s2X = 9999;
            s2Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s3Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage3);
            s3X = 9999;
            s3Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }

        if (pacRect.intersects(c4Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage4);
            c4X = 9999;
            c4Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(c5Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage5);
            c5X = 9999;
            c5Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(c6Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(cherryImage6);
            c6X = 9999;
            c6Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s4Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage4);
            s4X = 9999;
            s4Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s5Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage5);
            s5X = 9999;
            s5Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }
        if (pacRect.intersects(s6Rect.getBoundsInLocal())) {
            gamePane.getChildren().remove(strawberryImage6);
            s6X = 9999;
            s6Y = 9999;
            racer.setFruitsEaten(racer.getFruitsEaten() + 1);
            racer2.setFruitsEaten(racer.getFruitsEaten() + 1);
        }

        }
        
    }

    public void hitDetect() {
        for(PacmanRacer pac: racers){
            for (GhostRacer ghost : ghosts) {
                double racerX = pac.getImage().getTranslateX();
                double racerY = pac.getImage().getTranslateY();
                double ghostX = ghost.getImage().getTranslateX();
                double ghostY = ghost.getImage().getTranslateY();
                Rectangle pacRect = new Rectangle(racerX, racerY, 25, 25);
                Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
                if (pacRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive() && pac.isAlive()) {
                    // timer.stop();
                    gamePane.getChildren().remove(pac);
                    pac.die();
                    numPlayers--;
                    System.out.println(numPlayers);
            }

        }
            if(numPlayers==0 && endGame){
                endGame = false;
                timer.stop();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Game over!");
                        alert.setContentText("You have reached level " + level + " !");
                        alert.showAndWait();
                    }
                });
            }
        }
    }

    /** Button handler */
    public void handle(ActionEvent ae) {
        Button btn = (Button) ae.getSource();
        switch (btn.getText()) {
            case "Connect":
                doConnect();
                break;
            case "Disconnect":
                doDisconnect();
                break;
        }
    }

    private void doDisconnect() {
        System.out.println("Disconnect the client");
        try {
            oos.close();
            ois.close();
            socket.close();
            btnConnect.setText("Connect");
            tfName.setDisable(false);
            tfServer.setDisable(false);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Exception");
        }
    }

    private void sendMessage() {
        // NAME:MESSAGE
        try {
            oos.writeObject("CHAT@" + tfName.getText() + ":" + tfMessageToSend.getText());
            this.oos.flush();
            tfMessageToSend.clear();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class ClientThread extends Thread {

        @Override
        public void run() {

            try {
                while (true) {
                    Object obj = ois.readObject();
                    if (obj instanceof String) {

                        String message = (String) obj; // chat feedback
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                taForMessages.appendText(message + "\n");
                            }
                        });
                    } else if (obj instanceof Begin) {
                       begin=true;
                       timer.start();
                    } else if (obj instanceof Status) {
                        Status newStatus = (Status) obj;
                        if (newStatus.getID() != currentID) {
                            switch (newStatus.getID()) {
                                case 0:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            slider1.setValue(newStatus.getSliderStatus());
                                        }
                                    });
                                    break;
                                case 1:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            slider2.setValue(newStatus.getSliderStatus());
                                        }
                                    });
                                    break;
                            }
                        }
                    } else if (obj instanceof PacmanStatus) {
                        PacmanStatus newPacmanStatus = (PacmanStatus) obj;
                        if (newPacmanStatus.getID() != currentID) {
                            switch (newPacmanStatus.getID()) {
                                case 0:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            racer.getImage().setTranslateX(newPacmanStatus.getRacePosX());
                                            racer.getImage().setTranslateY(newPacmanStatus.getRacePosY());
                                            racer.getImage().setRotate(newPacmanStatus.getRaceROT());
                                        }
                                    });
                                    break;
                                case 1:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            racer2.getImage().setTranslateX(newPacmanStatus.getRacePosX());
                                            racer2.getImage().setTranslateY(newPacmanStatus.getRacePosY());
                                            racer2.getImage().setRotate(newPacmanStatus.getRaceROT());
                                        }
                                    });
                                    break;
                            }
                        }

                    } else if (obj instanceof DotStatus) {
                        DotStatus newDotStatus = (DotStatus) obj;
                        if (newDotStatus.getID() != currentID) {
                            switch (newDotStatus.getID()) {
                                case 0:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            double x = newDotStatus.getX();
                                            double y = newDotStatus.getY();
                                            dot.setFill(Color.BLACK);
                                            for (GhostRacer ghost : ghosts) {
                                                double ghostX = ghost.getImage().getTranslateX();
                                                double ghostY = ghost.getImage().getTranslateY();
                                                // for (int i = 0; i < 20; i++) {
                                                    dot.clearRect(x, y, 2, 2);
                                                    y -= -1 * (Math.sin(Math.toRadians(newDotStatus.getRot()))) * 7.26;
                                                    x += (Math.cos(Math.toRadians(newDotStatus.getRot()))) * 7.26;
                                                    dot.fillRect(x, y, 2, 2);
                                                    // dot.clearRect(x, y, 10, 10);
                                                    Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
                                                    Rectangle dotRect = new Rectangle(x, y, 3, 3);
                                                    if (dotRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive()) {
                                                        ghost.die();
                                                        grave.setFill(Color.BROWN);
                                                        grave.fillRect(ghostX + 12.5, ghostY, 20, 80);
                                                        grave.fillRect(ghostX - 8, ghostY + 20, 60, 20);
                                                        gamePane.getChildren().remove(ghost);
                                                        ghostsKilled++;
                                                    // }
                                    
                                                }
                                            }

                                        }
                                    });
                                    break;
                                case 1:
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        double x = newDotStatus.getX();
                                        double y = newDotStatus.getY();
                                        dot.setFill(Color.BLACK);
                                        for (GhostRacer ghost : ghosts) {
                                            double ghostX = ghost.getImage().getTranslateX();
                                            double ghostY = ghost.getImage().getTranslateY();
                                            // for (int i = 0; i < 20; i++) {
                                                dot.clearRect(x, y, 2, 2);
                                                y -= -1 * (Math.sin(Math.toRadians(newDotStatus.getRot()))) *7.25;
                                                x += (Math.cos(Math.toRadians(newDotStatus.getRot()))) * 7.26;
                                                dot.fillRect(x, y, 2, 2);
                                                // dot.clearRect(x, y, 10, 10);
                                                Rectangle ghostRect = new Rectangle(ghostX, ghostY, 25, 25);
                                                Rectangle dotRect = new Rectangle(x, y, 3, 3);
                                                if (dotRect.intersects(ghostRect.getBoundsInLocal()) && ghost.isAlive()) {
                                                    ghost.die();
                                                    grave.setFill(Color.BROWN);
                                                    grave.fillRect(ghostX + 12.5, ghostY, 20, 80);
                                                    grave.fillRect(ghostX - 8, ghostY + 20, 60, 20);
                                                    gamePane.getChildren().remove(ghost);
                                                    ghostsKilled++;
                                                // }
                                
                                            }
                                        }

                                    }
                                });
                                    break;
                            }
                        }

                    }    else if (obj instanceof GhostStatus) {
                        GhostStatus newGhostStatus = (GhostStatus) obj;
                        if (newGhostStatus.getID() != currentID) {
                            switch (newGhostStatus.getID()) {
                                case 0:
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            for(int i=0; i<12; i++){
                                                if(newGhostStatus.getNum()==i){
                                                    ghosts[i].getImage().setTranslateX(newGhostStatus.getRacePosX());
                                                    ghosts[i].getImage().setTranslateY(newGhostStatus.getRacePosY());

                                                }
                                            }

                                        }
                                    });
                                    break;
                                // case 1:
                                //     Platform.runLater(new Runnable() {
                                //         @Override
                                //         public void run() {
                                //             for(int i=0; i<12; i++){
                                //                 if(newGhostStatus.getNum()==i){
                                //                     ghosts[i].getImage().setTranslateX(newGhostStatus.getRacePosX());
                                //                     ghosts[i].getImage().setTranslateY(newGhostStatus.getRacePosY());
                                //                 }
                                //             }

                                //         }
                                //     });
                                //     break;
                            }
                        }

                    }
                    
                }

            } catch (ClassNotFoundException | IOException cnfe) {
                cnfe.getMessage();
            }
        }
    }

    
    /** 
     * @param value
     */
    private void sendSliderStatus(int value) {
        Status newStatus = new Status(this.currentID, value);
        try {
            oos.writeObject(newStatus);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @param racePosX
     * @param racePosY
     * @param raceROT
     */
    private void sendPacmanStatus(double racePosX, double racePosY, int raceROT) {
        if (connected) {

            PacmanStatus newPacmanStatus = new PacmanStatus(this.currentID, racePosX, racePosY, raceROT);
            try {
                oos.writeObject(newPacmanStatus);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    /** 
     * @param racePosX
     * @param racePosY
     * @param ghostNum
     */
    private void sendGhostStatus(double racePosX, double racePosY, int ghostNum) {
        if (connected) {
            GhostStatus newGhostStatus = new GhostStatus(this.currentID, racePosX, racePosY, ghostNum);
            try {
                oos.writeObject(newGhostStatus);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doConnect() {
        Object feedback=null;
        try {
            this.socket = new Socket(tfServer.getText(), SERVER_PORT);
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.ois = new ObjectInputStream(this.socket.getInputStream());
            this.oos.writeObject("REGISTER@" + tfName.getText());
            this.oos.flush();
            while(!(feedback instanceof Integer) && !connected){
                feedback = ois.readObject();
            }
            this.currentID = (Integer) feedback;
            switch (this.currentID) {
                case 0:
                    slider2.setDisable(true);
                    racer.setID(this.currentID);
                    racer.init();
                    break;
                case 1:
                    slider1.setDisable(true);
                    racer2.setID(this.currentID); 
                    racer2.init();
                    timer.start();
                    break;
            }
            ClientThread cT = new ClientThread();
            cT.start();
            connected = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @param type
     * @param message
     */
    public void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

}
