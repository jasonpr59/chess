function History(){
	this.history = new Array();
	this.alg_history = new Array();
}
//Param move: The Move we wish to add.
//Param alg_move: the algebraic description of the move.
//Note: We require the caller to calculate alg_move for us, since it depends on the current
//state of the board.  Note: we may, at some point, wish for History to keep its own representation
// of the current board, so that it can calculate alg_move on its own.
History.prototype.add_move = function(move, alg_move){
	this.history.push(move);
	this.alg_history.push(alg_move);
};

History.prototype.length = function(){
	return this.history.length;
};

//Returns the i'th Move played.
// Thus, if a game begins 1. e4 e5 2. f4 ..., get_move[1] returns the move corresponding to "e5"
// and get_move[2] returns the move corresponding to "f4".
History.prototype.get_move = function(i){
	return this.history[i];
};

//Returns the i'th move played, in algebraic form
//Thus, if a game begins 1. e4 e5 2. f4 ..., get_move[1] returns "e5"
//and get_move[2] returns "f4".
History.prototype.get_alg_move = function(i){
	return this.alg_history[i];
};