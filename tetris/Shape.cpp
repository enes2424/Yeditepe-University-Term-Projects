#include "Shape.hpp"


Shape::Shape(int** shape, int height, int width, Color color)
{
    heights[0] = height;
    widths[0] = width;
    shapes[0] = shape;
    heights[1] = width;
    widths[1] = height;
    shapes[1] = new int* [width];
    for (int i = 0; i < width; i++)
        shapes[1][i] = new int[height];
    for (int i = 0; i < width; i++)
        for (int j = 0; j < height; j++)
            shapes[1][i][j] = shape[height - 1 - j][i];
    heights[2] = height;
    widths[2] = width;
    shapes[2] = new int* [height];
    for (int i = 0; i < height; i++)
        shapes[2][i] = new int[width];
    for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++)
            shapes[2][i][j] = shape[height - 1 - i][width - 1 - j];
    heights[3] = width;
    widths[3] = height;
    shapes[3] = new int* [width];
    for (int i = 0; i < width; i++)
        shapes[3][i] = new int[height];
    for (int i = 0; i < width; i++)
        for (int j = 0; j < height; j++)
            shapes[3][i][j] = shape[j][width - 1 - i];
    this->color = color;
    this->shape = shape;
    this->height = height;
    this->width = width;
}

void    Shape::rotate(int status)
{
    shape = shapes[status];
    height = heights[status];
    width = widths[status];
}

Shape::~Shape(void)
{
    for (int i = 0; i < 1; i++) {
        for (int j = 0; j < heights[i]; j++)
            delete[] shapes[i][j];
        delete[] shapes[i];
    }
}

void    Shape::draw(int x, int y)
{
    for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++)
            if (shape[i][j])
                DrawRectangle((x + j) * 50, (y + i) * 50, 50, 50, color);
}

void    Shape::copy(CopyShape *copy_shape)
{
    if (copy_shape->shape) {
        for (int i = 0; i < copy_shape->height; i++)
            delete[] copy_shape->shape[i];
        delete[] copy_shape->shape;
    }
    copy_shape->color = color;
    copy_shape->height = height;
    copy_shape->width = width;
    copy_shape->shape = new int* [height];
    for (int i = 0; i < height; i++) {
        copy_shape->shape[i] = new int[width];
        for (int j = 0; j < width; j++)
            copy_shape->shape[i][j] = shape[i][j];
    }
}

int     Shape::getHeight(void)
{
    return height;
}

int     Shape::getWidth(void)
{
    return width;
}

Color   &Shape::getColor(void)
{
    return color;
}

int     Shape::get(int index1, int index2)
{
    return shape[index1][index2];
}