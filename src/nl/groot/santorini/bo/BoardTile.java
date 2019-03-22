package nl.groot.santorini.bo;

import nl.groot.santorini.exception.GameRuleException;

/**
 * Created by jdegroot on 2017-04-30.
 */
public class BoardTile
{

    //region CONSTANTS
    public static final int NO_PLAYER = 0;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    //endregion

    //region ATTRIBUTES
    private int row;
    private int column;
    private int buildHeight;
    private int occupied;
    //endregion

    //region CONSTRUCTION
    public BoardTile(int row, int column)
    {
        this.row = row;
        this.column = column;
        this.buildHeight = 0;
    }
    //endregion

    //region GETTER & SETTER
    public int getRow()
    {
        return row;
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn(int column)
    {
        this.column = column;
    }

    public int getBuildHeight()
    {
        return buildHeight;
    }

    public void setBuildHeight(int buildHeight)
    {
        this.buildHeight = buildHeight;
    }

    public boolean isPlayer1Occupied()
    {
        return occupied == PLAYER_1;
    }

    public boolean isPlayer2Occupied()
    {
        return occupied == PLAYER_2;
    }

    public boolean isOccupied()
    {
        return occupied != NO_PLAYER;
    }

    public void setOccupied(int player) throws GameRuleException
    {
        if (player == NO_PLAYER)
        {
            this.occupied = player;
        }
        else if (!isPlayer1Occupied() && !isPlayer2Occupied())
        {
            this.occupied = player;
        }
        else
        {
            throw new GameRuleException(String.format("The position (%d, %d) is already taken, pick another", row, column));
        }
    }

    public int getOccupied()
    {
        return occupied;
    }

    public void incrementHeight()
    {
        setBuildHeight(getBuildHeight() + 1);
    }
    //endregion

    //region METHODS
    public String getTileDisplayForm()
    {
        return buildHeight + " " + (occupied == PLAYER_1 ? "x" : (occupied == PLAYER_2 ? "o" : " "));
    }
    //endregion

    //region OVERRIDES
    @Override
    public int hashCode()
    {
        return (42 * (42 + getRow() + getColumn() + getBuildHeight()));
    }

    @Override
    public boolean equals(Object other)
    {
        boolean result = false;
        if (other instanceof BoardTile)
        {
            BoardTile that = (BoardTile) other;
            result = this.row == that.row &&
                    this.column == that.column &&
                    this.buildHeight == that.buildHeight &&
                    this.occupied == that.occupied;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "BoardTile{" +
                "row = " + row +
                ", column = " + column +
                ", buildHeight = " + buildHeight +
                ", occupied = " + occupied +
                '}';
    }
    //endregion
}
