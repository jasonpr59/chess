function Highlights(){
  this.board = new Array(8);
  for (var x = 1; x <= 8; x++){
    this.board[x-1] = [null, null, null, null, null, null, null, null];
  }
}

Highlights.prototype.setHighlight = function(x, y, color){
  this.board[x-1][y-1] = color;
};

Highlights.prototype.getHighlight = function(x, y){
  return this.board[x-1][y-1];
};
