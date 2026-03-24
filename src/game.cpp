#include "../include/game.h"
#include "../include/ai.h"

#include <vector>
#include <cstdlib>

bool control(int &winStatus)
{
    (void)winStatus;
    return false;
}

Texture2D createTexture2D(const char *file)
{
    Image image = LoadImage(file);
    Texture2D texture = LoadTextureFromImage(image);
    UnloadImage(image);
    return texture;
}

int gameLoop()
{
    InitWindow(805, 805, "AITermProject");

    char **map = new char *[7]{new char[7]{'E', 'E', 'E', 'A', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'E', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'E', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'E', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'E', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'E', 'E', 'E', 'E'},
                               new char[7]{'E', 'E', 'E', 'H', 'E', 'E', 'E'}};

    Texture2D background = createTexture2D("images/Background.png");
    Texture2D human = createTexture2D("images/Human.png");
    Texture2D possibleMoves = createTexture2D("images/PossibleMoves.png");
    Texture2D removedCell = createTexture2D("images/RemovedCell.png");
    Texture2D aiTexture = createTexture2D("images/Ai.png");

    Vector2 mousePos;

    bool isItTheHumansTurn = false;
    bool isMoveOperation = true;

    int x, y;
    int humanX = 3, humanY = 6;
    int winStatus = 0;

    bool canMove;

    AI ai(map);

    SetTargetFPS(60);

    while (!WindowShouldClose())
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawTexture(background, 0, 0, WHITE);

        for (int i = 0; i < 7; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                if (map[i][j] == 'A')
                    DrawTexture(aiTexture, 100 * j + 100, 100 * i + 100, WHITE);
                else if (map[i][j] == 'H')
                    DrawTexture(human, 100 * j + 100, 100 * i + 100, WHITE);
                else if (map[i][j] == 'P')
                    DrawTexture(possibleMoves, 100 * j + 100, 100 * i + 100, WHITE);
                else if (map[i][j] == 'R')
                    DrawTexture(removedCell, 100 * j + 100, 100 * i + 100, WHITE);
            }
        }

        EndDrawing();

        if (!isItTheHumansTurn)
        {
            if (isMoveOperation)
            {
                map = ai.move();

                if (map == NULL)
                {
                    winStatus = 2;
                    break;
                }
            }
            else
                map = ai.removeCell();
        }
        else if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            mousePos = GetMousePosition();
            x = (int)(mousePos.x / 100) - 1;
            y = (int)(mousePos.y / 100) - 1;

            if (x < 0 || y < 0 || x > 6 || y > 6)
                continue;

            if (isMoveOperation)
            {
                if (map[y][x] != 'P')
                    continue;

                map[humanY][humanX] = 'E';

                humanX = x;
                humanY = y;

                map[humanY][humanX] = 'H';

                for (int i = 0; i < 7; i++)
                    for (int j = 0; j < 7; j++)
                        if (map[i][j] == 'P')
                            map[i][j] = 'E';
            }
            else
            {
                if (map[y][x] == 'E')
                    map[y][x] = 'R';
                else
                    continue;
            }
        }
        else
            continue;

        isMoveOperation = !isMoveOperation;

        if (isMoveOperation)
            isItTheHumansTurn = !isItTheHumansTurn;

        if (isItTheHumansTurn && isMoveOperation)
        {
            canMove = false;

            for (int i = -1; i <= 1; i++)
            {
                y = humanY + i;

                if (y < 0 || y > 6)
                    continue;

                for (int j = -1; j <= 1; j++)
                {
                    x = humanX + j;

                    if (x < 0 || x > 6)
                        continue;

                    if (map[y][x] == 'E')
                    {
                        canMove = true;
                        map[y][x] = 'P';
                    }
                }
            }

            if (!canMove)
            {
                winStatus = 1;
                break;
            }
        }
    }

    if (map != NULL)
    {
        for (int i = 0; i < 7; i++)
            delete[] map[i];

        delete[] map;
    }

    UnloadTexture(background);
    UnloadTexture(human);
    UnloadTexture(possibleMoves);
    UnloadTexture(removedCell);
    UnloadTexture(aiTexture);

    return winStatus;
}

void gameEnd(int winStatus)
{
    if (winStatus == 0)
        return CloseWindow();

    Texture2D background;

    if (winStatus == 1)
        background = createTexture2D("images/Player1Win.png");
    else if (winStatus == 2)
        background = createTexture2D("images/Player2Win.png");

    while (!WindowShouldClose())
    {
        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawTexture(background, 0, 0, WHITE);
        EndDrawing();

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
            break;
    }

    UnloadTexture(background);

    return CloseWindow();
}
