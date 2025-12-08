#ifndef COLOR
#define COLOR

#include "ShapeList.hpp"

class ColorClass {
public:
	Color color;
	ColorClass(Color& color);
};

class ColorMatrix {
	ColorClass*** colors;
public:
	ColorMatrix(void);
	ColorClass *get(int index1, int index2);
	void		set(int index1, int index2, Color &color, int status);
	~ColorMatrix(void);
};

#endif