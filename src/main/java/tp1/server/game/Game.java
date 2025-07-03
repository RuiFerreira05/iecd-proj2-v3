package tp1.server.game;

import tp1.server.Connection;

public class Game {

    private char[][] board; // READ-ONLY
    private Connection c1;
    private Connection c2;
    private char player1Char;
    private char player2Char;

    public Game(Connection c1, Connection c2) {
        this.initBoard();
        this.c1 = c1;
        this.c2 = c2;
        this.player1Char = 'b';
        this.player2Char = 'w';
    }
    
    /**
     * Esta função tem o propósito de inicializar o array do tabuleiro de jogo
     * e preenche-lo com caracteres vazios
     * 
     * É chamada no construtor da classe
     */
    private void initBoard() {
        this.board = new char[15][15];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
        }
    }

    /**
     * Esta função tem o proposito de verificar se o jogo foi ganho por algum jogador
     * retornando o character do jogador que ganhou, ou 'n' se o jogo ainda não foi ganho
     * 
     * @return char - character do jogador que ganhou, ou 'n' se o jogo ainda não foi ganho
     */
    public char isGameWon() {
        // Verificação linha a linha
        for (int row = 0; row < board.length; row++) {
            char playerInAnalisis = ' ';
            int counter = 1;
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == playerInAnalisis && playerInAnalisis != ' ') {
                    counter++;
                    if (counter == 5) {
                        return playerInAnalisis;
                    }
                } else {
                    playerInAnalisis = board[row][col];
                    counter = 1;
                }
            }
        }
        // Verificação coluna a coluna
        for (int col = 0; col < board[0].length; col++) {
            char playerInAnalisis = ' ';
            int counter = 1;
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] == playerInAnalisis && playerInAnalisis != ' ') {
                    counter++;
                    if (counter == 5) {
                        return playerInAnalisis;
                    }
                } else {
                    playerInAnalisis = board[row][col];
                    counter = 1;
                }
            }
        }
        // Verificação diagonal descendente
        for (int col = 0; col < board.length - 4; col++) {
            char playerInAnalisis = ' ';
            int counter = 1;
            for (int row = 0; row < board.length - col; row++) {
                if (board[row][col + row] == playerInAnalisis && playerInAnalisis != ' ') {
                    counter++;
                    if (counter == 5) {
                        return playerInAnalisis;
                    }
                } else {
                    playerInAnalisis = board[row][col + row];
                    counter = 1;
                }
            }
        }
        // Verificação diagonal ascendente
        for (int col = 0; col < board.length - 4; col++) {
            int colDesc = board.length-1 - col;
            char playerInAnalisis = ' ';
            int counter = 1;
            for (int row = 0; row < board.length - col; row++) {
                if (board[row][colDesc - row] == playerInAnalisis && playerInAnalisis != ' ') {
                    counter++;
                    if (counter == 5) {
                        return playerInAnalisis;
                    }
                } else {
                    playerInAnalisis = board[row][colDesc - row];
                    counter = 1;
                }
            }
        }
        return 'n';
    }

    /**
     * Esta função tem o proposito de verificar se a jogada é válida, e se for, 
     * coloca o character do jogador na posição desejada
     * 
     * @param playerChar - character do jogador que está a jogar
     * @param row - linha onde o jogador quer jogar
     * @param col - coluna onde o jogador quer jogar
     * @return boolean - true se a jogada foi válida, false caso contrário
     */
    public boolean play(char playerChar, int row, int col) {
        if (row < 0 || col < 0) {
            return false;
        }
        if (row > board.length-1 || col > board.length-1) {
            return false;
        }
        if (board[row][col] == ' ' && row <= board.length-1 && col <= board.length-1) {
            board[row][col] = playerChar;
            return true;
        }
        return false;
    }

    /**
     * Esta função tem o proposito de verificar se o jogo terminou em empate
     * Começa por verificar se o tabuleiro encontra-se cheio, e depois verifica 
     * se o jogo não foi ganho por algum jogador
     * 
     * @return boolean - true se o jogo terminou em empate, false caso contrário
     */
    public boolean isTie() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        if (isGameWon() != 'n') {
            return false;
        }
        return true;
    }

    /**
     * Esta função tem o proposito de mostrar o tabuleiro na consola,
     * mostrando os characteres dos jogadores, e '+' para as posições vazias
     */
    public void displayBoard() {
        for (char[] row : board) {
            for (char col : row) {
                if (col == ' ') {
                    System.out.print("+ ");
                } else {
                    System.out.print(col + " ");
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Converte o tabuleiro do jogo para uma string.
     * Cada célula é representada por '1' para o player 1, '2' para o player 2 e '0' para células vazias.
     * @return String - representação do tabuleiro
     */
    public String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == player1Char) {
                    sb.append('1');
                } else if (cell == player2Char) {
                    sb.append('2');
                } else {
                    sb.append('0');
                }
            }
        }
        return sb.toString().trim();
    }

    // --------------- getters and setters -------------------

    public char[][] getBoard() {
        return board;
    }

    public char getPlayer1Char() {
        return player1Char;
    }

    public char getPlayer2Char() {
        return player2Char;
    }

    public void setPlayer1Char(char player1) {
        this.player1Char = player1;
    }

    public void setPlayer2Char(char player2) {
        this.player2Char = player2;
    }

    public Connection getC1() {
        return c1;
    }

    public Connection getC2() {
        return c2;
    }

    // public static void main(String[] args) {
    //     Game game = new Game();
    //     game.play('b', 1, 1);
    //     game.play('b', 2, 2);
    //     game.play('b', 3, 3);
    //     game.play('b', 4, 4);
    //     game.play('b', 5, 5);
    //     game.displayBoard();
    //     System.out.println(game.isTie() ? "Empate" : "Não é empate");
    //     System.out.println(game.isGameWon());
    // }
}
