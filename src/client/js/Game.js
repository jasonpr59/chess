function Game(){
  this.setup_new_game();
}


//Sets up pieces for a new game (and deletes any other pieces).
//Resets king movement information, and sets it to white's move.
//DOES NOT alter mouse-click info.
Game.prototype.setup_new_game = function(){
  this.board = new Array(8);
  this.whitesMove = true;
  this.history = new History();

  //King/castle information.
  //Useful in determining
  // 1) Whether the king is in check
  // 2) Whether a move would put the own king in check (thus making it illegal)
  // 3) Whether it is legal to castle.
  this.white_king_x = 5;
  this.white_king_y = 1;
  this.white_king_has_moved = false;
  this.white_rook_1_has_moved = false;
  this.white_rook_8_has_moved = false;


  this.black_king_x = 5;
  this.black_king_y = 8;
  this.black_king_has_moved = false;
  this.black_rook_1_has_moved = false;
  this.black_rook_8_has_moved = false;


  //Put pieces in correct locations.
  var type;
  for (var x = 1; x <= 8; x++){

    if (x == 1 || x == 8){
      type = "rook";
    } else if (x == 2 || x == 7) {
      type = "knight";
    } else if (x == 3 || x == 6) {
      type = "bishop";
    } else if (x == 4){
      type = "queen";
    } else if (x == 5){
      type = "king";
    }
    this.board[x-1] = [new Piece(type, true), new Piece("pawn", true), null, null, null, null, new Piece("pawn", false), new Piece(type,false)];
  }

  this.algebraic_converter = new AlgebraicConverter(this);
};

//Deeply copies all information pertaining to the state of the game.
Game.prototype.copy = function(){
  var board_copy = new Game();
  var piece;
  for (var i = 0; i < 8; i++){
    for (var j = 0; j < 8; j++){
      if (this.board[i][j] == null){
        board_copy.board[i][j] = null;
      } else {
        piece = new Piece(this.board[i][j].type, this.board[i][j].isWhite);
        board_copy.board[i][j] = piece;
      }
    }
  }

  board_copy.whitesMove = this.whitesMove;

  board_copy.white_king_has_moved = this.white_king_has_moved;
  board_copy.white_king_x = this.white_king_x;
  board_copy.white_king_y = this.white_king_y;
  board_copy.white_rook_1_has_moved = this.white_rook_1_has_moved;
  board_copy.white_rook_8_has_moved = this.white_rook_8_has_moved;

  board_copy.black_king_has_moved = this.black_king_has_moved;
  board_copy.black_king_x = this.black_king_x;
  board_copy.black_king_y = this.black_king_y;
  board_copy.black_rook_1_has_moved = this.black_rook_1_has_moved;
  board_copy.black_rook_8_has_moved = this.black_rook_8_has_moved;

  return board_copy;
};

//Puts a piece of the specified type and color at (x,y). If there was already a piece there,
//then that piece is replaced by the new one.
Game.prototype.put_piece = function(x, y, type, isWhite){
  this.board[x-1][y-1] = new Piece(type, isWhite);
};

//Removes the piece at (x, y) if it exists, or does nothing if that square is empty.
Game.prototype.remove_piece = function(x, y){
  this.board[x-1][y-1] = null;
};

Game.prototype.end_turn = function(){
  this.whitesMove = !this.whitesMove;
};

//Returns the piece in the x'th column and the y'th row, or null if there is no piece there.
//Note, this is 1-indexed, not zero indexed.
//e.g.: With the initial board setup, piece_at(4, 1) returns a the white queen,
//since the white queen begins at position d1.
Game.prototype.piece_at = function(x, y){
  return this.board[x-1][y-1];
};

//Requires: type is "queen" "rook" or "bishop" i.e. any of the long-range pieces.
//Returns true if a piece of type `type` could move from the origin square to the destination square,
//assuming there are not pieces in the way.
Game.prototype.in_range = function(x_o, y_o, x_d, y_d, type){
  switch (type){
  case "queen":
    return this.in_queen_range(x_o, y_o, x_d, y_d);
    break;
  case "rook":
    return this.in_rook_range(x_o, y_o, x_d, y_d);
    break;
  case "bishop":
    return this.in_bishop_range(x_o, y_o, x_d, y_d);
    break;
  default:
    throw "TypeError: in_range only takes types `queen`, `rook`, and `bishop`, not " + type;
  }
};

