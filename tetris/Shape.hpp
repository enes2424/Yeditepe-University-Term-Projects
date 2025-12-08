#ifndef SHAPE
#define SHAPE
#include <raylib.h>

struct CopyShape {
    int   width;
    int   height;
    Color color;
    int** shape;
};

class Shape {
private:
    int   width;
    int   height;
    Color color;
    int** shape;
    int   widths[4];
    int   heights[4];
    int** shapes[4];

public:
    Shape(int** shape, int height, int width, Color color);
    ~Shape(void);
    void    draw(int x, int y);
    int     getHeight(void);
    int     getWidth(void);
    Color   &getColor(void);
    int     get(int index1, int index2);
    void    rotate(int status);
    void    copy(CopyShape *copy_shape);
};

#endif