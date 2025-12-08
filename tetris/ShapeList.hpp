#ifndef SHAPELIST
#define SHAPELIST
#include "Shape.hpp"
#include <string>
#include <iostream>
using namespace std;

class ShapeList {
private:
	Shape	**list;
public:
	int size;
	ShapeList(void);
	Shape *operator[](int index);
	~ShapeList(void);
};

void	fill_color_name_list(string* color_name_list);
void	fill_color_list(Color *color_list);
Color	get_color(string* color_name_list, Color* color_list, string str);
void	add_shape_list(Shape*** list, int& size, string* color_name_list, Color* color_list);

#endif