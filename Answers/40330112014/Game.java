import java.util.Scanner;

public class Game {
    Utils utils = new Utils();
    AIPlayer aiPlayer;
    Player firstPlayer;
    Player secondPlayer;
    int size, mode, player1Hit = 0, player2Hit = 0, aiHit = 0;
    boolean randomized;

    /**
     * Method for starting the game and replay feature.
     */

    public void start() {
        boolean playAgain;
        do {
            System.out.println("Welcome to the battleship game");
            playGame();
            playAgain = askReplay();
        } while (playAgain);
    }

    private boolean askReplay() {
        System.out.println("Play again? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().equalsIgnoreCase("yes");
    }

    /**
     * The main method of game for playing it, game loop and...
     */

    private void playGame() {
        Ship ship = new Ship();
        MainMenu mainMenu = new MainMenu();
        Scanner input = new Scanner(System.in);
        Scanner numScanner = new Scanner(System.in);
        mainMenu.menu();
        mode = numScanner.nextInt();
        if (mode == 1 || mode == 2) {
            String shipPlacement;
            do {
                System.out.print("Ship placement(random or manual): ");
                shipPlacement = input.nextLine();
                if (!shipPlacement.equalsIgnoreCase("random") &&
                        !shipPlacement.equalsIgnoreCase("manual")) {
                    System.out.println("Invalid ship placement value. Please try again.");
                }
            } while (!shipPlacement.equalsIgnoreCase("random") &&
                    !shipPlacement.equalsIgnoreCase("manual"));
            randomized = shipPlacement.equalsIgnoreCase("random");
        }
        if (mode == 1) {
            size = utils.getGridSize();
            System.out.print("Enter the first player's name: ");
            String firstPlayerName = input.nextLine();
            firstPlayer = new Player(firstPlayerName, size);
            if (randomized) {
                ship.placeShipsRandom(firstPlayer.getboard().getGrid(), size);
            } else {
                ship.placeShipByUser(firstPlayer.getboard().getGrid(), size);
            }
            System.out.print("Enter the second player's name: ");
            String secondPlayerName = input.nextLine();
            secondPlayer = new Player(secondPlayerName, size);
            if (randomized) {
                ship.placeShipsRandom(secondPlayer.getboard().getGrid(), size);
            } else {
                ship.placeShipByUser(secondPlayer.getboard().getGrid(), size);
            }

            boolean player1Turn = true;
            while (!isGameOver()) {
                if (player1Turn) {
                    System.out.println(firstPlayerName + "'s turn:");
                    firstPlayer.getTrackingBoard().printGrid(firstPlayer.getTrackingBoard().getGrid(), size);
                    boolean validMove = playerTurn(secondPlayer.getboard().getGrid(), firstPlayer.getTrackingBoard().getGrid(), firstPlayer);
                    if (validMove) {
                        player1Turn = !player1Turn;
                    }
                } else {
                    System.out.println(secondPlayerName + "'s turn:");
                    secondPlayer.getTrackingBoard().printGrid(secondPlayer.getTrackingBoard().getGrid(), size);
                    boolean validMove = playerTurn(firstPlayer.getboard().getGrid(), secondPlayer.getTrackingBoard().getGrid(), secondPlayer);
                    if (validMove) {
                        player1Turn = !player1Turn;
                    }
                }
            }
            System.out.println("Game Over!");
            if (!player1Turn) {
                System.out.println(firstPlayerName + " wins!");
            } else {
                System.out.println(secondPlayerName + " wins!");
            }
        } else if (mode == 2) {
            size = utils.getGridSize();
            System.out.print("Enter the first player's name: ");
            String firstPlayerName = input.nextLine();
            firstPlayer = new Player(firstPlayerName, size);
            if (randomized) {
                ship.placeShipsRandom(firstPlayer.getboard().getGrid(), size);
            } else {
                ship.placeShipByUser(firstPlayer.getboard().getGrid(), size);
            }
            String secondPlayerName = "AI";
            aiPlayer = new AIPlayer(secondPlayerName, size);
            ship.placeShipsRandom(aiPlayer.getboard().getGrid(), size);

            boolean player1Turn = true;
            while (!isGameOver()) {
                if (player1Turn) {
                    System.out.println(firstPlayerName + "'s turn:");
                    firstPlayer.getTrackingBoard().printGrid(firstPlayer.getTrackingBoard().getGrid(), size);
                    boolean validMove = playerTurn(aiPlayer.getboard().getGrid(), firstPlayer.getTrackingBoard().getGrid(), firstPlayer);
                    if (validMove) {
                        player1Turn = !player1Turn;
                    }
                } else {
                    System.out.println(secondPlayerName + "'s turn:");
                    aiPlayer.getTrackingBoard().printGrid(aiPlayer.getTrackingBoard().getGrid(), size);
                    boolean validMove = AITurn(firstPlayer.getboard().getGrid(), aiPlayer.getTrackingBoard().getGrid(), aiPlayer);
                    if (validMove) {
                        player1Turn = !player1Turn;
                    }
                }
            }
            System.out.println("Game Over!");
            if (!player1Turn) {
                System.out.println(firstPlayerName + " wins!");
            } else {
                System.out.println(secondPlayerName + " wins!");
            }
        } else if (mode != 1 || mode != 2) {
            System.out.println("Invalid input! Please enter a valid option!");
        } else {
            playGame();
        }
    }

    /**
     * The Method is for checking that does all of ship's of one of the players are sunk or not.
     */

    private boolean isGameOver() {
        if (mode == 1) {
            if (!(utils.allShipsSunk(firstPlayer.getTrackingBoard().getGrid(), size) &&
                    !(utils.allShipsSunk(secondPlayer.getTrackingBoard().getGrid(), size)))) {
                return false;
            } else {
                return true;
            }
        } else if (mode == 2) {
            if (!(utils.allShipsSunk(firstPlayer.getTrackingBoard().getGrid(), size) &&
                    !(utils.allShipsSunk(aiPlayer.getTrackingBoard().getGrid(), size)))) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    /**
     * The Method is taking input from user and check the conditions.
     */

    private boolean playerTurn(char[][] opponentGrid, char[][] trackingGrid, Player player) {
        Scanner strScanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter target (e.g A1): ");
            String target = strScanner.nextLine();
            if (utils.isValidInput(target, size)) {
                Coordinate coordinate = new Coordinate(target.charAt(0), target.substring(1), size);
                int row = coordinate.getY();
                int col = coordinate.getX();
                if (trackingGrid[row][col] == 'H' || trackingGrid[row][col] == 'M') {
                    System.out.println("Invalid move! You have already tried this target.");
                    continue;
                }
                if (opponentGrid[row][col] == 'S') {
                    System.out.println("Hit!");
                    trackingGrid[row][col] = 'H';
                    player.getTrackingBoard().setGrid(trackingGrid);
                    if (player == firstPlayer) {
                        player1Hit++;
                    } else {
                        player2Hit++;
                    }
                    return true;
                } else {
                    System.out.println("Miss!");
                    trackingGrid[row][col] = 'M';
                    player.getTrackingBoard().setGrid(trackingGrid);
                    if (player == firstPlayer) {
                        player1Hit = 0;
                    } else {
                        player2Hit = 0;
                    }
                    return true;
                }
            } else {
                System.out.println("Invalid input");
                System.out.println("try one more time");
            }
        }
    }

    /**
     * The Method is taking input from AI and check the conditions.
     */

    private boolean AITurn(char[][] opponentGrid, char[][] trackingGrid, Player player) {
        while (true) {
            String target = aiPlayer.makeMove(size);
            if (utils.isValidInput(target, size)) {
                Coordinate coordinate = new Coordinate(target.charAt(0), target.substring(1), size);
                int row = coordinate.getY();
                int col = coordinate.getX();

                if (opponentGrid[row][col] == 'S') {
                    System.out.println("Hit");
                    trackingGrid[row][col] = 'H';
                    player.getTrackingBoard().setGrid(trackingGrid);
                    aiHit++;
                    return true;
                } else {
                    System.out.println("Miss");
                    trackingGrid[row][col] = 'H';
                    player.getTrackingBoard().setGrid(trackingGrid);
                    aiHit = 0;
                    return true;
                }
            } else {
                System.out.println("Invalid input");
                System.out.println("try one more time");
            }
        }
    }
}