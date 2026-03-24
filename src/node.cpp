#include "../include/node.h"
#include "../include/game.h"
#include <random>
#include <iostream>

Node::Node(char **map, int depth, int who, int operation)
{
    this->map = map;
    this->depth = depth;
    this->who = who;
    this->operation = operation;

    int x, y;

    if (depth != MAXDEPTH)
        branchOut();
    else
    {
        point = 0;
        for (int i = 0; i < 7; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                if (map[i][j] == 'A' || map[i][j] == 'H')
                {
                    for (int k = -1; k <= 1; k++)
                    {
                        y = i + k;

                        if (y < 0 || y > 6)
                            continue;

                        for (int l = -1; l <= 1; l++)
                        {
                            x = j + l;

                            if (x < 0 || x > 6)
                                continue;

                            if (map[y][x] == 'E')
                            {
                                if (map[i][j] == 'A')
                                    point++;
                                else if (map[i][j] == 'H')
                                    point--;
                            }
                        }
                    }
                }
            }
        }
    }
}

Node::~Node()
{
    for (int i = 0; i < 7; i++)
        delete[] map[i];
    delete[] map;
    for (Node *neighbour : neighbours)
        delete neighbour;
}

void Node::addLeafForMove(int i, int j, int previ, int prevj)
{
    char **tmp = copyMap();
    tmp[previ][prevj] = 'E';
    tmp[i][j] = who ? 'H' : 'A';

    neighbours.push_back(new Node(tmp, depth + 1, who, REMOVE));
}

void Node::addLeafForRemove(int i, int j)
{
    char **tmp = copyMap();
    tmp[i][j] = 'R';

    neighbours.push_back(new Node(tmp, depth + 1, !who, MOVE));
}

void Node::branchOut()
{
    addAllLeaf();

    if (neighbours.size() == 0)
    {
        if (who == AIPLAYER)
            point = -8;
        else
            point = 8;
        return;
    }

    if (who == AIPLAYER)
    {
        int max = -9;

        for (Node *neighbour : neighbours)
            if (neighbour->getPoint() > max)
                max = neighbour->getPoint();
        point = max;
    }
    else
    {
        int min = 9;

        for (Node *neighbour : neighbours)
            if (neighbour->getPoint() < min)
                min = neighbour->getPoint();

        point = min;
    }

    if (depth > 1)
        return;

    for (Node *neighbour : neighbours)
        if (point == neighbour->getPoint())
            targets.push_back(neighbour);
}

Node *Node::getTarget() const
{
    if (targets.size() == 0)
        return NULL;

    std::mt19937 generator(static_cast<unsigned int>(time(0)));
    std::uniform_int_distribution<int> distribution(0, targets.size() - 1);
    return targets[distribution(generator)];
}

const int &Node::getPoint() const
{
    return point;
}

char **Node::copyMap()
{
    char **copy = new char *[7];
    for (int i = 0; i < 7; i++)
    {
        copy[i] = new char[7];
        for (int j = 0; j < 7; j++)
            copy[i][j] = map[i][j];
    }
    return copy;
}

void Node::addAllLeaf()
{
    char c = operation ? (who ? 'A' : 'H') : (who ? 'H' : 'A');

    for (int i = 0; i < 7; i++)
    {
        for (int j = 0; j < 7; j++)
        {
            if (map[i][j] == c)
            {
                for (int k = -1; k <= 1; k++)
                {
                    int y = i + k;

                    if (y < 0 || y > 6)
                        continue;

                    for (int l = -1; l <= 1; l++)
                    {
                        int x = j + l;

                        if (x < 0 || x > 6)
                            continue;

                        if (map[y][x] == 'E')
                        {
                            if (operation == MOVE)
                                addLeafForMove(y, x, i, j);
                            else
                                addLeafForRemove(y, x);
                        }
                    }
                }
            }
        }
    }
}
