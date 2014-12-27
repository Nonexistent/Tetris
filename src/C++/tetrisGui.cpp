#include <iostream>
#include <windows.h> 
#include <cstdlib> 
#include <chrono>
#include <ctime>
using namespace std;

/*
* Author: Nonexistent
* Creation Date: December 24th, 2014
* Last Update: December 26th, 2014
*
*/

class Cell{
private:
	bool occupied = false;
public:
	COLORREF color;
	int x = 0, y = 0;
	Cell(){}
	Cell(int a, int b){
		x = a;
		y = b;
	}
	void setCoordinates(int column, int row){
		x = column;
		y = row;
	}
	bool isOccupied(){
		return occupied;
	}
	Cell& setOccupied(bool b){
		occupied = b;
		return *this;
	}
};

const int ROW = 22, COLUMN = 10;
const int MATRIX[2][2] = {
	{0, 1},
	{-1, 0}
};

const Cell defaultBricks[7][4] = {
	{Cell(4, 21), Cell(5, 21), Cell(6, 21), Cell(7, 21)},	//I
	{Cell(4, 21), Cell(4, 20), Cell(5, 20), Cell(6, 20)},	//J
	{Cell(4, 20), Cell(5, 20), Cell(6, 20), Cell(6, 21)},	//L
	{Cell(5, 21), Cell(6, 21), Cell(5, 20), Cell(6, 20)},	//O
	{Cell(4, 20), Cell(5, 21), Cell(5, 20), Cell(6, 21)},	//Z
	{Cell(4, 21), Cell(5, 21), Cell(5, 20), Cell(6, 20)},	//S
	{Cell(4, 20), Cell(5, 21), Cell(5, 20), Cell(6, 20)}		//T
};
Cell currentBrick[4];
Cell grid[COLUMN][ROW];
int score = 0;
bool gameIsAlive = true;

//gui
const char name[] = "aName";
const int offset = 20;
const int WIDTH = 200, HEIGHT = 440 + offset;
const int BOARD_HEIGHT = HEIGHT - offset;
const int xIncrement = WIDTH / COLUMN;
const int yIncrement = BOARD_HEIGHT / ROW;
bool drawGrid = true;
const string fini("Round Over");
COLORREF colors[] = {
	RGB(89, 195, 226),
	RGB(255, 0, 0),
	RGB(0, 163, 0),
	RGB(117, 25, 117),
	RGB(255, 140, 0),
	RGB(66, 105, 225),
	RGB(230, 230, 0) };

//prototypes
void printGrid();
void removeCells();
void setCells();
void update();
int clearRows(int multiplier);
bool createBrick();
bool withinBounds(int x, int y);
bool down();
bool up();
bool right();
bool left();

int abs(int i){ return i < 0 ? -i : i; }

