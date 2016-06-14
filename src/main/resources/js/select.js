function roundWithTwoDecimals(value)
{
	return (Math.round(value * 100)) / 100;
}

function handleGroupClick(event)
{
	var coordinates = event.hitPnt;
	$('#coordX').html(roundWithTwoDecimals(coordinates[0]));
	$('#coordY').html(roundWithTwoDecimals(coordinates[1]));
	$('#coordZ').html(roundWithTwoDecimals(coordinates[2]));
	$('#gridX').html(roundWithTwoDecimals(coordinates[0]/cellRowSize));
	$('#gridY').html(roundWithTwoDecimals(coordinates[1])/lExageration);
	$('#gridZ').html(roundWithTwoDecimals(coordinates[2])/cellColumnSize);
	findNeighbours(event.hitPnt)
}

function findNeighbours(point)
{
	var j = point[0]/cellRowSize;
	var value = point[1]/lExageration;
	var i = point[2]/cellColumnSize;
	
	var ceil_i = Math.ceil(i);
	var ceil_j = Math.ceil(j);
	var floor_i = Math.floor(i);
	var floor_j = Math.floor(j);
	
	$('#gridValue').html(
	"array["+floor_i+"]["+floor_j+"] = "+(array[floor_i][floor_j]/lExageration).toFixed(2)+ "\n" +
	"array["+ceil_i+"]["+floor_j+"] = "+(array[ceil_i][floor_j]/lExageration).toFixed(2) + "\n" +
	"array["+floor_i+"]["+ceil_j+"] = "+(array[floor_i][ceil_j]/lExageration).toFixed(2) + "\n" +
	"array["+ceil_i+"]["+ceil_j+"] = "+(array[ceil_i][ceil_j]/lExageration).toFixed(2));
	
}

window.onload = function(event){
	this.grid = document.getElementById("grid");
	lExageration = 7.0;
	this.height = grid.height;
	this.columns = parseInt(grid.xDimension);
	this.cellColumnSize = parseInt(grid.xSpacing);
	this.rows = parseInt(grid.zDimension);
	this.cellRowSize = parseInt(grid.zSpacing);
	height = height.split(" ");
	this.result = height.map(Number);
	this.array = [];
	while(result.length > 0) array.push(result.splice(0,columns));
	document.getElementById("insert").innerHTML = '\
		<div style="position:absolute;left:1000px;top:100px;width:200px">\
			<h3>Click coordinates:</h3>\
			<table style="font-size:1em;">\
				<tr><td>X: </td><td id="coordX">-</td></tr>\
				<tr><td>Y: </td><td id="coordY">-</td></tr>\
				<tr><td>Z: </td><td id="coordZ">-</td></tr>\
				<tr><td>i: </td><td id="gridZ">-</td></tr>\
				<tr><td>j: </td><td id="gridX">-</td></tr>\
				<tr><td>value: </td><td id="gridY">-</td></tr>\
				<tr><td></td><td id="gridValue"></td></tr>\
			</table>\
		</div>\
	';
        };
