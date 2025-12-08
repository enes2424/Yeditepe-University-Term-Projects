#include <iostream>
#include "Color.hpp"
#include <random>

using namespace std;

void    draw_board(ColorMatrix& color_matrix) {
    for (int i = 0; i < 16; i++)
        for (int j = 0; j < 12; j++)
            if (color_matrix.get(i, j))
                DrawRectangle(j * 50, i * 50, 50, 50, color_matrix.get(i, j)->color);
}

bool    is_collison(ColorMatrix &color_matrix, Shape &shape, int x, int y) {
    if (y + shape.getHeight() > 16 || shape.getWidth() + x > 12 || x < 0)
        return true;
    for (int i = 0; i < shape.getHeight(); i++)
        for (int j = 0; j < shape.getWidth(); j++)
            if (shape.get(i, j) && color_matrix.get(y + i, x + j))
                return true;
    return false;
}

void    save(ColorMatrix& color_matrix, Shape& shape, int x, int y, int &score, int &level, int &z, bool &is_game_over) {
    Color none = WHITE;
    for (int i = 0; i < shape.getHeight(); i++)
        for (int j = 0; j < shape.getWidth(); j++)
            if (shape.get(i, j))
                color_matrix.set(y + i, x + j, shape.getColor(), 1);
    for (int i = 0; i < 12; i++)
        if (color_matrix.get(0, i)) {
            is_game_over = true;
            return ;
        }
    int count = 0;
    for (int i = 0; i < 16; i++) {
        bool a = true;
        for (int j = 0; j < 12; j++)
            if (!color_matrix.get(i, j))
                a = false;
        if (a) {
            count++;
            score += 100;
            level++;
            z -= 50;
            if (z == 0)
                exit(0);
            for (int k = 0; k < 12; k++)
                if (color_matrix.get(i, k))
                    delete color_matrix.get(i, k);
            for (int k = i - 1; k >= 0; k--)
                for (int n = 0; n < 12; n++) {
                    if (color_matrix.get(k, n))
                        color_matrix.set(k + 1, n, color_matrix.get(k, n)->color, 1);
                    else
                        color_matrix.set(k + 1, n, none, 0);
                }
            for (int k = 0; k < 12; k++)
                color_matrix.set(0, k, none, 0);
        }
    }
    if (count > 1)
        score += (count - 1) * 100;
}