LRESULT CALLBACK WndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
	PAINTSTRUCT ps;
	switch(msg) {
	case WM_SIZE:
		if(wParam == SIZE_RESTORED){
			drawGrid = true;
		}
		break;
		
	case WM_KEYDOWN:
		{
			switch(wParam){
			case VK_UP:
				if(up()) { break; }
				else{return false; }
				
			case VK_DOWN:
				if(down()) { break; }
				else{return false; }
				
			case VK_RIGHT:
				if(right()) { break; }
				else{return false; }
				
			case VK_LEFT:
				if(left()) { break; }
				else{return false; }
			}
			InvalidateRect(hwnd, 0 ,FALSE);
			return true;
		}
	case WM_PAINT:
		{
			HDC hdc = BeginPaint(hwnd, &ps);
			if(drawGrid){
				auto pen = SelectObject(hdc, CreatePen(PS_SOLID, 1, RGB(211, 211, 211))); 
				
				//vertical
				for(int i = 0; i < WIDTH; i += xIncrement){
					MoveToEx(hdc, i, offset, NULL);
					LineTo(hdc, i, HEIGHT);
				}
				//horizontal
				for(int i = offset; i <= HEIGHT; i += yIncrement){
					MoveToEx(hdc, 0, i, NULL);
					LineTo(hdc, WIDTH, i);
				}
				DeleteObject(pen);
				drawGrid = false;
			}
			
			string s = "Score: " + to_string(score);
			TextOut(hdc, 3, 3, s.c_str(), s.length());
			
			if(!gameIsAlive){
				SetTextColor(hdc, colors[2]);
				TextOut(hdc, 100, 3, fini.c_str(), fini.length());
			}
			
			for(int row = 0; row < ROW; row++){
				for(int col = 0; col < COLUMN; col++){
					Cell temp = grid[col][row];
					
					RECT square{ temp.x * xIncrement + 2,
						(abs(temp.y - 21) * yIncrement + 2 + offset),
						temp.x * xIncrement + 19,
						(abs(temp.y - 21) * yIncrement) + 19 + offset};
					
					if(grid[col][row].isOccupied()){
						auto brush = CreateSolidBrush(temp.color);
						FillRect(hdc, &square, brush);
						DeleteObject(brush);
					}else{
						auto brush = CreateSolidBrush(RGB(255, 255, 255));
						FillRect(hdc, &square, brush);
						DeleteObject(brush);
					}
				}
			}
			EndPaint(hwnd, &ps);
			break;
		}
		
	case WM_CLOSE:
		DestroyWindow(hwnd);
		break;
		
	case WM_ERASEBKGND:
		return true;
		
	case WM_DESTROY:
		PostQuitMessage(0);
		break;

	default:
		return DefWindowProc(hwnd, msg, wParam, lParam);
	}
	return 0;
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
	WNDCLASSEX wc;
	HWND hwnd;
	MSG msg;
	
	memset(&msg, 0, sizeof(msg));
	
	wc.cbSize = sizeof(WNDCLASSEX);
	wc.style = 0;
	wc.lpfnWndProc = WndProc;
	wc.cbClsExtra = 0;
	wc.cbWndExtra = 0;
	wc.hInstance = hInstance;
	wc.hIcon = LoadIcon(NULL, IDI_APPLICATION);
	wc.hCursor = LoadCursor(NULL, IDC_ARROW);
	wc.hbrBackground = (HBRUSH)(COLOR_WINDOW+1);
	wc.lpszMenuName  = NULL;
	wc.lpszClassName = name;
	wc.hIconSm = LoadIcon(NULL, IDI_APPLICATION);

	if(!RegisterClassEx(&wc))
	{
		MessageBox(NULL, "registration", "error",
		MB_ICONEXCLAMATION | MB_OK);
		return 0;
	}

	
	hwnd = CreateWindowEx(
	WS_EX_CLIENTEDGE,
	name,
	"Tetris",
	WS_OVERLAPPEDWINDOW,
	CW_USEDEFAULT, CW_USEDEFAULT, WIDTH+xIncrement, HEIGHT + (2 * yIncrement) + 5,
	NULL, NULL, hInstance, NULL);

	if(hwnd == NULL) {
		MessageBox(NULL, "creation", "error",
		MB_ICONEXCLAMATION | MB_OK);
		return 0;
	}

	ShowWindow(hwnd, nCmdShow);
	UpdateWindow(hwnd);
	//---------------------------------------------------
	
	
	for(int row = 0; row < ROW; row++){
		for(int col = 0; col < COLUMN; col++){
			grid[col][row].setCoordinates(col, row);
		}
	}
	
	auto start = chrono::high_resolution_clock::now();
	srand(time(0));
	createBrick();
	
	
	//------------------------------------------------------------
	
	while (WM_QUIT != msg.message  && gameIsAlive) {
		auto end = chrono::high_resolution_clock::now() - start;
		auto millis = chrono::duration_cast<chrono::milliseconds>(end).count();
		
		if(millis >= 1000){
			update();
			InvalidateRect(hwnd, 0 ,FALSE);
			start = chrono::high_resolution_clock::now();
		}
		Sleep(4);
		if(PeekMessage (&msg, NULL, 0, 0, PM_REMOVE) > 0){
			TranslateMessage(&msg);
			DispatchMessage(&msg);
			if(msg.message == WM_QUIT) return 0;
		}
	}
	while(GetMessage(&msg, NULL, 0, 0) > 0)
	{
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}
	return msg.wParam;
}

