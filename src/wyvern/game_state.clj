(ns wyvern.game-state)

(defprotocol T
  ; list of all players (by id) in the game. 
  (all-players [this])
  ; what aspects of game state player-id can see. 
  (view [this player-id])
  ; returns boolean. Legality check. 
  (legal-action [this player-id action])
  ; generates one legal move. No guarantees. Implementation dependent.
  ; if player-id is :random, it will be random as understood by the game.
  (a-legal-move [this player-id])
  ; generate a new Game representing the state after the move.
  (move [this player-actions])
  ; returns boolean. Is the game over?
  (terminal? [this])
  ; how'd that player do? Only makes sense if (terminal? this) is true.
  (score [this player-id]))

(defn all-scores [game]
  (into {} (map (fn [player] [player (score game player)]) (all-players game))))
