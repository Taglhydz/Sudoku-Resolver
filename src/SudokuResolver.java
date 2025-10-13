import java.lang.reflect.Array;
import java.util.*;

public class SudokuResolver implements ISudokuResolver {
    private ISudokuDisplayer sudokuDisplayer;

    public SudokuResolver(ISudokuDisplayer sudokuDisplayer) {
        this.sudokuDisplayer = sudokuDisplayer;
    }

    public boolean resolve(int[][] sudoku) {

        while (!finito(sudoku)) {
            sudokuDisplayer.display(sudoku);

            boolean changed = false;

            for (int i = 0; i < 9; i++ ) { // tout parcourir le sudoku(sudoku)
                for (int j = 0; j < 9; j++) {
                    if (sudoku[i][j] == 0) { //si la case est vide

                        ArrayList<Integer> elim = new ArrayList<Integer>(this.remove(remove(getValuesSquare(i, j, sudoku), getValuesRow(i, sudoku)), getValuesColumn(j, sudoku)));
                        
                        if (elim.size() == 1) {
                            sudoku[i][j] = elim.get(0);
                            changed = true;
                        }
                    }
                }
            }

            // méthode par carré en enlevant les possibilités grace au lignes et colonnes autour
            for (int carreX = 0; carreX < 3; carreX++) {
                for (int carreY = 0; carreY < 3; carreY++) {
                    int startX = carreX * 3;
                    int startY = carreY * 3;
                    
                    ArrayList<List<Object>> lstValuesPossibles = new ArrayList<List<Object>>();
                    
                    // prendre les possibilités dans chaque carré
                    for (int k = startX; k < startX + 3; k++) {
                        for (int l = startY; l < startY + 3; l++) {
                            if (sudoku[k][l] == 0) {
                                ArrayList<Integer> elim = new ArrayList<Integer>(this.remove(remove(getValuesSquare(k, l, sudoku), getValuesRow(k, sudoku)), getValuesColumn(l, sudoku)));
                                lstValuesPossibles.add(Arrays.asList(k, l, elim));
                            }
                        }
                    }
                    
                    // trouver les valeurs uniques
                    for (int value = 1; value <= 9; value++) {
                        int nbTimes = 0;
                        int posX = -1, posY = -1;
                        
                        // compte le nombre de fois qu'apparait la valeur
                        for (List<Object> entry : lstValuesPossibles) {
                            @SuppressWarnings("unchecked")
                            ArrayList<Integer> possibilities = (ArrayList<Integer>) entry.get(2);
                            
                            if (possibilities.contains(value)) {
                                nbTimes++;
                                posX = (int) entry.get(0);
                                posY = (int) entry.get(1);
                            }
                        }
                        
                        // si la valeur apparaît une seule fois, l'ajouter au sudoku
                        if (nbTimes == 1 && posX != -1 && posY != -1) {
                            sudoku[posX][posY] = value;
                            changed = true;
                        }
                    }
                }
            }

            if (!changed) break;
        }

        sudokuDisplayer.display(sudoku);
        return finito(sudoku);
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