import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.stage.Stage;

public class SudokuGUI extends Application {
    private TextField[][] cells = new TextField[9][9];
    private Button        solveButton;
    private Button        clearButton;
    private Button        exampleButton;
    private Label         statusLabel;
    private Label         titleLabel;
    private BorderPane    root;
    private GridPane      grid;
    private boolean       isDarkMode = true;
    
    // Couleurs Light Mode
    private static final String LIGHT_BG      = "#f0f0f0";
    private static final String LIGHT_TITLE   = "#333333";
    private static final String LIGHT_CELL_1  = "#ffffff";
    private static final String LIGHT_CELL_2  = "#e8f4f8";
    private static final String LIGHT_TEXT    = "#000000";
    private static final String LIGHT_GRID_BG = "#333333";
    private static final String LIGHT_BORDER  = "#333333";
    private static final String LIGHT_STATUS  = "#666666";
    
    // Couleurs Dark Mode
    private static final String DARK_BG       = "#1a1a1a";
    private static final String DARK_TITLE    = "#ffffff";
    private static final String DARK_CELL_1   = "#2a2a2a";
    private static final String DARK_CELL_2   = "#1f3a4a";
    private static final String DARK_TEXT     = "#ffffff";
    private static final String DARK_GRID_BG  = "#0d0d0d";
    private static final String DARK_BORDER   = "#0d0d0d";
    private static final String DARK_STATUS   = "#b0b0b0";
    
    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: " + DARK_BG + ";");
        
        // header
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        
        // titre
        titleLabel = new Label("Sudoku Resolver");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: " + DARK_TITLE + ";");
        
        // Créer le switch personnalisé
        HBox switchContainer = createThemeSwitch();
        
        headerBox.getChildren().addAll(titleLabel, switchContainer);
        root.setTop(headerBox);
        
        // grille
        grid = createGrid();
        root.setCenter(grid);
        
        // btn
        VBox bottomBox = createBottomPanel();
        root.setBottom(bottomBox);
        
        Scene scene = new Scene(root, 550, 680);
        
