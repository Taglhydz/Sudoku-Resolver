public class SudokuDisplayer implements ISudokuDisplayer {
    public void display(int[][] sudoku) {
        StringBuilder stringBuilder = new StringBuilder("-------------------------" + System.lineSeparator());
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (x == 0)
                    stringBuilder.append("| ");
                stringBuilder.append(sudoku[y][x] + " ");
                if ((x + 1) % 3 == 0)
                    stringBuilder.append("| ");
                if (x + 1 == 9) {
                    stringBuilder.append(System.lineSeparator());
                    if ((y + 1) % 3 == 0)
                        stringBuilder.append("-------------------------" + System.lineSeparator());
                }
            }
        }
        System.out.println(stringBuilder.toString());
    }
}