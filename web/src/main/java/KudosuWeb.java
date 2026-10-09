import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.teavm.jso.JSExport;

/**
 * Point d'entrée de la démo web : expose le solveur au JavaScript via TeaVM.
 */
public class KudosuWeb {
    public static void main(String[] args) {
    }

    // renvoie la grille résolue (81 chiffres) ou une chaîne vide si pas de solution
    @JSExport
    public static String solve(String sudokuString) {
        int[][] sudoku = stringToSudoku(sudokuString);
        ISudokuResolver resolver = new SudokuResolver(s -> { });
        PrintStream out = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        boolean solved;
        try {
            solved = resolver.resolve(sudoku);
        } finally {
            System.setOut(out);
        }
        return solved ? sudokuToString(sudoku) : "";
    }

    // renvoie ce qu'afficherait `java Kudosu` dans un terminal (codes ANSI inclus)
    @JSExport
    public static String runTerminal(String sudokuString) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            int[][] sudoku = stringToSudoku(sudokuString);
            ISudokuResolver resolver = new SudokuResolver(new SudokuDisplayer());
            resolver.resolve(sudoku);
        } finally {
            System.setOut(out);
        }
        return buffer.toString();
    }

    private static int[][] stringToSudoku(String sudokuString) {
        int[][] sudoku = new int[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                sudoku[y][x] = Character.getNumericValue(sudokuString.charAt(y * 9 + x));
            }
        }
        return sudoku;
    }

    private static String sudokuToString(int[][] sudoku) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : sudoku)
            for (int value : row)
                stringBuilder.append(value);
        return stringBuilder.toString();
    }
}
