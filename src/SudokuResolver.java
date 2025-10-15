import java.lang.reflect.Array;
import java.util.*;

public class SudokuResolver implements ISudokuResolver {
    private ISudokuDisplayer sudokuDisplayer;

    public SudokuResolver(ISudokuDisplayer sudokuDisplayer) {
        this.sudokuDisplayer = sudokuDisplayer;
    }

    public boolean resolve(int[][] sudoku) {
        return resolveBacktracking(sudoku);
    }

    private boolean resolveBacktracking(int[][] sudoku) {
        // etape 1 : première methode de déduction classique
        boolean progress = true;
        while (progress && !finito(sudoku)) {
            sudokuDisplayer.display(sudoku);
            progress = firstMethodDeduction(sudoku);
        }

        // sudoku résolu
        if (finito(sudoku)) {
            sudokuDisplayer.display(sudoku);
            return true;
        }

        // sudoku avec erreur
        if (hasError(sudoku)) {
            return false;
        }

        // etape 2: backtracking si bloqué
        // trouve la case possibilités min
        int minPossibilities = 10;
        int bestX = -1, bestY = -1;
        ArrayList<Integer> bestPossibilities = null;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0) {
                    ArrayList<Integer> possibilities = this.remove(
                        remove(getValuesSquare(i, j, sudoku), getValuesRow(i, sudoku)), 
                        getValuesColumn(j, sudoku)
                    );
                    
                    if (possibilities.size() == 0) {
                        // pas de possibilité -> erreur
                        return false;
                    }
                    
                    if (possibilities.size() < minPossibilities) {
                        minPossibilities = possibilities.size();
                        bestX = i;
                        bestY = j;
                        bestPossibilities = possibilities;
                    }
                }
            }
        }

        // pas de case vide trouvé -> fini
        if (bestX == -1) {
            sudokuDisplayer.display(sudoku);
            return finito(sudoku);
        }

        // essaye chaque possibilité pour la case choisie au dessus
        for (int value : bestPossibilities) {
            // créé une copie du sudoku pour test
            int[][] sudokuCopy = copySudoku(sudoku);
            sudokuCopy[bestX][bestY] = value;

            // essaye de résoudre avec une valeur
            if (resolveBacktracking(sudokuCopy)) {
                // si résolu -> copie de la copie vers l'original
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        sudoku[i][j] = sudokuCopy[i][j];
                    }
                }
                return true;
            }
        }

        // aucune possibilité fonctionné -> échec
        return false;
    }

    private boolean firstMethodDeduction(int[][] sudoku) {
        boolean changed = false;

        // 1ere méthode : case avec une possibilité
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0) {
                    ArrayList<Integer> elim = new ArrayList<Integer>(
                        this.remove(remove(getValuesSquare(i, j, sudoku), getValuesRow(i, sudoku)), 
                        getValuesColumn(j, sudoku))
                    );
                    
                    if (elim.size() == 1) {
                        sudoku[i][j] = elim.get(0);
                        changed = true;
                    }
                }
            }
        }

        // 2eme méthode : val uniques dans un carré
        for (int carreX = 0; carreX < 3; carreX++) {
            for (int carreY = 0; carreY < 3; carreY++) {
                int startX = carreX * 3;
                int startY = carreY * 3;
                
                ArrayList<List<Object>> lstValuesPossibles = new ArrayList<List<Object>>();
                
                // prendre les possibilité de chaque case du carré
                for (int k = startX; k < startX + 3; k++) {
                    for (int l = startY; l < startY + 3; l++) {
                        if (sudoku[k][l] == 0) {
                            ArrayList<Integer> elim = new ArrayList<Integer>(
                                this.remove(remove(getValuesSquare(k, l, sudoku), getValuesRow(k, sudoku)), 
                                getValuesColumn(l, sudoku))
                            );
                            lstValuesPossibles.add(Arrays.asList(k, l, elim));
                        }
                    }
                }
                
                // trouver les valeurs uniques
                for (int value = 1; value <= 9; value++) {
                    int nbTimes = 0;
                    int posX = -1, posY = -1;
                    
                    // counter
                    for (List<Object> entry : lstValuesPossibles) {
                        @SuppressWarnings("unchecked")
                        ArrayList<Integer> possibilities = (ArrayList<Integer>) entry.get(2);
                        
                        if (possibilities.contains(value)) {
                            nbTimes++;
                            posX = (int) entry.get(0);
                            posY = (int) entry.get(1);
                        }
                    }
                    
                    // si une seule occurence -> ajouter au sudoku
                    if (nbTimes == 1 && posX != -1 && posY != -1) {
                        sudoku[posX][posY] = value;
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    private boolean hasError(int[][] sudoku) { //fini
        // si une case vide sans aucune possibilité alors il y a une erreur
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0) {
                    ArrayList<Integer> possibilities = this.remove(
                        remove(getValuesSquare(i, j, sudoku), getValuesRow(i, sudoku)), 
                        getValuesColumn(j, sudoku)
                    );
                    if (possibilities.size() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[][] copySudoku(int[][] sudoku) { //fini
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                copy[i][j] = sudoku[i][j];
            }
        }
        return copy;
    }

    public ArrayList<Integer> remove(ArrayList<Integer> a, ArrayList<Integer> b) { //fini
        ArrayList<Integer> c = new ArrayList<Integer>();

        for (int i = 0; i < a.size(); i++) {
            if (b.contains(a.get(i))) c.add(a.get(i));
        }

        return c;
    }

    public ArrayList<Integer> reverse(ArrayList<Integer> listToReverse) { //fini
        ArrayList<Integer> listReversed = new ArrayList<Integer>();
        for (int i = 1; i < 10; i++)
            if (!listToReverse.contains(i)) listReversed.add(i);
        return listReversed;
    }

    public boolean finito(int[][] tab) { //fini
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    if (tab[i][j] == tab[i][k] && j != k && tab[i][j] != 0) return false;
                    if (tab[j][i] == tab[k][i] && j != k && tab[j][i] != 0) return false;
                }
                if (tab[i][j] == 0) return false;
            }
        }
        return true;
    }

    public ArrayList<Integer> getValuesSquare(int x, int y, int[][] tab) { // normalement fini mais à verifier
        ArrayList<Integer> listCarre = new ArrayList<Integer>();
        int a = (x / 3) * 3;
        int b = (y / 3) * 3;

        for (int i = a; i < a + 3; i++) {
            for (int j = b; j < b + 3; j++) {
                if (i == x && j == y) continue;
                if (tab[i][j] != 0) listCarre.add(tab[i][j]);
            }
        }

        return reverse(listCarre);
    }

    public ArrayList<Integer> getValuesRow(int x, int[][] tab) { //fini
        ArrayList<Integer> listLig = new ArrayList<Integer>();
        for ( int i = 0; i < 9; i++ )
            listLig.add(tab[x][i]);
        return reverse(listLig);
    }

    public ArrayList<Integer> getValuesColumn(int y, int[][] tab) { //fini
        ArrayList<Integer> listCol = new ArrayList<Integer>();
        for ( int i = 0; i < 9; i++ )
            listCol.add(tab[i][y]);
        return reverse(listCol);
    }

    public int findByElim(int x, int y, int[][] tab, ArrayList<Integer> lstValuesPossibles) {
        return 0;
    }
}