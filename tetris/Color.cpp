#include "Color.hpp"

ColorClass::ColorClass(Color& color) {
	this->color = color;
}

ColorMatrix::ColorMatrix(void) {
	colors = new ColorClass * *[16];
	for (int i = 0; i < 16; i++)
	{
		colors[i] = new ColorClass * [12];
		for (int j = 0; j < 12; j++)
			colors[i][j] = NULL;
	}
}

ColorClass* ColorMatrix::get(int index1, int index2) {
	return colors[index1][index2];
}

void		ColorMatrix::set(int index1, int index2, Color& color, int status) {
	if (status)
		colors[index1][index2] = new ColorClass(color);
	else
		colors[index1][index2] = NULL;
}

ColorMatrix::~ColorMatrix(void) {
	for (int i = 0; i < 16; i++)
	{
		for (int j = 0; j < 12; j++)
			if (colors[i][j])
				delete colors[i][j];
		delete[] colors[i];
	}
	delete[] colors;
}