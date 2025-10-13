public class SudokuDisplayer implements ISudokuDisplayer {
    // Codes ANSI pour les couleurs
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    
    // Styles
    public static final String BOLD = "\u001B[1m";
    
    public void display(int[][] sudoku) {
        StringBuilder stringBuilder = new StringBuilder(CYAN + "-------------------------" + RESET + System.lineSeparator());
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (x == 0)
                    stringBuilder.append(CYAN + "| " + RESET);
                
                // Colorer les nombres (par exemple, en vert si non-zéro, en rouge si zéro)
                if (sudoku[y][x] == 0) {
                    stringBuilder.append(RED + BOLD + sudoku[y][x] + " " + RESET);
                } else {
                    stringBuilder.append(sudoku[y][x] + " ");
                }
                
                if ((x + 1) % 3 == 0)
                    stringBuilder.append(CYAN + "| " + RESET);
                if (x + 1 == 9) {
                    stringBuilder.append(System.lineSeparator());
                    if ((y + 1) % 3 == 0)
                        stringBuilder.append(CYAN + "-------------------------" + RESET + System.lineSeparator());
                }
            }
        }
        System.out.println(stringBuilder.toString());
    }
}