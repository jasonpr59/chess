//Creates a new Algebraic converter.
//Param: The game to which queries will refer.  This game is never modified by the Algebraic Converter.
function AlgebraicConverter(game){
	this.game = game;
}

AlgebraicConverter.prototype.xpos = function(location){
	if (location.length != 2){
		throw "location must be a two-character string, such as 'e3', but was " + location;
	}
	return location.charCodeAt(0)-96;
};

AlgebraicConverter.prototype.ypos = function(location){
	if (location.length != 2){
		throw "location must be a two-character string, such as 'e3', but was " + location;
	}
	return parseInt(location.charAt(1));
};

AlgebraicConverter.prototype.numberToFile = function(number){
	if (number >= 1 && number <= 8){
		return String.fromCharCode(number + 96);
	} else {
		throw "number must be in [1...8], not " + number;
	}
};

AlgebraicConverter.prototype.algToMove = function(alg){
	//TODO: Implement
	throw "Not yet implemented.";
	
};

AlgebraicConverter.prototype.pieceTypeToLetter = function(type){
	
	switch(type){
	case "pawn":
		return "";
		break;
	case "rook":
		return "R";
		break;
	case "knight":
		return "N";
		break;
	case "bishop":
		return "B";
		break;
	case "queen":
		return "Q";
		break;
	case "king":
		return "K";
		break;
	default:
		throw "Unrecognized piece type: " + move.moved_piece.type;
	}
};

AlgebraicConverter.prototype.moveToAlg = function(move){
	
	if (move.moved_piece.type == "king" && move.x_o == 5){
		if (move.x_d == 7){
			//Kingside Castle
			return "O-O";
		} else if (move.x_d == 3) {
			//Queenside Castle
			return "O-O-O";
		}
		//Else, it's a normal king move, and will be handled below.
	}
	
	//Override for promotions... because the piece WAS a pawn when it moved!
	var piecePrefix = move.promoted ? "" : this.pieceTypeToLetter(move.moved_piece.type);

	
	
	//TODO: Finish Implementing Disambiguation
	var disambiguationPrefix = "";
	
	if ((move.moved_piece.type == "pawn" || move.promoted) && move.captured_piece != null){
		disambiguationPrefix = this.numberToFile(move.x_o);
	}
	
	
	
	var capturePrefix = "";
	if (move.captured_piece != null){
		capturePrefix = "x";
	}

	var promotionSuffix = "";
	if (move.promoted){
		promotionSuffix = "(" + this.pieceTypeToLetter(move.moved_piece.type) + ")";
	}
	
	var checkSuffix = "";
	var opposite_king_x;
	var opposite_king_y;
	if (move.moved_piece.isWhite){
		opposite_king_x = game.black_king_x;
		opposite_king_y = game.black_king_y;
	} else {
		opposite_king_x = game.white_king_x;
		opposite_king_y = game.white_king_y;
	}
	
	if (this.game.is_under_attack(opposite_king_x, opposite_king_y, move.moved_piece.isWhite, true)){
		checkSuffix = "+";
	}
	
	//TODO: Check if is checkmate, ie if checkSuffix should be "#"
	
	
	return piecePrefix + disambiguationPrefix + capturePrefix + this.numberToFile(move.x_d) + move.y_d + promotionSuffix + checkSuffix;
};