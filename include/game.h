#ifndef GAME_H
#define GAME_H

struct Texture createTexture2D(const char *file);
int gameLoop();
void gameEnd(int winStatus);

#endif