Game.prototype.in_rook_range = function(x_o, y_o, x_d, y_d){
  if (x_o == x_d && y_o == y_d){
    //Since it's already there, it cannot *attack* there...
    //In other terms, if a different piece moved there, *that* piece
    //would take *this* one, so *this* one couldn't take *that* one!
    return false;
  }
  if (x_o != x_d && y_o != y_d){
    return false;
  }
  return true;
};

Game.prototype.in_bishop_range = function(x_o, y_o, x_d, y_d){
  if (x_o == x_d && y_o == y_d){
    //Since it's already there, it cannot *attack* there...
    //In other terms, if a different piece moved there, *that* piece
    //would take *this* one, so *this* one couldn't take *that* one!
    return false;
  }
  if (Math.abs(x_d-x_o) != Math.abs(y_d-y_o)){
    return false;
  }
  return true;
};

Game.prototype.in_queen_range = function(x_o, y_o, x_d, y_d){
  return (this.in_rook_range(x_o, y_o, x_d, y_d) || this.in_bishop_range(x_o, y_o, x_d, y_d));
};


//Requires: (x_o, y_o) -> (x_d, y_d) has a straight row, column, or diagonal path.
Game.prototype.path_clear = function(x_o, y_o, x_d, y_d,isWhite){
  var x_step;
  var y_step;
  if (x_d > x_o){
    x_step = 1;
  } else if (x_d == x_o){
    x_step = 0;
  } else if (x_d < x_o){
    x_step = -1;
  }

  if (y_d > y_o){
    y_step = 1;
  } else if (y_d == y_o){
    y_step = 0;
  } else if (y_d < y_o) {
    y_step = -1;
  }

  var x_examined = x_o + x_step;
  var y_examined = y_o + y_step;
  //Check squares between start and end.
  while (x_examined != x_d || y_examined != y_d){
    if (this.piece_at(x_examined, y_examined) != null){
      return false;
    }
    x_examined += x_step;
    y_examined += y_step;
  }

  //Can only move to final square if its empty, or if an enemy piece is there.
  if (this.piece_at(x_examined, y_examined) == null || this.piece_at(x_examined, y_examined).isWhite != isWhite){
    return true;
  } else {
    return false;
  }
};

//Function used for moving a piece according to the rules of the game.
//Performs checks to ensure that the move is legal
//Params: (x_o, y_o) are the coordinates of the piece to be moved.
//Params: (x_d, y_d) are the destination coordinates.
Game.prototype.can_move = function(x_o, y_o, x_d, y_d, ignore_check){
  if (x_o < 1 || x_o > 8 || y_o < 1 || y_o > 8){
    //Invalid origin coordinates: (" + x_o + ", " + y_o + ")"
    return false;
  }
  if (x_d < 1 || x_d > 8 || y_d < 1 || y_d > 8){
    //Invalid destination coordinates: (" + x_d + ", " + y_d + ")";
    return false;
  }

  var targetPiece = this.piece_at(x_o, y_o);
  if(targetPiece == null){
    return false;
  }

  switch (targetPiece.type){
  case "pawn":
    if (!this._can_move_pawn_ignoring_check(x_o, y_o, x_d, y_d)){
      return false;
    }
    break;

  case "rook":
    if (!this._can_move_rook_ignoring_check(x_o, y_o, x_d, y_d)){
      return false;
    }
    break;

  case "bishop":
    if (!this._can_move_bishop_ignoring_check(x_o, y_o, x_d, y_d)){
      return false;
    }
    break;

  case "queen":
    if (!this._can_move_queen_ignoring_check(x_o, y_o, x_d, y_d)){
      return false;
    }
    break;

  case "king":
    if (!this._can_move_king_ignoring_check(x_o, y_o, x_d, y_d, true)){
      return false;
    }
    break;

  case "knight":
    if (!this._can_move_knight_ignoring_check(x_o, y_o, x_d, y_d)){
      return false;
    }
    break;

  default:
    throw "Piece type " + targetPiece.type + " is invalid.";
  }
  return ignore_check || !this.would_check_own_king(x_o, y_o, x_d, y_d);
};

