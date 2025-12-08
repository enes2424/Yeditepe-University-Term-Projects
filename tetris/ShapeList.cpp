#include "ShapeList.hpp"

ShapeList::ShapeList(void) {
	list = new Shape * [10];
	string* color_name_list = new string[26];
	Color* color_list = new Color[26];
	size = 10;
	fill_color_name_list(color_name_list);
	fill_color_list(color_list);
	bool is_available = false;
	string str;
	while (true)
	{
		cout << "Do you want to give the shapes your own special colors?" << endl;
		cin >> str;
		if (str == "yes" ||  str == "no")
			break;
		cout << "wrong input!!" << endl;
	}
	if (str == "yes") {
		cout << "Choose colors for must-have shapes." << endl;
		list[0] = new Shape(new int* [3] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}, new int[3] {0, 1, 0}}, 3, 3, get_color(color_name_list, color_list, "0 1 0\n1 1 1\n0 1 0"));
		list[1] = new Shape(new int* [2] {new int[2] {1, 1}, new int[2] {1, 1}}, 2, 2, get_color(color_name_list, color_list, "1 1\n1 1"));
		list[2] = new Shape(new int* [2] {new int[2] {1, 0}, new int[2] {1, 1}}, 2, 2, get_color(color_name_list, color_list, "1 0\n1 1"));
		list[3] = new Shape(new int* [3] {new int[1] {1}, new int[1] {1}, new int[1] {1}}, 3, 1, get_color(color_name_list, color_list, "1\n1\n1"));
		list[4] = new Shape(new int* [2] {new int[3] {1, 1, 0}, new int[3] {0, 1, 1}}, 2, 3, get_color(color_name_list, color_list, "1 1 0\n0 1 1"));
		list[5] = new Shape(new int* [2] {new int[3] {1, 0, 1}, new int[3] {1, 1, 1}}, 2, 3, get_color(color_name_list, color_list, "1 0 1\n1 1 1"));
		list[6] = new Shape(new int* [3] {new int[2] {1, 0}, new int[2] {1, 1}, new int[2] {0, 1}}, 3, 2, get_color(color_name_list, color_list, "1 0\n1 1\n0 1"));
		list[7] = new Shape(new int* [3] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}, new int[3] {1, 0, 1}}, 3, 3, get_color(color_name_list, color_list, "0 1 0\n1 1 1\n1 0 1"));
		list[8] = new Shape(new int* [3] {new int[3] {1, 0, 0}, new int[3] {1, 1, 1}, new int[3] {0, 0, 1}}, 3, 3, get_color(color_name_list, color_list, "1 0 0\n1 1 1\n0 0 1"));
		list[9] = new Shape(new int* [2] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}}, 2, 3, get_color(color_name_list, color_list, "0 1 0\n1 1 1"));
	}
	else {
		list[0] = new Shape(new int* [3] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}, new int[3] {0, 1, 0}}, 3, 3, MAROON);
		list[1] = new Shape(new int* [2] {new int[2] {1, 1}, new int[2] {1, 1}}, 2, 2, BLUE);
		list[2] = new Shape(new int* [2] {new int[2] {1, 0}, new int[2] {1, 1}}, 2, 2, GOLD);
		list[3] = new Shape(new int* [3] {new int[1] {1}, new int[1] {1}, new int[1] {1}}, 3, 1, ORANGE);
		list[4] = new Shape(new int* [2] {new int[3] {1, 1, 0}, new int[3] {0, 1, 1}}, 2, 3, PURPLE);
		list[5] = new Shape(new int* [2] {new int[3] {1, 0, 1}, new int[3] {1, 1, 1}}, 2, 3, DARKBLUE);
		list[6] = new Shape(new int* [3] {new int[2] {1, 0}, new int[2] {1, 1}, new int[2] {0, 1}}, 3, 2, PINK);
		list[7] = new Shape(new int* [3] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}, new int[3] {1, 0, 1}}, 3, 3, GREEN);
		list[8] = new Shape(new int* [3] {new int[3] {1, 0, 0}, new int[3] {1, 1, 1}, new int[3] {0, 0, 1}}, 3, 3, BROWN);
		list[9] = new Shape(new int* [2] {new int[3] {0, 1, 0}, new int[3] {1, 1, 1}}, 2, 3, LIME);
	}
	while (true) {
		while (true) {
			cout << "Do you want to add new shape?" << endl;
			cin >> str;
			if (str == "yes" || str == "no")
				break;
			cout << "wrong input!!" << endl;
		}
		if (str == "yes")
			add_shape_list(&list, size, color_name_list, color_list);
		else
			break;
	}
	delete[] color_name_list;
	delete[] color_list;
}

