//Must be run with the ScalaFX library (as you can see)
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.animation.AnimationTimer
import scalafx.scene.paint.Color
import scalafx.scene.input.MouseEvent

val boardSize = 100
var cells: Array[Array[Boolean]] = Array.fill(boardSize)(Array.fill(boardSize)(util.Random.nextBoolean))

//Function for applying rules
def calculateNextStep() = {
	def getNeighborsForCell(cellRow: Int, cellCol: Int): Int = {
		val cellsToCheck = Array(-1, 0, 1)
		var neighbors = 0
		
		for (row <- cellsToCheck; col <- cellsToCheck; if !(row == 0 && col == 0)) {
			val newRow = cellRow + row
			val newCol = cellCol + col
			if ((0 until boardSize contains newRow) && (0 until boardSize contains newCol) && cells(newRow)(newCol)) neighbors += 1
		}
		
		return neighbors
	}
	
	val newCells: Array[Array[Boolean]] = Array.ofDim(boardSize, boardSize)
	
	for (row <- 0 until cells.length; col <- 0 until cells(row).length) {
		val neighbors = getNeighborsForCell(row, col)
		newCells(row)(col) = if (cells(row)(col)) (neighbors == 2 || neighbors == 3) else (neighbors == 3)
	}
	
	cells = newCells
}

val app = new JFXApp {
	stage = new JFXApp.PrimaryStage {
		title = "Conway's Game of Life by Thomas Lauerman"
		scene = new Scene(800, 800) {
			val canvas = new Canvas(width.value, height.value)
			val gc = canvas.graphicsContext2D
			content = canvas
			
			//Drawing cells
			var lastTime: Long = 0
			val timer = AnimationTimer(t => {
				if (lastTime == 0) lastTime = t
				
				if (t - lastTime >= 100000000) {
					val spacing: Int = 2
					val cellWidth: Double = (width.value - (spacing*boardSize)) / boardSize
					val cellHeight: Double = (height.value - (spacing*boardSize)) / boardSize
					
					gc.clearRect(0, 0, width.value, height.value)
					for (row <- 0 until cells.length; col <- 0 until cells(row).length) {
						val x = ((spacing + cellWidth) * row) + (spacing/2)
						val y = ((spacing + cellHeight) * col) + (spacing/2)
						
						gc.fill = if (cells(row)(col)) Color.Blue else Color.LightGrey
						gc.fillRect(x, y, cellWidth, cellHeight)
					}
					
					calculateNextStep()
					lastTime = t
				}
			})
			timer.start
			
			//Reset board on mouse click
			onMouseClicked = (me: MouseEvent) => {
				cells = Array.fill(boardSize)(Array.fill(boardSize)(util.Random.nextBoolean))
			}
		}
	}
}

//Start app
app.main(args)