//Requires that, as long as the move doesn't put the moving piece's king in check, the move would be valid.
Game.prototype.would_check_own_king = function(x_o, y_o, x_d, y_d){
  var new_board = this.copy();
  var piece = new_board.piece_at(x_o, y_o);
  if (piece.type == "king"){
    new_board._transport_king(x_o, y_o, x_d, y_d);
  } else {
    new_board._transport(x_o, y_o, x_d, y_d);
  }

  var isWhite = piece.isWhite;
  var king_x;
  var king_y;
  if (isWhite){
    king_x = new_board.white_king_x;
    king_y = new_board.white_king_y;
  } else {
    king_x = new_board.black_king_x;
    king_y = new_board.black_king_y;
  }
  return new_board.is_under_attack(king_x, king_y, !isWhite, true);
};

Game.prototype.can_move_now = function(x_o, y_o, x_d, y_d){
  return (this.can_move(x_o, y_o, x_d, y_d, false) && this.piece_at(x_o, y_o).isWhite == this.whitesMove);
};

//Params (x_o, y_o) the location of the piece to be moved.
//Params (x_d, y_d) the location to which the piece should be moved.
//promotionCallback A function of parameters (x_o, y_o, x_d, y_d, isWhite) to be called when a pawn of color isWhite is promoted
//when moved from (x_o, y_o) to (x_d, y_d), which returns the piece type to which to promote.
Game.prototype.move = function(x_o, y_o, x_d, y_d, promotionCallback, castlesCallback){
  if (this.can_move_now(x_o, y_o, x_d, y_d)){
    var targetPiece = this.piece_at(x_o, y_o);
    var capturedPiece = this.piece_at(x_d, y_d);
    var promoted = false;
    if (targetPiece.type == null) {
      //Should never happen, given that can_move returned true...
      throw "There's no piece at " + x_o + ", " + y_o;
    } else if (targetPiece.type == "king") {
      this._transport_king(x_o, y_o, x_d, y_d, castlesCallback);
    } else {
      this._transport(x_o, y_o, x_d, y_d);

      //Handle pawn promotion
      if ((y_d == 1 && !targetPiece.isWhite || y_d == 8 && targetPiece.isWhite) && targetPiece.type == "pawn"){
        var promotionType = promotionCallback(x_o, y_o, x_d, y_d, targetPiece.isWhite);
        while (promotionType != "queen" && promotionType != "rook" && promotionType != "bishop" && promotionType != "knight"){
          promotionType = promotionCallback(x_o, y_o, x_d, y_d, targetPiece.isWhite);
        }
        this.piece_at(x_d, y_d).type = promotionType;
        promoted = true;
      }
    }





    //If we got here, the move was successful.
    this.update_has_moved(x_o, y_o, x_d, y_d);
    var move = new Move(x_o, y_o, x_d, y_d, targetPiece, capturedPiece, promoted);
    this.history.add_move(move, this.algebraic_converter.moveToAlg(move));
    this.end_turn();
  } else {
    throw "It is illegal to move the piece at (" + x_o + ", " + y_o + ") to (" + x_d + ", " + y_d + ").";
  }
};

//Updates "has_moved" variables (so that we can check whether castling is legal).
//Note: we don't really need x_d or y_d.  But, soon I plan to replace all occurrences of
//(x_o, y_o, x_d, y_d) with a Move object, so I'm writing all 4 parameters to ease the upcoming refactoring.
Game.prototype.update_has_moved = function(x_o, y_o, x_d, y_d){
  if (x_o == 1){
    if (y_o == 1){
      this.white_rook_1_has_moved = true;
    } else if (y_o == 8){
      this.black_rook_1_has_moved = true;
    } else {
      return;
    }
  } else if (x_o == 5) {
    if (y_o == 1){
      this.white_king_has_moved = true;
    } else if (y_o == 8){
      this.black_king_has_moved = true;
    } else {
      return;
    }
  } else if (x_o == 8) {
    if (y_o == 1){
      this.white_rook_8_has_moved = true;
    } else if (y_o == 8){
      this.black_rook_8_has_moved = true;
    } else {
      return;
    }
  }
};

//Requires the piece at (x_o, y_o) is a pawn, and that piece is the color of the army whose turn it is.
Game.prototype._can_move_pawn_ignoring_check = function(x_o, y_o, x_d, y_d){
  return this._can_push_pawn_ignoring_check(x_o, y_o, x_d, y_d) || this._can_capture_with_pawn_ignoring_check(x_o, y_o, x_d, y_d, false);
};

