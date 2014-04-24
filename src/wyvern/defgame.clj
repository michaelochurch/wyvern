(ns wyvern.defgame
  (:use wyvern.utils))

(defn- keywordize-map [m]
  (into {} (map (fn [[k v]] [(keyword nil (name k)) v]) m)))

(defn- nil-values [coll]
  (into {} (map (fn [k] [k nil]) coll)))

(defmacro defgame [name & {:keys [constants defaults fields legal legal-1 terminal? score]}]
  (let [all-names-used (concat fields (keys defaults) (keys constants))]
    `(def ~name 
       {:init ~(let [base (merge (keywordize-map (nil-values fields))
                                 (keywordize-map defaults)
                                 (keywordize-map constants))]
                 (fn [& [config]] (merge base config)))
        :legal (fn [{:keys [~@all-names-used] 
                     :as ~(symbol nil "$game-state")} 
                    ~(symbol nil "$player-id")
                    ~(symbol nil "$action")]
                 ~legal)
        :legal-1 (fn [{:keys [~@all-names-used] 
                       :as ~(symbol nil "$game-state")} 
                      ~(symbol nil "$player-id")]
                 ~legal-1)
        :terminal? (fn [{:keys [~@all-names-used]
                         :as ~(symbol nil "$game-state")}]
                    ~terminal?)
        :score (fn [{:keys [~@all-names-used]
                      :as ~(symbol nil "$game-state")}
                     ~(symbol nil "$player-id")]
                 (double-convert-wrapper ~score))})))

(defn- double-convert-wrapper [x]
  (cond (number? x)  (double x)
        (true?  x)   1.0
        (false? x)   0.0
        :else (throw (IllegalArgumentException. "type error in double-convert-wrapper"))))

(defgame make-instance [game-spec]
  ;; Creates a "runnable" instance of the game. 
  )

(defgame nim
  :players [2]
  :fields  [active-player stones-left]
  :defaults {:active-player 0
             :stones-left   16}
  :visible  {:active-player true
             :stones-left   true}
  :constants {max-stones 3}
  :legal     (if (= $player-id active-player)
               (and (number? $action)
                    (<= 1 $action (max max-stones stones-left)))
               (= $action :no-op))
  :legal-1   (if (= $player-id active-player)
               (inc (rand-int (max max-stones stones-left)))
               :no-op)
  :move      :STUB
  :terminal? (= stones-left 0)
  :score     (not= $player-id active-player)) ;; true ~= 1.0, false ~= 0.0
                                              ;; this allows a "win condition" to be the API



(comment
(defgame hearts
  :players [4]
  :random true ;; creates a 5th player ID for :random  
  :fields [... hands ...]
  :visible {:hands (get hands $player-id)}
                   ;; true means the field is visible, false means it's omitted, 
                   ;; a function allows partial visibility (e.g. that player's hand).
))