Shape *ShapeList::operator[](int index) {
	return list[index];
}

ShapeList::~ShapeList(void) {
	for (int i = 0; i < 10; i++)
		delete list[i];
	delete[] list;
}

void	add_shape_list(Shape ***list, int &size, string *color_name_list, Color *color_list) {
	string str;
	Shape** new_list = new Shape * [size + 1];
	int** matrix;
	int count2;
	int count;

	for (int i = 0; i < size; i++)
		new_list[i] = (*list)[i];
	while (true) {
		cout << "enter shape (example: 101-010-101) -> ";
		cin >> str;
		count = 0;
		for (int i = 0; i < str.length(); i++)
			count += str[i] == '-';
		matrix = new int* [++count];
		count2 = 0;
		for (int i = 0; i < str.length() && str[i] != '-'; i++)
			count2 += str[i] == '0' || str[i] == '1';
		for (int i = 0; i < count; i++)
			matrix[i] = new int[count2];
		for (int k = 0, i = 0, j = 0; k < str.length(); k++)
		{
			if (str[k] == '0')
				matrix[i][j++] = 0;
			else if (str[k] == '1')
				matrix[i][j++] = 1;
			else if (str[k] == '-')
			{
				i++;
				j = 0;
			}
		}
		//if (control(str))
		//	break;
		if (true)
			break;
		for (int i = 0; i < count; i++)
			delete[] matrix[i];
		delete[] matrix;
		cout << "Wrong input!!" << endl;
	}
	new_list[size++] = new Shape(matrix, count, count2, get_color(color_name_list, color_list, str));
	delete[] *list;
	*list = new_list;
}

Color get_color(string* color_name_list, Color* color_list, string str) {
	bool is_available = false;
	Color color;
	string search;
	while (!is_available) {
		cout << "Only the following colors are available in the defaults list." << endl;
		for (int i = 0; i < 25; i++)
			cout << color_name_list[i] << ", ";
		cout << color_name_list[25] << endl;
		cout << str << " -> ";
		cin >> search;
		for (int i = 0; i < 26; i++)
			if (color_name_list[i] == search) {
				color = color_list[i];
				is_available = true;
				break;
			}
		if (!is_available)
			cout << "Wrong input!!" << endl;
	}
	return color;
}

void fill_color_name_list(string* color_name_list) {
	color_name_list[0] = "LIGHTGRAY";
	color_name_list[1] = "GRAY";
	color_name_list[2] = "DARKGRAY";
	color_name_list[3] = "YELLOW";
	color_name_list[4] = "ORANGE";
	color_name_list[5] = "PINK";
	color_name_list[6] = "RED";
	color_name_list[7] = "MAROON";
	color_name_list[8] = "GREEN";
	color_name_list[9] = "LIME";
	color_name_list[10] = "DARKGREEN";
	color_name_list[11] = "SKYBLUE";
	color_name_list[12] = "BLUE";
	color_name_list[13] = "DARKBLUE";
	color_name_list[14] = "PURPLE";
	color_name_list[15] = "VIOLET";
	color_name_list[16] = "DARKPURPLE";
	color_name_list[17] = "BEIGE";
	color_name_list[18] = "BROWN";
	color_name_list[19] = "DARKBROWN";
	color_name_list[20] = "WHITE";
	color_name_list[21] = "BLACK";
	color_name_list[22] = "BLANK";
	color_name_list[23] = "MAGENTA";
	color_name_list[24] = "RAYWHITE";
	color_name_list[25] = "GOLD";
}

void	fill_color_list(Color* color_list) {
	color_list[0] = LIGHTGRAY;
	color_list[1] = GRAY;
	color_list[2] = DARKGRAY;
	color_list[3] = YELLOW;
	color_list[4] = ORANGE;
	color_list[5] = PINK;
	color_list[6] = RED;
	color_list[7] = MAROON;
	color_list[8] = GREEN;
	color_list[9] = LIME;
	color_list[10] = DARKGREEN;
	color_list[11] = SKYBLUE;
	color_list[12] = BLUE;
	color_list[13] = DARKBLUE;
	color_list[14] = PURPLE;
	color_list[15] = VIOLET;
	color_list[16] = DARKPURPLE;
	color_list[17] = BEIGE;
	color_list[18] = BROWN;
	color_list[19] = DARKBROWN;
	color_list[20] = WHITE;
	color_list[21] = BLACK;
	color_list[22] = BLANK;
	color_list[23] = MAGENTA;
	color_list[24] = RAYWHITE;
	color_list[25] = GOLD;
}