//Requires the piece at (x_o, y_o) is a pawn, and that piece is the color of the army whose turn it is.
Game.prototype._can_push_pawn_ignoring_check = function(x_o, y_o, x_d, y_d){
  var targetPiece = this.piece_at(x_o, y_o);
  var forwards = this.whitesMove ? 1 : -1;

  //DANGER!  dy is define differently here than in other movement methods!
  //Here, dy > 0 iff the pawn is moving FORWARD.  This means that if
  //the pawn is black, a positive dy means the pawn is moving TOWARDS y=1!
  var dx = x_d - x_o;
  var dy = (y_d - y_o) * forwards;

  if (dx == 0) {
    //Must be a forward move
    if (dy == 1) {
      //Can only move forward to empty square.
      return this.piece_at(x_d, y_d) == null;
    } else if (dy == 2) {
      if (!(y_o == 2 && targetPiece.isWhite) && !(y_o == 7 && !targetPiece.isWhite)){
        //pawns can only move forward two squares if they're at their home row
        return false;
      }
      //The (y_o + y_d)/2 is the row between the origin and the destination.
      //Written strangely to avoid re-calculating directions based on isWhite.
      if (this.piece_at(x_d, (y_o + y_d)/2) != null){
        //pawn cannot move through occupied square
        return false;
      } else {
        //pawn can only move forward to unoccupied square
        return this.piece_at(x_d, y_d) == null;
      }

    } else {
      //pawn cannot move with dy = `dy`
      return false;
    }
  } else {
    //pawn cannot move with dx = `dx`
    return false;
  }
};

//Requires the piece at (x_o, y_o) is a pawn, and that piece is the color of the army whose turn it is.
//Param ignore_victim: if true, does not check whether there is a valid victim to capture.
//                                         This is useful for determining whether the pawn *could* attack if a piece *were* there.
Game.prototype._can_capture_with_pawn_ignoring_check = function(x_o, y_o, x_d, y_d, ignore_victim){
  var targetPiece = this.piece_at(x_o, y_o);
  var forwards = targetPiece.isWhite ? 1 : -1;

  //DANGER!  dy is define differently here than in other movement methods!
  //Here, dy > 0 iff the pawn is moving FORWARD.  This means that if
  //the pawn is black, a positive dy means the pawn is moving TOWARDS y=1!
  var dx = x_d - x_o;
  var dy = (y_d - y_o) * forwards;

  if (dx == 1 || dx == -1){
    //Must be a capture
    if (dy == 1){
      if (ignore_victim){
        return true;
      } else {
        var capturedPiece = this.piece_at(x_d, y_d);
        if (capturedPiece == null){
          //Cannot capture a piece that isn't there!
          return false;
        } else {
          //Can only capture other army's pieces!
          return capturedPiece.isWhite != targetPiece.isWhite;
        }
      }
    } else {
      //pawn must advance one row when capturing, not `dy` rows";
      return false;
    }
    //TODO: Handle en passant.
  } else {
    //Not a capture
    return false;
  }
};


//Requires that the piece at (x_o, y_o) is a knight whose color is that of the army whose turn it is.
Game.prototype._can_move_knight_ignoring_check = function(x_o, y_o, x_d, y_d){
  var targetPiece = this.piece_at(x_o, y_o);
  var dx = x_d - x_o;
  var dy = y_d - y_o;
  if ((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2)){
    return (this.piece_at(x_d, y_d) == null || this.piece_at(x_d, y_d).isWhite != targetPiece.isWhite);
  } else {
    //knight must move 2 squares along  rank or file and one square in orthogonal direction
    return false;
  }
};

//Requires that the piece at (x_o, y_o) is a bishop, whose color is that of the army whose turn it is.
Game.prototype._can_move_bishop_ignoring_check = function(x_o, y_o, x_d, y_d){
  var targetPiece = this.piece_at(x_o, y_o);
  if (!this.in_bishop_range(x_o, y_o, x_d, y_d)){
    //bishop must move a nonzero number of squares along a diagonal.
    return false;
  }

  return (this.path_clear(x_o, y_o, x_d, y_d, targetPiece.isWhite));
};

