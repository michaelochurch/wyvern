(ns wyvern.utils)

(defn get! [m k]
  (let [ret (get m k ::not-found)]
    (if (= ret ::not-found)
      (throw (Exception. (format "get!: map %s missing key %s" m k)))
      ret)))

(defn image-map [f coll]
  (reduce (fn [m x]
            (assoc m x (f x)))
          {}
          coll))

(defn map-values [f m]
  (let [init-val (if (record? m) m (empty m))]
    (reduce (fn [acc [k v]]
              (assoc acc k (f v)))
            init-val
            m)))

(defn update
  ([map key f] (assoc map key (f (get map key))))
  ([map key f & kfs]
     (if kfs
       (if (next kfs)
         (recur (update map key f) (first kfs) (second kfs) (nnext kfs))
         (throw (IllegalArgumentException. 
                 "update expects event number of arguments, found odd.")))
       (update map key f))))