        primaryStage.setTitle("Sudoku Resolver");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setStyle("-fx-background-color: #0d0d0d;");
        grid.setPadding(new Insets(10));
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField cell = new TextField();
                cell.setPrefWidth(50);
                cell.setPrefHeight(50);
                cell.setAlignment(Pos.CENTER);
                cell.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                
                // couleur alt des carrés 3x3
                String bgColor = (i / 3 + j / 3) % 2 == 0 ? DARK_CELL_1 : DARK_CELL_2;
                cell.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + DARK_TEXT + ";");
                
                // bordures + épaisses carrés 3x3
                String borderStyle = getBorderStyle(i, j, DARK_BORDER);
                if (!borderStyle.isEmpty()) {
                    cell.setStyle(cell.getStyle() + borderStyle);
                }
                
                // valide saisie (1-9)
                final int row = i;
                final int col = j;
                cell.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.length() > 1) {
                        cell.setText(newVal.substring(0, 1));
                    }
                    if (!newVal.isEmpty() && !newVal.matches("[1-9]")) {
                        cell.setText(oldVal);
                    }
                    // remettre couleur txt
                    String currentBg = (row / 3 + col / 3) % 2 == 0 ? 
                        (isDarkMode ? DARK_CELL_1 : LIGHT_CELL_1) : 
                        (isDarkMode ? DARK_CELL_2 : LIGHT_CELL_2);
                    String textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
                    String border = getBorderStyle(row, col, isDarkMode ? DARK_BORDER : LIGHT_BORDER);
                    cell.setStyle("-fx-background-color: " + currentBg + "; -fx-text-fill: " + textColor + ";" + border);
                });
                
                cells[i][j] = cell;
                grid.add(cell, j, i);
            }
        }
        
        return grid;
    }
    
    private VBox createBottomPanel() {
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15, 0, 0, 0));
        
        // le bas
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        solveButton = new Button("Résoudre");
        solveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
        solveButton.setOnAction(e -> solveSudoku());
        
        clearButton = new Button("Effacer");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
        clearButton.setOnAction(e -> clearGrid());
        
        exampleButton = new Button("Exemple");
        exampleButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
        exampleButton.setOnAction(e -> loadExample());
        
        buttonBox.getChildren().addAll(exampleButton, solveButton, clearButton);
        
        // status
        statusLabel = new Label("Entrez un sudoku et cliquez sur 'Résoudre'");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setStyle("-fx-text-fill: #b0b0b0;");
        
        vbox.getChildren().addAll(buttonBox, statusLabel);
        return vbox;
    }
    
    private HBox createThemeSwitch() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(0, 10, 0, 0));
        
        // Label
        Label label = new Label("☀");
        label.setFont(Font.font("Arial", 16));
        label.setStyle("-fx-text-fill: " + DARK_TITLE + ";");
        
        // Créer le switch
        StackPane switchPane = new StackPane();
        switchPane.setPrefSize(50, 25);
        switchPane.setMaxSize(50, 25);
        switchPane.setMinSize(50, 25);
        
        // Piste (background du switch)
        Rectangle track = new Rectangle(50, 25);
        track.setArcWidth(25);
        track.setArcHeight(25);
        track.setFill(javafx.scene.paint.Color.web("#4CAF50")); // Vert pour dark mode
        
        // Bouton circulaire (thumb)
        Circle thumb = new Circle(10);
        thumb.setFill(javafx.scene.paint.Color.WHITE);
        thumb.setTranslateX(15); // Position initiale (dark mode activé)
        
        switchPane.getChildren().addAll(track, thumb);
        
        // Gestion du clic
        switchPane.setOnMouseClicked(e -> {
            isDarkMode = !isDarkMode;
            
            // Animation du thumb
            TranslateTransition transition = new TranslateTransition(Duration.millis(200), thumb);
            transition.setToX(isDarkMode ? 15 : -15);
            transition.play();
            
            // Changement de couleur de la piste
            track.setFill(isDarkMode ? 
                javafx.scene.paint.Color.web("#4CAF50") : 
                javafx.scene.paint.Color.web("#757575"));
            
            // Changer le label
            label.setText(isDarkMode ? "☀" : "🌙");
            
            toggleTheme();
        });
        
        switchPane.setCursor(javafx.scene.Cursor.HAND);
        
        container.getChildren().addAll(label, switchPane);
        return container;
    }
    
    private void solveSudoku() {
        // disable btn résolution
        solveButton  .setDisable(true);
        clearButton  .setDisable(true);
        exampleButton.setDisable(true);
        statusLabel  .setText("Résolution en cours...");
        statusLabel  .setStyle("-fx-text-fill: #FF9800;");
        
        new Thread(() -> {
            try {
                // recup les val grille
                int[][] sudoku = new int[9][9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        String text = cells[i][j].getText();
                        if (!text.isEmpty()) {
                            sudoku[i][j] = Integer.parseInt(text);
                        } else {
                            sudoku[i][j] = 0;
                        }
                    }
                }
                
                // resolve
                ISudokuDisplayer displayer = new SudokuDisplayerGUI(cells);
                ISudokuResolver  resolver  = new SudokuResolver(displayer);
                
                boolean solved = resolver.resolve(sudoku);
                
                // maj interface
                Platform.runLater(() -> {
                    if (solved) {
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                cells[i][j].setText(String.valueOf(sudoku[i][j]));
                                String currentStyle = cells[i][j].getStyle();
                                cells[i][j].setStyle(currentStyle.replaceAll("-fx-text-fill: [^;]+;", "-fx-text-fill: #2196F3;"));
                            }
                        }
                        statusLabel.setText("Sudoku résolu avec succès !");
                        statusLabel.setStyle("-fx-text-fill: #4CAF50;");
                    } else {
                        statusLabel.setText("Impossible de résoudre ce sudoku");
                        statusLabel.setStyle("-fx-text-fill: #f44336;");
                    }
                    
                    // enable btn
                    solveButton  .setDisable(false);
                    clearButton  .setDisable(false);
                    exampleButton.setDisable(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel  .setText("✗ Erreur lors de la résolution");
                    statusLabel  .setStyle("-fx-text-fill: #f44336;");
                    solveButton  .setDisable(false);
                    clearButton  .setDisable(false);
                    exampleButton.setDisable(false);
                });
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void clearGrid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText("");
            }
        }
        statusLabel.setText("Grille effacée");
        statusLabel.setStyle("-fx-text-fill: " + (isDarkMode ? DARK_STATUS : LIGHT_STATUS) + ";");
    }
    
    private void loadExample() {
        // Exemples de sudoku
        String[] examples = {
            "690170003802000700050240000060451000001730002000902506000504030030027948046003275", // easy
            "530700904000400003040300007900045000180000000000098561406000002008900740000020096", // easy
            "427319008000080000080402000130070065004005029008261300871500000006098703340620000", // easy
            "900600300000000469600540000378005002000763015060028704030157906045300120100080500", // easy
            "040027000800640000070030908410900000003005002008000016084000000096000700150490820", // medium
            "000340001050026000000000200005000102004062598000500004068010000302080910097000603", // medium
            "600030750020040016000072000000010003180060024000900065700004001000000040400157038", // medium
            "900000672050409001000000000300602005429015763501704000002006157196500200000020396", // medium
            "030000000500074100002800704701000003200006015060309007609040001058027000000190000", // medium
            "405000000060042810000006030000000300030705469009360087000283000000500000204019500", // medium
            "000000062000007380800613000060300105100429000300061090090800507000700930050000800", // medium
            "000300900020001007000000150157000400090040000000106030030207000509000800600004500", // hard
            "801000045000000706056000800090700100000080000000200538000040080427000010000090004", // hard
            "000580470600000950200000000004002000702800004000030265000050000020703000080090006", // hard
            "004300080000600009061900000020490000503000900000062003300004568780000040000000000", // hard
            "050000000469000005000009300000507200100030000000000010600000007704200100800600042", // expert
            "000070045400000800801000000080002000000590060210004000500400003002000007000600054", // expert
            "050090010000000700000050406908002000070800000600540020000000062000703100506000000"  // expert
        };
        
        int randomIndex = (int) (Math.random() * examples.length);
        String example = examples[randomIndex];
        
        String level;
        if (randomIndex < 4) {
            level = "facile";
        } else if (randomIndex < 11) {
            level = "moyen";
        } else if (randomIndex < 15) {
            level = "difficile";
        } else {
            level = "expert";
        }
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = Character.getNumericValue(example.charAt(i * 9 + j));
                if (value != 0) {
                    cells[i][j].setText(String.valueOf(value));
                } else {
                    cells[i][j].setText("");
                }
                String currentStyle = cells[i][j].getStyle();
                cells[i][j].setStyle(currentStyle.replaceAll("-fx-text-fill: [^;]+;", "-fx-text-fill: #ffffff;"));
            }
        }
        statusLabel.setText("Exemple chargé (niveau " + level + ")");
        statusLabel.setStyle("-fx-text-fill: #b0b0b0;");
    }
    
    private void toggleTheme() {
        if (isDarkMode) {
            root       .setStyle("-fx-background-color: " + DARK_BG        + ";");
            titleLabel .setStyle("-fx-text-fill: "        + DARK_TITLE     + ";");
            grid       .setStyle("-fx-background-color: " + DARK_GRID_BG   + ";");
            statusLabel.setStyle("-fx-text-fill: "        + DARK_STATUS    + ";");
            
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String bgColor = (i / 3 + j / 3) % 2 == 0 ? DARK_CELL_1 : DARK_CELL_2;
                    String baseStyle = "-fx-background-color: " + bgColor + "; -fx-text-fill: " + DARK_TEXT + ";";
                    String borderStyle = getBorderStyle(i, j, DARK_BORDER);
                    cells[i][j].setStyle(baseStyle + borderStyle);
                }
            }
        } else {
            root       .setStyle("-fx-background-color: " + LIGHT_BG       + ";");
            titleLabel .setStyle("-fx-text-fill: "        + LIGHT_TITLE    + ";");
            grid       .setStyle("-fx-background-color: " + LIGHT_GRID_BG  + ";");
            statusLabel.setStyle("-fx-text-fill: "        + LIGHT_STATUS   + ";");
            
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String bgColor     = (i / 3 + j / 3) % 2 == 0 ? LIGHT_CELL_1 : LIGHT_CELL_2;
                    String baseStyle   = "-fx-background-color: " + bgColor + "; -fx-text-fill: " + LIGHT_TEXT + ";";
                    String borderStyle = getBorderStyle(i, j, LIGHT_BORDER);
                    cells[i][j].setStyle(baseStyle + borderStyle);
                }
            }
        }
    }
    
    private String getBorderStyle(int i, int j, String borderColor) {
        String borderWidth = "0 0 0 0";
        
        if (i % 3 == 0 && i != 0) {
            borderWidth = "3 0 0 0";
        }

        if (j % 3 == 0 && j != 0) {
            if (i % 3 == 0 && i != 0) {
                borderWidth = "3 0 0 3";
            } else {
                borderWidth = "0 0 0 3";
            }
        }
        
        if (!borderWidth.equals("0 0 0 0")) {
            return " -fx-border-width: " + borderWidth + "; -fx-border-color: " + borderColor + ";";
        }
        return "";
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