//Requires that the piece at (x_o, y_o) is a rook, whose color is that of the army whose turn it is.
Game.prototype._can_move_rook_ignoring_check = function(x_o, y_o, x_d, y_d){
  var targetPiece = this.piece_at(x_o, y_o);
  if (!this.in_rook_range(x_o, y_o, x_d, y_d)){
    //rook must move a nonzero number of squares along EITHER rank XOR file.
    return false;
  }

  return (this.path_clear(x_o, y_o, x_d, y_d, targetPiece.isWhite));
};

//Requires that the piece at (x_o, y_o) is a queen, whose color is that of the army whose turn it is.
Game.prototype._can_move_queen_ignoring_check = function(x_o, y_o, x_d, y_d){
  var targetPiece =  this.piece_at(x_o, y_o);
  if (!this.in_queen_range(x_o, y_o, x_d, y_d)){
    //invalid row/col/diag
    return false;
  }

  return (this.path_clear(x_o, y_o, x_d, y_d, targetPiece.isWhite));
};


//Requires that the piece at (x_o, y_o) is a king, whose color is that of the army whose turn it is.
//DANGER! TODO: This is a bit of a misnomer: ignores check for single moves, but considers check for castling.

Game.prototype._can_move_king_ignoring_check = function(x_o, y_o, x_d, y_d, consider_castling){
  var targetPiece = this.piece_at(x_o, y_o);
  var dx = x_d - x_o;
  var dy = y_d - y_o;
  if (dx == 0 && dy == 0){
    //King must move a nonzero number of squares
    return false;
  }


  //Castling moves are always different from non-castling moves.
  if (consider_castling && this.can_castle_king(x_o, y_o, x_d, y_d)){
    return true;
  }


  if (Math.abs(dx) > 1 || Math.abs(dy) > 1){
    //King can only move one square
    return false;
  }

  return this.path_clear(x_o, y_o, x_d, y_d, targetPiece.isWhite);
};

//Returns true iff it is legal to castle the king from (x_o, y_o) to (x_d, y_d);
//Note: It may be legal for the king to castle even if this function returns false,
//namely if it can castle on the other side, or if the origin or destination square is invalid.
Game.prototype.can_castle_king = function(x_o, y_o, x_d, y_d){
  var targetPiece = this.piece_at(x_o, y_o);

  if (targetPiece == null){
    return false;
  }

  //Check that king hasn't moved.
  if (this.king_has_moved(targetPiece.isWhite)){
    return false;
  }

  if (y_d - y_o != 0){
    return false;
  }


  var Side = { "KINGSIDE": 0, "QUEENSIDE": 1};
  var side;
  if (x_d == 3){
    side = Side.QUEENSIDE;
  } else if (x_d == 7){
    side = Side.KINGSIDE;
  } else {
    return false;
  }

  //Check that implicated rook hasn't moved.
  if (this.rook_has_moved((side == Side.QUEENSIDE) ? 1 : 8, targetPiece.isWhite)){
    return false;
  }

  //Check that the squares between are empty
  if (side == Side.QUEENSIDE){
    if (this.piece_at(2, y_d) != null || this.piece_at(3, y_d) != null || this.piece_at(4, y_d) != null){
      return false;
    }
  } else {
    if (this.piece_at(6,y_d) != null || this.piece_at(7,y_d) != null){
      return false;
    }
  }

  //Check that no square between kings current and final location are threatened.
  var byWhite = !targetPiece.isWhite;
  var king_dir;
  if (side == Side.QUEENSIDE){
    king_dir = -1;
  } else {
    king_dir = 1;
  }

  if (this.is_under_attack(5 + 2 * king_dir, y_d, byWhite, true) || this.is_under_attack(5 + king_dir, y_d, byWhite, true) || this.is_under_attack(5, y_d, byWhite, true)){
    return false;
  }

  //Finally,
  return true;
};

Game.prototype.king_has_moved = function(isWhite){
  if (isWhite){
    return this.white_king_has_moved;
  } else {
    return this.black_king_has_moved;
  }
};

Game.prototype.rook_has_moved = function(column, isWhite){
  if (column == 1){
    return isWhite ? this.white_rook_1_has_moved : this.black_rook_1_has_moved;
  } else if (column == 8){
    return isWhite ? this.white_rook_8_has_moved : this.black_rook_8_has_moved;
  } else {
    throw "Column must equal 1 or 8, not " + column;
  }

};

