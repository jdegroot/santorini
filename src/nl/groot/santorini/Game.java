package nl.groot.santorini;

import nl.groot.santorini.agent.Agent;
import nl.groot.santorini.bo.GameBoard;

import java.util.Locale;

/**
 * Created by jdegroot on 2017-04-30.
 */
public class Game
{
    private static GameBoard gameBoard;

    public static void main(String[] args)
    {
        gameBoard = new GameBoard();
        System.out.println(String.format(Locale.CANADA, "Next to move: %s", gameBoard.getNextTurnState().name()));
        System.out.println(gameBoard.toString());

//        Scanner inputScanner = new Scanner(System.in);
//        String command = "";
//        boolean exit = false;
//        while (!exit)
//        {
//            command = inputScanner.next();
//            exit = processCommand(command);
//        }

        boolean exit = false;
        Agent agent1 = new Agent("Agent 1", true);
        Agent agent2 = new Agent("Agent 2", true);
        Agent[] agents = new Agent[] {agent1, agent2};
        setupGame(agent1);
        setupGame(agent2);
        int turn = 0;
        while (!exit)
        {
            exit = doTurn(agents[turn++ % 2]);
        }

        System.out.println(String.format("Game over! Player %d won the game in %d turns!", gameBoard.getWinner(), turn));
    }

    private static void setupGame(Agent agent)
    {
        gameBoard.processMove(agent.getNextMove(gameBoard));
        printGameState();
        gameBoard.processMove(agent.getNextMove(gameBoard));
        printGameState();
    }

    private static boolean doTurn(Agent agent)
    {
        // Select unit
        gameBoard.processMove(agent.getNextMove(gameBoard));
        // Move unit
        gameBoard.processMove(agent.getNextMove(gameBoard));
        printGameState();
        if (!gameBoard.isGameOver())
        {
            // Build
            gameBoard.processMove(agent.getNextMove(gameBoard));
            printGameState();
        }
        return gameBoard.isGameOver();
    }

    private static boolean processCommand(String command)
    {
        if ("exit".equals(command))
        {
            return true;
        }

        String[] newPos = command.split(",");
        if (newPos.length != 2)
        {
            System.out.println("Invalid position, format like \"x,y\"");
        }
        else
        {
            gameBoard.processMove(newPos);
        }

        System.out.println(String.format(Locale.CANADA, "Next to move: %s", gameBoard.getCurrentTurnState().name()));
        System.out.println(gameBoard.toString());
        return gameBoard.isGameOver();
    }

    private static void printGameState()
    {
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {

        }
        System.out.print("\033[H\033[2J");
        System.out.println(String.format(Locale.CANADA, "Next to move: %s", gameBoard.getCurrentTurnState().name()));
        System.out.println(gameBoard.toString());

    }
}
