function GameUI(game, canvas, history_table, to_move){
  this.game = game;
  this.canvas = canvas;
  this.ctx = canvas.getContext('2d');
  this.width = canvas.width;
  this.history_table = history_table;
  this.to_move = to_move;

  //Info pertaining to in-progress click-then-click move.
  this.currentlyMoving = false;
  this.currentx = null;
  this.currenty = null;

}



GameUI.prototype.click = function(x, y, width){
  var row = Math.floor(8 * x/width) + 1;
  var col = 8 - Math.floor(8 *y/width);
  if (this.currentlyMoving){
    //This click indicates the end of a move.
    try {
      this.game.move(this.currentx, this.currenty, row, col, function(x_o, y_o, x_d, y_d, isWhite){return prompt("Promote to 'queen', 'rook', 'bishop', or 'knight'?");});
    } finally {
      this.currentlyMoving = false;
      this.currentx = null;
      this.currenty = null;
      this.draw(new Highlights());
    }
  } else {
    //This click indicates the start of a move.
    var piece = this.game.piece_at(row, col);
    if (piece != null && piece.isWhite == this.game.whitesMove){
      this.currentlyMoving = true;
      this.currentx = row;
      this.currenty = col;
      var hl = new Highlights();

      for (var attack_row = 1; attack_row <= 8; attack_row++){
        for (var attack_col = 1; attack_col <= 8; attack_col++){
          if (this.game.can_move(row, col, attack_row, attack_col, false)){
            hl.setHighlight(attack_row, attack_col, "red");
          }
        }
      }
      hl.setHighlight(row, col, "yellow");
      this.draw(hl);
    }
  }
};

GameUI.prototype.draw = function(highlights){
  this.drawBoard(highlights);
  this.drawPieces(this.game);
  this.update_history();
  this.update_to_move();
};


GameUI.prototype.drawBoard = function(highlights){
  this.ctx.save();
  var squareLength = this.canvas.width/8;
  var highlightColor;
  for (var x = 1; x <= 8; x++){
    for (var y = 1; y <= 8; y++){
      if ((x + y) % 2 == 0){
        //Black square
        this.ctx.fillStyle = "#666666";
      } else {
        //White Square
        this.ctx.fillStyle = "white";
      }
      this.ctx.fillRect((x - 1) * squareLength, (8 - y) * squareLength, squareLength, squareLength);
      highlightColor = highlights.getHighlight(x,y);
      if (highlightColor != null){
        this.ctx.fillStyle = highlightColor;
        this.ctx.save();
        this.ctx.globalAlpha = 0.3;
        this.ctx.fillRect((x - 1) * squareLength, (8 - y) * squareLength, squareLength, squareLength);
        this.ctx.restore();
      }
    }
  }
  this.ctx.restore();
};

GameUI.prototype.drawPieces = function(game){
  this.ctx.save();
  this.ctx.scale(0.125,0.125);
  var piece;
  for (var x = 1; x <= 8; x++){
    for (var y = 1; y <= 8; y++){
      this.ctx.save();
      this.ctx.translate(this.width*(x-1), this.width*(8-y));
      piece = game.piece_at(x,y);
      if (piece != null){
        this.drawPiece(piece.type, piece.isWhite);
      }
      this.ctx.restore();
    }
  }
  this.ctx.restore();
};

