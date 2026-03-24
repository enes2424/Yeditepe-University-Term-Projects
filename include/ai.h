#ifndef AI_H
#define AI_H

#include "raylib.h"

class AI
{
public:
    AI(char **map);
    char **move();
    char **removeCell();

private:
    class Node *node;
    class Node *target;
    char **map;
};

#endif
