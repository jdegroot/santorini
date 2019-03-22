package nl.groot.santorini.bo;

import javafx.util.Pair;
import nl.groot.santorini.exception.GameRuleException;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by jdegroot on 2017-04-30.
 */
public class GameBoard {

    //region CONSTANTS
    public static final int MAX_BOARD_WIDTH = 5;
    public static final int MAX_BOARD_HEIGHT = 5;
    public static final int MAX_BUILD_HEIGHT = 4;
    public static final int WIN_BUILD_HEIGHT = 3;
    private static final String SPACE = " ";
    private static final String DIVIDER = "|";
    private static final String DOT = ".";
    private static final String NEW_LINE = "\n";
    //endregion

    //region ATTRIBUTES
    private BoardTile[][] board;
    private BoardTile moveTile;
    private TurnState turnState;
    private String errorMessage;
    //endregion

    //region CONSTRUCTION
    public GameBoard()
    {
        board = new BoardTile[MAX_BOARD_WIDTH][MAX_BOARD_HEIGHT];
        for (int i = 0; i < MAX_BOARD_WIDTH; i++)
        {
            for (int j = 0; j < MAX_BOARD_HEIGHT; j++)
            {
                board[i][j] = new BoardTile(i, j);
            }
        }
        turnState = TurnState.Player1Place;
    }
    //endregion

    //region GETTER & SETTER
    public BoardTile[][] getBoard()
    {
        return board;
    }

    public void setBoard(BoardTile[][] board)
    {
        this.board = board;
    }
    //endregion

    //region METHODS
    public TurnState getCurrentTurnState()
    {
        return turnState;
    }

    public TurnState getNextTurnState() throws IllegalStateException
    {
        TurnState result;

        switch (turnState)
        {
            case Player1Place:
            {
                if (playerUnitCount(BoardTile.PLAYER_1) != 2)
                {
                    result = TurnState.Player1Place;
                }
                else
                {
                    result = TurnState.Player2Place;
                }
                break;
            }
            case Player2Place:
            {
                if (playerUnitCount(BoardTile.PLAYER_2) != 2)
                {
                    result = TurnState.Player2Place;
                }
                else
                {
                    result = TurnState.Player1Move;
                }
                break;
            }
            case Player1Move:
            {
                result = TurnState.Player1Build;
                break;
            }
            case Player1Build:
            {
                result = TurnState.Player2Move;
                break;
            }
            case Player2Move:
            {
                result = TurnState.Player2Build;
                break;
            }
            case Player2Build:
            {
                result = TurnState.Player1Move;
                break;
            }
            default:
            {
                throw new IllegalStateException(String.format(Locale.CANADA,
                        "Don't know what the next turn state is based on current turn state %s", turnState.name()));
            }
        }

        return result;
    }

    public GameBoard processMove(String[] newPos)
    {
        int x = Integer.parseInt(newPos[0]);
        int y = Integer.parseInt(newPos[1]);

        return processMove(new Position(x, y));
    }

    public GameBoard processMove(Position position)
    {
        BoardTile tile = board[position.getX()][position.getY()];
        boolean nextState = true;

        try
        {
            switch (turnState)
            {
                case Player1Place:
                {
                    tile.setOccupied(BoardTile.PLAYER_1);
                    break;
                }
                case Player2Place:
                {
                    tile.setOccupied(BoardTile.PLAYER_2);
                    break;
                }
                case Player1Move:
                case Player2Move:
                {
                    if (moveTile == null)
                    {
                        checkSelectionLegal(tile);
                        moveTile = tile;
                        nextState = false;
                    }
                    else
                    {
                        checkMoveLegal(tile);
                        tile.setOccupied(moveTile.getOccupied());
                        moveTile.setOccupied(BoardTile.NO_PLAYER);
                        moveTile = tile;
                    }
                    break;
                }
                case Player1Build:
                case Player2Build:
                {
                    checkBuildLegal(tile);
                    tile.incrementHeight();
                    moveTile = null;
                    break;
                }
            }

            if (nextState)
            {
                turnState = getNextTurnState();
            }
        }

        catch (GameRuleException e)
        {
            errorMessage = e.getMessage();
        }

        return this;
    }

    public boolean isSetup()
    {
        return getCurrentTurnState() == TurnState.Player1Place || getCurrentTurnState() == TurnState.Player2Place;
    }

