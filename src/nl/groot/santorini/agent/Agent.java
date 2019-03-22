package nl.groot.santorini.agent;

import nl.groot.santorini.bo.BoardTile;
import nl.groot.santorini.bo.GameBoard;
import nl.groot.santorini.bo.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent
{

    private String name;
    private Position posUnit1;
    private Position posUnit2;
    private Position activePosition;
    private List<Position> legalMoves;
    private Random random;
    private int activeUnit;
    private boolean smart;

    public Agent(String name)
    {
        this(name, false);
    }

    public Agent(String name, boolean smart)
    {
        this.name = name;
        this.random = new Random();
        this.smart = smart;
    }


    public Position getNextMove(GameBoard gameBoard)
    {
        switch (gameBoard.getCurrentTurnState())
        {
            case Player1Place:
            case Player2Place:
            {
                return getPlacement(gameBoard);
            }
            case Player1Move:
            case Player2Move:
            {
                return getMove(gameBoard);
            }
            case Player1Build:
            case Player2Build:
            {
                return getBuild(gameBoard);
            }
        }

        return null;
    }

    private Position getPlacement(GameBoard gameBoard)
    {
        int x = random.nextInt(GameBoard.MAX_BOARD_WIDTH);
        int y = random.nextInt(GameBoard.MAX_BOARD_HEIGHT);

        if (gameBoard.getBoard()[x][y].isOccupied())
        {
            return getPlacement(gameBoard);
        }

        if (posUnit1 == null)
        {
            posUnit1 = new Position(x, y);
        }
        else
        {
            posUnit2 = new Position(x, y);
        }
        return new Position(x, y);
    }

    private Position getMove(GameBoard gameBoard)
    {
        if (activePosition == null)
        {
            activeUnit = random.nextInt(2);
            activePosition = activeUnit == 0 ? posUnit1 : posUnit2;
            legalMoves = getLegalMoves(activePosition, gameBoard);
            if (legalMoves.size() == 0)
            {
                activePosition = null;
                return getMove(gameBoard);
            }
        }
        else
        {
            Position newPosition = selectMove(gameBoard);
            if (activeUnit == 0)
            {
                posUnit1 = newPosition;
            }
            else
            {
                posUnit2 = newPosition;
            }
            activePosition = newPosition;
        }

        return activePosition;
    }

    private Position selectMove(GameBoard gameBoard)
    {
        Position move = null;
        if (smart)
        {
            for (Position option : legalMoves)
            {
                if (gameBoard.getBoard()[option.getX()][option.getY()].getBuildHeight() == GameBoard.WIN_BUILD_HEIGHT)
                {
                    move = option;
                    break;
                }
            }
        }

        if (move == null)
        {
            move = legalMoves.get(random.nextInt(legalMoves.size()));
        }
        return move;
    }

    private Position getBuild(GameBoard gameBoard)
    {
        List<Position> legalBuilds = getLegalBuild(gameBoard);
        Position result = legalBuilds.get(random.nextInt(legalBuilds.size()));
        activePosition = null;
        return result;
    }

    private List<Position> getLegalMoves(Position currentPos, GameBoard gameBoard)
    {
        List<Position> legalMoves = new ArrayList<>();
        BoardTile currentTile = gameBoard.getBoard()[currentPos.getX()][currentPos.getY()];

        for (int x = currentPos.getX() - 1; x <= currentPos.getX() + 1; x++)
        {
            for (int y = currentPos.getY() - 1; y <= currentPos.getY() + 1; y++)
            {
                if (x >= 0 && x < GameBoard.MAX_BOARD_WIDTH && y >= 0 && y < GameBoard.MAX_BOARD_HEIGHT)
                {
                    BoardTile tile = gameBoard.getBoard()[x][y];
                    if (!tile.isOccupied() && tile.getBuildHeight() - currentTile.getBuildHeight() <= 1)
                    {
                        legalMoves.add(new Position(x, y));
                    }
                }
            }
        }

        return legalMoves;
    }

    private List<Position> getLegalBuild(GameBoard gameBoard)
    {
        List<Position> legalBuilds = new ArrayList<>();

        for (int x = activePosition.getX() - 1; x <= activePosition.getX() + 1; x++)
        {
            for (int y = activePosition.getY() - 1; y <= activePosition.getY() + 1; y++)
            {
                if (x >= 0 && x < GameBoard.MAX_BOARD_WIDTH && y >= 0 && y < GameBoard.MAX_BOARD_HEIGHT)
                {
                    BoardTile tile = gameBoard.getBoard()[x][y];
                    if (!tile.isOccupied() && tile.getBuildHeight() < GameBoard.MAX_BUILD_HEIGHT)
                    {
                        legalBuilds.add(new Position(x, y));
                    }
                }
            }
        }

        return legalBuilds;
    }
}
