(ns wyvern.game-state)

;; A game-state/T represents the state of a game. Each game will typically have
;; a state type (e.g. NimType) that represents everything there is to know. 

;; Abstract types

;; PlayerId (typically :random or an integer)
;; Action (unconstrained)

(defprotocol T

  ; GameState => [PlayerId]
  ; Returns a list of all players in the game. 
  (all-players [this])

  ; GameState, PlayerId => GameView
  ; What aspects of game state player-id can see. 
  (view [this player-id])

  ; GameState, PlayerId, Action => Boolean
  ; Legality check for a specific (player, action) pair.  
  (legal-action [this player-id action])

  ; GameState, PlayerId => Action
  ; generates one legal move. No guarantees. Implementation dependent.
  ; if player-id is :random, it will be a random legal move (as per the game's definition).
  (a-legal-move [this player-id])

  ; GameState, {PlayerId, Action} => GameState
  ; map argument must contain an action (possibly :no-op) for each player-id. 
  ; generate a new Game representing the state after the move.
  (move [this player-actions])

  ; GameState => Boolean
  ; Returns true if the game is over.
  (terminal? [this])

  ; GameState, PlayerId => Number
  ; How'd that player do? Only makes sense if (terminal? this) is true.
  (score [this player-id]))

(defn all-scores [game-state]
  (into {} (map (fn [player] [player (score game player)]) (all-players game))))
