#ifndef NODE_H
#define NODE_H

#include <vector>

#define AIPLAYER 0
#define HUMANPLAYER 1

#define MOVE 0
#define REMOVE 1

#define MAXDEPTH 7

class Node
{
public:
    Node(char **map, int depth, int who, int operation);
    ~Node();
    Node *getTarget() const;
    const int &getPoint() const;
    char **copyMap();

private:
    std::vector<Node *> neighbours;
    std::vector<Node *> targets;
    char **map;
    int depth;
    int point;
    int who;
    int operation;
    void branchOut();
    void addAllLeaf();
    void addLeafForMove(int i, int j, int previ, int prevj);
    void addLeafForRemove(int i, int j);
};

#endif
