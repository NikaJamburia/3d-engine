(ns three-d-engine.3d-util)

(defn m [matrix i1 i2]
  (get (get matrix i1) i2))

(defn third [collection]
  (get collection 2))

(defn square [n]
  (* n n))

(defn check-state [boolean-func ^String message]
  (if (= true (boolean-func))
    true (throw (IllegalStateException. message))))