int main() {

    InitWindow(900, 800, "Mikail 211 Project");
    Image image = LoadImage("background.png");
    Image game_over_image = LoadImage("gameover.png");
    Texture2D texture = LoadTextureFromImage(image);
    Texture2D game_over_texture = LoadTextureFromImage(game_over_image);
    UnloadImage(image);
    UnloadImage(game_over_image);

    Shape *shape;
    Shape* next_shape;
    Shape* tmp;
    CopyShape copy_shape;
    copy_shape.shape = NULL;
    ShapeList shape_list;
    ColorMatrix color_matrix;
    random_device rd;
    mt19937 gen(rd());
    uniform_int_distribution<int> distribution(0, shape_list.size - 1);
    bool    is_game_over = false;
    int     a = 0;
    int     y = 0;
    int     score = 0;
    int     moving_loop = 0; //Each time this variable becomes 1000 the figure goes down one step
    int     random_shape = distribution(gen);
    shape = shape_list[random_shape];
    random_shape = distribution(gen);
    next_shape = shape_list[random_shape];
    next_shape->rotate(0);
    next_shape->copy(&copy_shape);
    shape->rotate(0);
    int rotation_status = 0;
    uniform_int_distribution<int> distribution2(0, 12 - shape->getWidth());
    int    x = distribution2(gen);
    int    z = 1000;
    int    level = 1;
    InitAudioDevice();
    Music music = LoadMusicStream("tetris.wav");
    Sound blockSound = LoadSound("trung.wav");
    Music game_over_music = LoadMusicStream("game-over.wav");
    PlayMusicStream(music);
    PlayMusicStream(game_over_music);
    bool    right_to_change_shape = true;
    while (WindowShouldClose() == false) {
        if (!is_game_over) {
            UpdateMusicStream(music);
            if (a == 80) {
                if (IsKeyDown(KEY_RIGHT) || IsKeyDown(KEY_D)) {
                    x++;
                    if (is_collison(color_matrix, *shape, x, y))
                        x--;
                }
                if (IsKeyDown(KEY_LEFT) || IsKeyDown(KEY_A)) {
                    x--;
                    if (is_collison(color_matrix, *shape, x, y))
                        x++;
                }
                if (IsKeyDown(KEY_DOWN) || IsKeyDown(KEY_S)) {
                    y++;
                    if (is_collison(color_matrix, *shape, x, y)) {
                        PlaySound(blockSound);
                        y--;
                        save(color_matrix, *shape, x, y, score, level, z, is_game_over);
                        right_to_change_shape = true;
                        y = 0;
                        shape = next_shape;
                        random_shape = distribution(gen);
                        next_shape = shape_list[random_shape];
                        next_shape->rotate(0);
                        next_shape->copy(&copy_shape);
                        shape->rotate(0);
                        rotation_status = 0;
                        uniform_int_distribution<int> distribution2(0, 12 - shape->getWidth());
                        x = distribution2(gen);
                    }
                }
                if (IsKeyDown(KEY_UP) || IsKeyDown(KEY_W)) {
                    rotation_status++;
                    if (rotation_status == 4)
                        rotation_status = 0;
                    shape->rotate(rotation_status);
                    if (is_collison(color_matrix, *shape, x, y)) {
                        cout << "This shape is not rotatable!!" << endl;
                        rotation_status--;
                        if (rotation_status == -1)
                            rotation_status = 0;
                        shape->rotate(rotation_status);
                    }
                }
                if (IsKeyDown(KEY_SPACE) && right_to_change_shape) {
                    tmp = shape;
                    shape = next_shape;
                    next_shape = tmp;
                    if (is_collison(color_matrix, *shape, x, y)) {
                        tmp = shape;
                        shape = next_shape;
                        next_shape = tmp;
                    }
                    else {
                        next_shape->rotate(0);
                        next_shape->copy(&copy_shape);
                        shape->rotate(0);
                        right_to_change_shape = false;
                    }
                }
                a = 0;
            }
            BeginDrawing();
            ClearBackground(RAYWHITE);
            DrawTexture(texture, 0, 0, WHITE);
            DrawText("L E V E L", 680, 70, 30, BLACK);
            DrawText(TextFormat("%d", level), 648 + (202 - MeasureText(TextFormat("%d", level), 40)) / 2, 110, 40, BLACK);
            DrawText("S C O R E", 675, 478, 30, BLACK);
            DrawText(TextFormat("%d", score), 648 + (202 - MeasureText(TextFormat("%d", score), 40)) / 2, 515, 40, BLACK);
            for (int i = 0; i < copy_shape.height; i++)
                for (int j = 0; j < copy_shape.width; j++)
                    if (copy_shape.shape[i][j])
                        DrawRectangle(50 * j + 648 + (202 - copy_shape.width * 50) / 2, 50 * i + 225 + (202 - copy_shape.height * 50) / 2, 50, 50, copy_shape.color);
            if (moving_loop >= z)
            {
                y++;
                if (is_collison(color_matrix, *shape, x, y))
                {
                    PlaySound(blockSound);
                    y--;
                    save(color_matrix, *shape, x, y, score, level, z, is_game_over);
                    right_to_change_shape = true;
                    y = 0;
                    shape = next_shape;
                    random_shape = distribution(gen);
                    next_shape = shape_list[random_shape];
                    next_shape->rotate(0);
                    next_shape->copy(&copy_shape);
                    shape->rotate(0);
                    rotation_status = 0;
                    uniform_int_distribution<int> distribution2(0, 12 - shape->getWidth());
                    x = distribution2(gen);
                }
                moving_loop = 0;
            }
            shape->draw(x, y);
            draw_board(color_matrix);
            EndDrawing();
            a++;
            moving_loop++;
        }
        else
        {
            UpdateMusicStream(game_over_music);
            BeginDrawing();
            ClearBackground(RAYWHITE);
            DrawTexture(game_over_texture, 0, 0, WHITE);
            EndDrawing();
        }
    }
    UnloadMusicStream(music);
    UnloadMusicStream(game_over_music);
    UnloadSound(blockSound);
    CloseAudioDevice();
    CloseWindow();
    return 0;
}