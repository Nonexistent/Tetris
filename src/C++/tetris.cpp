#include <iostream>
#include <windows.h> 
#include <cstdlib> 
#include <chrono>
#include <ctime>
using namespace std;
/*
* Author: Nonexistent
* Last Update: October 17th, 2014
*
*/

class Cell{
private:
	bool occupied = false;
public:
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
	void setOccupied(bool b){
		occupied = b;
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
bool printEntire = true;
int rowUpper = 0;
int rowLower = 0;
int score = 0;
bool gameIsAlive = true;

//prototypes
void printGrid();
void removeCells();
void setCells();
void update();
void determineKey();
void moveCursor(int column, int line);
int clearRows(int multiplier);
bool createBrick();
bool withinBounds(int x, int y);
bool moveController(int move);
bool down();
bool up();
bool right();
bool left();

int abs(int i){
	return i < 0 ? -i : i;
}

int main(){
	for(int row = 0; row < ROW; row++){
		for(int col = 0; col < COLUMN; col++){
			grid[col][row].setCoordinates(col, row);
		}
	}
	auto start = chrono::high_resolution_clock::now();
	srand(time(0));
	createBrick();
	int i = 55;
	while(gameIsAlive){
		auto end = chrono::high_resolution_clock::now() - start;
		auto millis = chrono::duration_cast<chrono::milliseconds>(end).count();
		if(millis >= 1000){
			update();
			printGrid();
			start = chrono::high_resolution_clock::now();
		}
		Sleep(25);
		determineKey();
		Sleep(i);
	}
	cout << "\n G A M E ---- O V E R ---- M 8";
	cin.get();
	cin.get();
	return 0;
}

void update(){
	if (!moveController(VK_DOWN)) {
		score += clearRows(1);
		gameIsAlive = createBrick();
	}
}

void moveCursor(int x, int y) {
	COORD coord;
	coord.X = x;
	coord.Y = y;
	
	HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

	if (!SetConsoleCursorPosition(hConsole, coord)){
		//error
	}
}

void determineKey(){
	int i = -1;
	if(GetAsyncKeyState(VK_UP)){ i =  VK_UP;}
	else if(GetAsyncKeyState(VK_DOWN)){ i = VK_DOWN;}
	else if(GetAsyncKeyState(VK_RIGHT)){ i = VK_RIGHT;}
	else if(GetAsyncKeyState(VK_LEFT)){ i = VK_LEFT;}
	if(moveController(i)){
		printGrid();
	}
}

bool createBrick(){
	Cell temp[4] = defaultBricks[rand() % 7];
	for(int i = 0; i < 4; i++){
		if(grid[temp[i].x][temp[i].y].isOccupied()){
			return false;
		}
		currentBrick[i].setCoordinates(temp[i].x, temp[i].y);
	}
	return true;
}

void printGrid(){
	//ITERATE BACKWARDS
	if(printEntire){
		system("cls");
		cout << "Score: " << score << endl;
		for(int row = ROW - 1; row >= 0; row--){
			cout << "|";
			for(int col = 0; col < COLUMN; col++){
				if(grid[col][row].isOccupied()){
					cout << "X";
				}else{
					cout << " ";
				}
			}
			cout << "|\n";
		}
		printEntire = false;
	}else{
		//optimize grid printing
		moveCursor(0, abs(rowUpper - 21) + 1);
		for(int row = rowUpper; row >= rowLower; row--){
			cout << "|";
			for(int col = 0; col < COLUMN; col++){
				if(grid[col][row].isOccupied()){
					cout << "X";
				}else{
					cout << " ";
				}
			}
			cout << "|\n";
		}
	}	
}

bool moveController(int key){
	switch(key){
	case VK_UP: return up();
	case VK_DOWN: return down();
	case VK_RIGHT: return right();
	case VK_LEFT: return left();
	}
	return false;
}

void removeCells(){
	rowUpper = 0;
	for(Cell c : currentBrick){
		grid[c.x][c.y].setOccupied(false);
		if(c.y > rowUpper){
			rowUpper = c.y;
		}
	}
}

void setCells(){
	rowLower = 500;
	for(Cell c : currentBrick){
		grid[c.x][c.y].setOccupied(true);
		if(c.y < rowLower){
			rowLower = c.y;
		}
	}
}

bool withinBounds(int x, int y){
	if ((x < 0 || x > 9) || (y < 0 || y > 21)) {
		return false;
	}
	return true;
}

//rotate
bool up(){
	removeCells();
	rowUpper++;
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
	rowLower--;
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
		score += clearRows(multiplier + 1);
end:;
	}
	
	printEntire = true;
	return score;
}