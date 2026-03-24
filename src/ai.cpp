#include "../include/ai.h"
#include "../include/node.h"

#include <thread>

AI::AI(char **map)
{
    this->map = map;
}

char **AI::move()
{
    std::this_thread::sleep_for(std::chrono::milliseconds(500));
    node = new Node(map, 0, AIPLAYER, MOVE);

    target = node->getTarget();

    if (target == NULL)
    {
        delete node;
        return NULL;
    }

    map = (target = node->getTarget())->copyMap();
    return map;
}

char **AI::removeCell()
{
    std::this_thread::sleep_for(std::chrono::seconds(1));
    for (int i = 0; i < 7; i++)
        delete[] map[i];
    delete[] map;
    map = target->getTarget()->copyMap();
    delete node;
    return map;
}
