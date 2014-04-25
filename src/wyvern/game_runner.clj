(ns wyvern.game-runner
  (:use wyvern.utils))

(def ^:dynamic *verbose* false)

(defn set-verbose [x]
  (alter-var-root #'*verbose* (fn [_] x)))

(defn default-player-ids [n-players]
  (into [] (range n-players)))

(defn default-player-fns [game player-ids]
  (image-map (fn [id]
               (fn [view] ((:legal-1 game) view id)))
             player-ids))

(defn- print-view [player-id view]
  (printf "Player %s sees: %s\n" player-id (pr-str view)))

(defn default-on-view [player-ids]
  (image-map (fn [id]
               #(print-view id %))
             player-ids))

(defn game-instance [game & {:keys [n-players config player-ids player-fns on-view]}]
  (let [n-players (or n-players (first (:n-players game)))
        player-ids (or player-ids (default-player-ids n-players))]
    {:game game
     :state ((:init game) config)
     :player-ids player-ids
     :player-fns (or player-fns (default-player-fns game player-ids))
     :on-view    (or on-view    (default-on-view player-ids))}))

(defn run-game-instance [game-instance]
)


