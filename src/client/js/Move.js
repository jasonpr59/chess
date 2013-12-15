function Move(x_o, y_o, x_d, y_d, moved_piece, captured_piece, promoted){
  this.x_o = x_o;
  this.y_o = y_o;
  this.x_d = x_d;
  this.y_d = y_d;
  this.moved_piece = moved_piece;
  this.captured_piece = captured_piece;
  this.promoted = promoted;
}

Move.prototype.toString = function(){
  return "(" + this.x_o + ", " + this.y_o + ") to (" + this.x_d + ", " + this.y_d +")";
};