    public boolean isGameOver()
    {
        return moveTile != null && moveTile.getBuildHeight() == WIN_BUILD_HEIGHT && moveTile.isOccupied();
    }

    public int getWinner()
    {
        if (moveTile == null || moveTile.getBuildHeight() != WIN_BUILD_HEIGHT)
        {
            return  -1;
        }
        return moveTile.isPlayer1Occupied() ? BoardTile.PLAYER_1 : BoardTile.PLAYER_2;
    }
    //endregion

    //region IMPLEMENTATION
    private int playerUnitCount(int player)
    {
        int result = 0;
        for(int i = 0; i < MAX_BOARD_WIDTH; i++)
        {
            for (int j = 0; j < MAX_BOARD_HEIGHT; j++)
            {
                result += board[i][j].getOccupied() == player ? 1 : 0;
            }
        }
        return result;
    }

    private void checkMoveLegal(BoardTile tile) throws GameRuleException
    {
        int xDist = Math.abs(tile.getRow() - moveTile.getRow());
        int yDist = Math.abs(tile.getColumn() - moveTile.getColumn());
        int heightDiff = tile.getBuildHeight() - moveTile.getBuildHeight();
        if (xDist > 1 || yDist > 1)
        {
            throw new GameRuleException("Illegal move, you can move a maximum of 1 tile around your player");
        }
        if (xDist == 0 && yDist == 0)
        {
            throw new GameRuleException("Illegal move, you have to move from your current space");
        }
        if (heightDiff > 1)
        {
            throw new GameRuleException("Illegal move, you can only jump up 1 level");
        }
        if (tile.getBuildHeight() == MAX_BUILD_HEIGHT)
        {
            throw new GameRuleException("Illegal move, you can not move on top of a finished tower");
        }
    }

    private void checkBuildLegal(BoardTile tile) throws GameRuleException
    {
        int xDist = Math.abs(tile.getRow() - moveTile.getRow());
        int yDist = Math.abs(tile.getColumn() - moveTile.getColumn());
        if (xDist > 1 || yDist > 1)
        {
            throw new GameRuleException("Illegal move, you can only build 1 tile around your player");
        }
        if (tile.isOccupied())
        {
            throw new GameRuleException("Illegal move, can not build where a player stands");
        }
        if (tile.getBuildHeight() == 4)
        {
            throw new GameRuleException("Illegal move, towers can not be higher than 4");
        }
    }

    private void checkSelectionLegal(BoardTile tile) throws GameRuleException
    {
        if (tile.getOccupied() != (turnState == TurnState.Player1Move ? BoardTile.PLAYER_1 : BoardTile.PLAYER_2))
        {
            throw new GameRuleException(String.format("The position (%d,%d) has no player on it, choose another", tile.getRow(), tile.getColumn()));
        }
    }
    //endregion

    //region OVERRIDES
    @Override
    public boolean equals(Object other)
    {
        boolean result = false;

        if (other instanceof GameBoard)
        {
            GameBoard that = (GameBoard) other;
            result = this.turnState.equals(that.turnState) &&
                    Arrays.equals(this.board, that.board);
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        if (errorMessage != null)
        {
            builder.append(errorMessage);
            errorMessage = null;
        }
        else
        {
            builder.append(SPACE);
            builder.append(SPACE);
            builder.append(SPACE);
            builder.append(SPACE);
            builder.append(SPACE);
            for (int i = 0; i < MAX_BOARD_WIDTH; i++)
            {
                builder.append(i);
                builder.append(DOT);
                builder.append(SPACE);
                builder.append(SPACE);
                builder.append(DIVIDER);
                builder.append(SPACE);
            }

            builder.append(NEW_LINE);

            for (int row = 0; row < MAX_BOARD_HEIGHT; row++)
            {
                builder.append(row);
                builder.append(DOT);
                builder.append(SPACE);
                builder.append(DIVIDER);
                for (int column = 0; column < MAX_BOARD_WIDTH; column++)
                {
                    builder.append(SPACE);
                    builder.append(board[column][row].getTileDisplayForm());
                    builder.append(SPACE);
                    builder.append(DIVIDER);
                }
                builder.append(NEW_LINE);
            }
        }

        return builder.toString();
    }

    //endregion
}