void update(){
	if (!down()) {
		score += clearRows(1);
		gameIsAlive = createBrick();
	}
}

bool withinBounds(int x, int y){
	if ((x < 0 || x > 9) || (y < 0 || y > 21)) {
		return false;
	}
	return true;
}

bool createBrick(){
	const Cell* temp = defaultBricks[rand() % 7];
	COLORREF color = colors[rand() % 7];
	for(int i = 0; i < 4; i++){
		if(grid[temp[i].x][temp[i].y].isOccupied()){
			return false;
		}
		currentBrick[i].color = color;
		currentBrick[i].setCoordinates(temp[i].x, temp[i].y);
	}
	for(Cell c : currentBrick){
		grid[c.x][c.y].setOccupied(true).color = c.color;;
	}
	return true;
}

void removeCells(){
	for(Cell c : currentBrick){
		grid[c.x][c.y].setOccupied(false);
	}
}

void setCells(){
	for(Cell c : currentBrick){
		grid[c.x][c.y].setOccupied(true).color = c.color;
	}
}

bool up(){
	removeCells();
	//index 2 will be point of origin
	int offsetX = currentBrick[2].x;
	int offsetY = currentBrick[2].y;
	Cell temp[4];
	for(int i = 0; i < 4; i++){
		temp[i].setCoordinates(currentBrick[i].x - offsetX, currentBrick[i].y - offsetY);
		int tempx = ((temp[i].x * MATRIX[0][0]) + (temp[i].y * MATRIX[1][0])) + offsetX;
		int tempy = ((temp[i].x * MATRIX[0][1]) + (temp[i].y * MATRIX[1][1])) + offsetY;
		if(!withinBounds(tempx, tempy) || grid[tempx][tempy].isOccupied()){
			setCells();
			return false;
		}
		temp[i].x = tempx;
		temp[i].y = tempy;
	}
	for(int i = 0; i < 4; i++){
		currentBrick[i].x = temp[i].x;
		currentBrick[i].y = temp[i].y;
	}
	setCells();
	return true;
}

bool right(){
	removeCells();
	for(Cell c : currentBrick){
		if(c.x == 9 || grid[c.x + 1][c.y].isOccupied()){
			setCells();
			return false;
		}
	}
	for(int i = 0; i < 4; i++){
		currentBrick[i].x++;
	}
	setCells();
	return true;
}

bool left(){
	removeCells();
	for(Cell c : currentBrick){
		if(c.x == 0 || grid[c.x - 1][c.y].isOccupied()){
			setCells();
			return false;
		}
	}
	for(int i = 0; i < 4; i++){
		currentBrick[i].x--;
	}
	setCells();
	return true;
}

bool down(){
	removeCells();
	for(Cell c : currentBrick){
		if(c.y == 0 || grid[c.x][c.y - 1].isOccupied()){
			setCells();
			return false;
		}
	}
	for(int i = 0; i < 4; i++){
		currentBrick[i].y--;
	}
	setCells();
	return true;
}

int clearRows(int multiplier){
	int score = 0;
	
	for (int i = 0; i < ROW; i++) {
		for (int j = 0; j < COLUMN; j++) {
			if (!grid[j][i].isOccupied()) {
				goto end;
			}
		}
		for (int j = 0; j < COLUMN; j++) {
			grid[j][i].setOccupied(false);
			score = multiplier * 40;
		}
		for (int c = 0; c < COLUMN; c++) {
			for (int r = i; r < ROW; r++) {
				if (grid[c][r].isOccupied()) {
					if (!grid[c][r - 1].isOccupied()) {
						grid[c][r].setOccupied(false);
						grid[c][r - 1].setOccupied(true);
					}
				}
			}
		}
		Sleep(7);
		score += clearRows(multiplier + 1);
		end:;
	}
	return score;
}