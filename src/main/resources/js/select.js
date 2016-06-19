function roundWithTwoDecimals(value) {
    return (Math.round(value * 100)) / 100;
}

function handleClick(event) {
    var coordinates = event.hitPnt;
    document.getElementById('coordX').innerHTML = roundWithTwoDecimals(coordinates[0]);
    document.getElementById('coordY').innerHTML = roundWithTwoDecimals(coordinates[1]);
    document.getElementById('coordZ').innerHTML = roundWithTwoDecimals(coordinates[2]);
    document.getElementById('gridX').innerHTML = roundWithTwoDecimals(coordinates[0]/cellColumnSize);
    document.getElementById('gridY').innerHTML = roundWithTwoDecimals(coordinates[1]/transform[1]);
    document.getElementById('gridZ').innerHTML = roundWithTwoDecimals(coordinates[2]/cellRowSize);
    findNeighbours(event.hitPnt)
}

function findNeighbours(point) {
    var j = point[0] / cellRowSize;
    var i = point[2] / cellColumnSize;

    var ceil_i = Math.ceil(i);
    var ceil_j = Math.ceil(j);
    var floor_i = Math.floor(i);
    var floor_j = Math.floor(j);

    document.getElementById("gridValues").innerHTML = "<p>" +
        "array[" + floor_i + "][" + floor_j + "] = " + (array[floor_i][floor_j]).toFixed(2) + "<br>" +
        "array[" + ceil_i + "][" + floor_j + "] = " + (array[ceil_i][floor_j]).toFixed(2) + "<br>" +
        "array[" + floor_i + "][" + ceil_j + "] = " + (array[floor_i][ceil_j]).toFixed(2) + "<br>" +
        "array[" + ceil_i + "][" + ceil_j + "] = " + (array[ceil_i][ceil_j]).toFixed(2) + "</p><br>";
    
    document.getElementById("situation").style.display = "block";
}

function check(situationType) {
    this.situationType = situationType;
}

function getTransformScale() {
	this.transform = document.getElementById("elevationTransform").scale.split(" ");
}

window.onload = function() {
    this.grid = document.getElementById("grid");
    this.height = grid.height;
    this.columns = parseInt(grid.xDimension);
    this.cellColumnSize = parseInt(grid.xSpacing);
    this.rows = parseInt(grid.zDimension);
    this.cellRowSize = parseInt(grid.zSpacing);
    height = height.split(" ");
    this.result = height.map(Number);
    this.array = [];
    while (result.length > 0) array.push(result.splice(0, columns));
	getTransformScale();

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
			</table>\
			<br>\
			<div id="gridValues"></div>\
			<div id="situation" style="display:none;">\
				Z Value: <input type="text" id="situationValue" size="10" value="5"><br>\
				<input type="radio" name="situationType" onclick="check(this.value)" value="relative" checked>Relative\
				<input type="radio" name="situationType" onclick="check(this.value)" value="absolute">Absolute<br>\
				<br>\
				<input type="submit" value="Show Flooding" onclick="flood()">\
			</div>\
		</div>\
	';
}