GameUI.prototype.drawPiece = function(piece, isWhite){
  var width = this.canvas.width;
  this.ctx.save();
  this.ctx.lineWidth = 20;
  if (isWhite){
    this.ctx.strokeStyle= "#999999";
    this.ctx.fillStyle = "#cccccc";
  } else {
    this.ctx.strokeStyle = "#333333";
    this.ctx.fillStyle = "#1a1a1a";
  }

  if (piece == "pawn"){
    this.ctx.beginPath();
    this.ctx.moveTo(0.30*width, 0.85*width);
    this.ctx.lineTo(0.45*width, 0.50*width);
    this.ctx.arc(0.50*width, 0.45*width, 0.10*width, 3*Math.PI/4, Math.PI/4, false);
    this.ctx.lineTo(0.55*width, 0.50*width);
    this.ctx.lineTo(0.70*width, 0.85*width);
    this.ctx.closePath();
    this.ctx.fill();
    this.ctx.stroke();
  } else if (piece == "rook") {
    this.ctx.beginPath();

    this.ctx.moveTo(0.25*width, 0.25*width);
    this.ctx.lineTo(0.35*width, 0.25*width);
    this.ctx.lineTo(0.35*width, 0.35*width);
    this.ctx.lineTo(0.45*width, 0.35*width);
    this.ctx.lineTo(0.45*width, 0.25*width);
    this.ctx.lineTo(0.55*width, 0.25*width);
    this.ctx.lineTo(0.55*width, 0.35*width);
    this.ctx.lineTo(0.65*width, 0.35*width);
    this.ctx.lineTo(0.65*width, 0.25*width);
    this.ctx.lineTo(0.75*width, 0.25*width);
    this.ctx.lineTo(0.75*width, 0.45*width);
    this.ctx.lineTo(0.70*width, 0.45*width);
    this.ctx.lineTo(0.70*width, 0.75*width);
    this.ctx.lineTo(0.75*width, 0.75*width);
    this.ctx.lineTo(0.75*width, 0.85*width);
    this.ctx.lineTo(0.25*width, 0.85*width);
    this.ctx.lineTo(0.25*width, 0.75*width);
    this.ctx.lineTo(0.30*width, 0.75*width);
    this.ctx.lineTo(0.30*width, 0.45*width);
    this.ctx.lineTo(0.25*width, 0.45*width);

    this.ctx.closePath();
    this.ctx.fill();
    this.ctx.stroke();
  } else if (piece == "knight") {
    this.ctx.beginPath();
    //this.ctx.moveTo(0.20*width, 0.85*width);
    //this.ctx.lineTo(0.20*width, 0.75*width);
    //this.ctx.lineTo(0.25*width, 0.70*width);
    this.ctx.moveTo(0.25*width, 0.85*width);
    this.ctx.lineTo(0.25*width, 0.30*width);
    this.ctx.lineTo(0.20*width, 0.25*width);
    this.ctx.lineTo(0.20*width, 0.15*width);
    this.ctx.lineTo(0.30*width, 0.15*width);
    this.ctx.lineTo(0.40*width, 0.25*width);
    this.ctx.lineTo(0.40*width, 0.15*width);
    this.ctx.lineTo(0.65*width, 0.15*width);
    this.ctx.lineTo(0.80*width, 0.30*width);
    this.ctx.lineTo(0.80*width, 0.55*width);
    this.ctx.lineTo(0.65*width, 0.55*width);
    this.ctx.lineTo(0.50*width, 0.40*width);
    this.ctx.lineTo(0.45*width, 0.40*width);
    this.ctx.lineTo(0.45*width, 0.45*width);
    this.ctx.lineTo(0.75*width, 0.75*width);
    this.ctx.lineTo(0.75*width, 0.85*width);
    this.ctx.closePath();
    this.ctx.fill();
    this.ctx.stroke();
  } else if (piece == "bishop") {
    this.ctx.beginPath();
    this.ctx.moveTo(0.25*width, 0.85*width);
    this.ctx.arc(0.65*width, 0.30*width, 0.28*width, Math.PI * 0.75, Math.PI + Math.acos(15/28), false);
    this.ctx.arc(0.35*width, 0.30*width, 0.28*width, 2*Math.PI-Math.acos(15/28), 1.85*Math.PI, false);
    this.ctx.lineTo(0.50*width, 0.30*width);
    this.ctx.arc(0.35*width, 0.30*width, 0.28*width, 1.90*Math.PI, Math.PI * 0.25, false);
    this.ctx.lineTo(0.75*width, 0.85*width);
    this.ctx.closePath();
    this.ctx.fill();
    this.ctx.stroke();
  } else if (piece == "queen") {
    this.ctx.beginPath();
    this.ctx.moveTo(0.25*width, 0.85*width);
    this.ctx.lineTo(0.10*width, 0.25*width);
    this.ctx.lineTo(0.23*width, 0.45*width);
    this.ctx.lineTo(0.30*width, 0.25*width);
    this.ctx.lineTo(0.40*width, 0.45*width);
    this.ctx.lineTo(0.50*width, 0.25*width);
    this.ctx.lineTo(0.60*width, 0.45*width);
    this.ctx.lineTo(0.70*width, 0.25*width);
    this.ctx.lineTo(0.77*width, 0.45*width);
    this.ctx.lineTo(0.90*width, 0.25*width);
    this.ctx.lineTo(0.75*width, 0.85*width);
    this.ctx.closePath();
    this.ctx.fill();
    this.ctx.stroke();
  } else if (piece == "king") {
    this.ctx.beginPath();
    this.ctx.moveTo(0.15*width, 0.85*width);
    this.ctx.arc(0.50*width, 0.85*width, 0.35*width, Math.PI, Math.PI + Math.acos(1/7), false);
    this.ctx.lineTo(0.45*width, 0.45*width);
    this.ctx.lineTo(0.30*width, 0.45*width);
    this.ctx.lineTo(0.30*width, 0.35*width);
    this.ctx.lineTo(0.45*width, 0.35*width);
    this.ctx.lineTo(0.45*width, 0.20*width);
    this.ctx.lineTo(0.55*width, 0.20*width);
    this.ctx.lineTo(0.55*width, 0.35*width);
    this.ctx.lineTo(0.70*width, 0.35*width);
    this.ctx.lineTo(0.70*width, 0.45*width);
    this.ctx.lineTo(0.55*width, 0.45*width);
    this.ctx.arc(0.50*width, 0.85*width, 0.35*width, 2 * Math.PI - Math.acos(1/7), 2 * Math.PI, false);

    this.ctx.lineTo(0.15*width, 0.85*width);
    this.ctx.fill();
    this.ctx.stroke();
  } else {
  }

  this.ctx.restore();
};

GameUI.prototype.update_history = function(){
  var output = "";
  var i = 0;
  while (2 * i < this.game.history.length()){
    //output += "<tr><td>" + (i+1) + ".</td><td>" + this.game.history.get_move(2*i) + "</td><td>" + (2 * i + 1 < this.game.history.length() ? this.game.history.get_move(2*i+1) : "") + "</td></tr>\n";
    output += "<tr><td>" + (i+1) + ".</td><td>" + this.game.history.get_alg_move(2*i) + "</td><td>" + (2 * i + 1 < this.game.history.length() ? this.game.history.get_alg_move(2 * i + 1) : "") + "</td></tr>\n";
    i++;
  }
  this.history_table.html(output);
};

GameUI.prototype.update_to_move = function(){
  var move_text;
  if (this.game.whitesMove){
    move_text = "White To Move";
  } else {
    move_text = "Black To Move";
  }
  this.to_move.html(move_text);
};
