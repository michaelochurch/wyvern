(ns wyvern.defgame)

(comment
(defgame nim
  :players [2]
  :fields  [active-player stones-left]
  :const-fields {max-stones 3}
  :defaults {:active-player 0
             :stones-left   16}
  :visible  {:active-player true
             :stones-left   true}
  :legal     (if (= $player-id active-player)
               (and (number? $action)
                    (<= 1 $action (max max-stones stones-left)))
               :no-op)
  :legal-1   (if (= $player-id active-player)
               (inc (rand-int (max max-stones stones-left)))
               :no-op)
  :terminal? (= stones-left 0)
  :score     (= $player-id active-player)) ;; true ~= 1.0, false ~= 0.0
)

(comment
(defgame hearts
  :players [4]
  :random true ;; creates a 5th player ID for :random  
  :fields [... hands ...]
  :visible {:hands (get hands $player-id)}
                   ;; true means the field is visible, false means it's omitted, 
                   ;; a function allows partial visibility (e.g. that player's hand).
)
