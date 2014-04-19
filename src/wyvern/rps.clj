(ns wyvern.rps
  (:use wyvern.types
        wyvern.utils))

;; Currently broken (due to interface changes around views). 

(def ^:const default-play-upto 5)

(defrecord State [points play-upto])

(def outcome
  {[:rock :rock]         :draw
   [:rock :scissors]     :win-0
   [:rock :paper]        :win-1
   [:scissors :rock]     :win-1
   [:scissors :scissors] :draw
   [:scissors :paper]    :win-0
   [:paper :rock]        :win-0
   [:paper :scissors]    :win-1
   [:paper :paper]       :draw})

(defn RPS-move [game-state player-actions]
  (let [result (outcome [(player-actions 0) (player-actions 1)])]
    (case result
      :draw game-state
      :win-0 (update-in game-state [:points 0] inc)
      :win-1 (update-in game-state [:points 1] inc))))

(defrecord RPSGame [state]
  Game
  ; two players named 0 and 1
  (all-players [this] [0 1])
  ; percent information
  (view [this _] state)
  ; actions are :rock, :paper, :scissors
  (legal-actions [this player-id]
    #{:rock :paper :scissors})
  ; a move is one "round" of RPS
  (move [this player-actions]
    (RPSGame. (RPS-move state player-actions)))
  ; game-ending condition is one player winning a certain number of rounds
  (terminal? [this]
    (some (fn [[_id points]] (>= points (:play-upto state)))
          (:points state)))
  (score [this player-id]
    ((:points state) player-id)))

(defn make-RPSGame [& [play-upto]]
  (RPSGame. (State. {0 0.0, 1 0.0} (or play-upto default-play-upto))))

(def Spec
  {:new make-RPSGame
   :n-players [2]
   :impl RPSGame})
