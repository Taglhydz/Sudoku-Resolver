import javafx.application.Platform;
import javafx.scene.control.TextField;

public class SudokuDisplayerGUI implements ISudokuDisplayer {
    private TextField[][] cells;
    
    public SudokuDisplayerGUI(TextField[][] cells) {
        this.cells = cells;
    }
    
    @Override
    public void display(int[][] sudoku) {
        Platform.runLater(() -> {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (sudoku[i][j] != 0) {
                        cells[i][j].setText(String.valueOf(sudoku[i][j]));
                    } else {
                        cells[i][j].setText("");
                    }
                }
            }
        });
        try {
            Thread.sleep(5); // pour voir la résolution
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