//Requires that this move be valid.
Game.prototype._transport = function(x_o, y_o, x_d, y_d){
  this.board[x_d-1][y_d-1] = this.board[x_o-1][y_o-1];
  this.board[x_o-1][y_o-1] = null;
};

//Requires that the move be either a valid, legal single-space king move,
//or a valid, legal castling move.
Game.prototype._transport_king = function(x_o, y_o, x_d, y_d, castlesCallback){
  var isWhite = this.piece_at(x_o, y_o).isWhite;
  if (Math.abs(x_d-x_o) == 2){
    // Dirty hack to easily communicate to the caller's caller's... caller that
    // this move was a castle.
    if (castlesCallback) {
      castlesCallback();
    }
    //By the requirements of this method, this move is a valid, legal castle.
    this._castle_king(x_o, y_o, x_d, y_d);
  } else {
    //By the requirements of this method, this move is a valid, legal single-space king move.
    this._transport(x_o, y_o, x_d, y_d);
  }
  if (isWhite){
    this.white_king_x = x_d;
    this.white_king_y = y_d;
  } else {
    this.black_king_x = x_d;
    this.black_king_y = y_d;
  }

};

Game.prototype._castle_king = function(x_o, y_o, x_d, y_d){
  var rook_x_o;
  var rook_x_d;
  if (x_d == 7){
    rook_x_o = 8;
    rook_x_d = 6;
  } else if (x_d == 3) {
    rook_x_o = 1;
    rook_x_d = 4;
  } else {
    throw "destination column must be 3 or 7, not " + x_d;
  }

  //Move king
  this._transport(x_o, y_o, x_d, y_d);

  //Move rook
  this._transport(rook_x_o, y_o, rook_x_d, y_d);
};



//Param x the x-coordinate of the square in question.
//Param y the y-coordinate of the square in question.
//Param byWhite: if true, returns whether any white pieces can attack the square.  if false, returns whether any black pieces can attack the square.
//Returns true iff the square (x,y) can be attacked by a the army of the specified color.
Game.prototype.is_under_attack = function(x, y, byWhite, ignore_check){
  var piece;

  //Check if there's any square on the board that has a piece that could attack (x,y).
  //TODO: Loop through all pieces, rather than all squares.
  for (var row = 1; row <= 8; row++){
    for (var col = 1; col <= 8; col++){
      piece = this.piece_at(row, col);
      if (piece == null){
        continue;
      } else if (piece.isWhite != byWhite) {
        continue;
      } else if (piece.type == "pawn"){
        if (this._can_capture_with_pawn_ignoring_check(row, col, x, y, true) && (!this.would_check_own_king(row, col, x, y) || ignore_check)){
          return true;
        }
      }else if (piece.type == "king") {
        /*If we considered castling, then there would be another call to is_under_attack
          somewhere in the can_castle_king stack, possibly leading to infinite recursion.
          Luckily, the king can never capture by castling, so we can ignore castling here
          by setting consider_castling = false in the can_move_king function call.
        */
        if (this._can_move_king_ignoring_check(row, col, x, y, false) && (!this.would_check_own_king(row, col, x, y) || ignore_check)){
          return true;
        }

      } else if (this.can_move(row, col, x, y, ignore_check)){
        return true;
      }
    }
  }
  return false;
};

Game.prototype.makeServerMove = function(moveString) {
  // TODO: Assert correct length.
  // TODO: Handle promotion.
  var startFile = parseInt(moveString.charAt(0), 10);
  var startRank = parseInt(moveString.charAt(1), 10);
  var endFile = parseInt(moveString.charAt(2), 10);
  var endRank = parseInt(moveString.charAt(3), 10);
  if (moveString.length == 5) {
    var typeChar = moveString.charAt(4);
    var type;
    if (typeChar == "N") {
      type = "knight";
    } else if (typeChar == "B") {
      type = "bishop";
    } else if (typeChar == "R") {
      type = "rook";
    } else if (typeChar == "Q") {
      type = "queen";
    } else {
      throw "Invalid promotion type.";
    }
    var promotionCallback = function() {
      return type;
    };
    this.move(startFile, startRank, endFile, endRank, promotionCallback);
  } else {
    this.move(startFile, startRank, endFile, endRank);
  }
};
