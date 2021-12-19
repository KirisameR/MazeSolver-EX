# MazeSolver EX

## What is it?

A multi-strategy maze solver powered by JavaFX, integrated with A*, Dijkstra, DFS and BFS path-finding algorithm.


## How to run it?

1. Compile: 
	~~~bash
	sh javac.sh ./src/MazeApplication.java
	~~~
2. Run:
	~~~bash
	sh java.sh MazeApplication   
	~~~

## How to use it?

1. Import the maze from any `.txt` file:
	Maze Solver EX takes in `.txt` file with specific context as valid input.<br>

	Click on `Load Map` button, in the pop-up menu select the algorithm you want to apply, then click `Load` button to select the `.txt` file from your device.

	You can inport your own maze. in your `.txt` file, please record maze map as a rectangular character 2-D matrix, where CORRIDOR is represented as `.`, ENTRANCE is `e`, EXIT should be `x`and wall needs to be `#`. 

2. Observe the process of solving the maze:<br>
	Click `Step up`, you will see each steps made by the algorithm. 


3. Save solving result: <br>
	Click `Save Route` to save the binary file.

4. Load from binary file: <br>
	Click `Load Route` to retrieve serialized maze solving session.