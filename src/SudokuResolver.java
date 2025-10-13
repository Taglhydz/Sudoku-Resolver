import java.util.*;

public class SudokuResolver implements ISudokuResolver {
    private ISudokuDisplayer sudokuDisplayer;

    public SudokuResolver(ISudokuDisplayer sudokuDisplayer) {
        this.sudokuDisplayer = sudokuDisplayer;
    }

    public boolean resolve(int[][] sudoku) {
        sudokuDisplayer.display(sudoku);
        //moi qui fait
        int itr = 0;
        int nbErr = 0;
        int nbCorrect = 0;
        int tmpI = 0;
        int tmpJ = 0;
        int maxNbErr = 40;
        int[][] cloneSudoku;
        boolean sudokuResolu = false;
        ArrayList<Integer> tmpRm = new ArrayList<Integer>();


        while (!finito(sudoku) /*itr < 400*/){
            for (int i = 0; i < 9; i++ ){ // tout parcourir le sudoku(sudoku)
                for (int j = 0; j < 9; j++){
                    if (sudoku[i][j] == 0){ //si la case est vide
                        ArrayList<Integer> elim = new ArrayList<Integer>(this.remove(remove(getValuesSquare(i, j, sudoku), getValuesRow(i, sudoku)), getValuesColumn(j, sudoku)));
                        if (elim.size() < 1) return finito(sudoku);

                        else if (elim.size() > 1) {
                            nbErr++;
                            
                            if (nbErr > maxNbErr) {
                                for (int k = 0; k < elim.size(); k++)
                                {
                                    cloneSudoku = sudoku.clone();
                                    cloneSudoku[i][j] = elim.get(k);
                                    sudokuResolu = resolve(cloneSudoku);
                                    if (sudokuResolu) {
                                        sudoku = cloneSudoku;
                                        return true;
                                    }
                                }
                            } 
                        }
                        else sudoku[i][j] = elim.get(0); //rempli le sudoku d'une valeur trouver
                    }
                }
            }
            // itr++;
        }

        sudokuDisplayer.display(sudoku);
        return finito(sudoku);
    }

    public ArrayList<Integer> remove(ArrayList<Integer> a, ArrayList<Integer> b) { //fini à vérifier
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
}