(ns wyvern.utils)

(defn update
  ([map key f] (assoc map key (f (get map key))))
  ([map key f & kfs]
     (if kfs
       (if (next kfs)
         (recur (update map key f) (first kfs) (second kfs) (nnext kfs))
         (throw (IllegalArgumentException. 
                 "update expects event number of arguments, found odd.")))
       (update map key f))))
