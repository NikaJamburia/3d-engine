(ns three-d-engine.3d-core
  (:require [three-d-engine.3d-util :refer :all]))

; CONFIG
(def window-size {:width 1024 :height 768})

; MODEL
(defn vector-3d [x y z]
  {:pre [(check-state #(number? x) (str "All coordinates must be numbers" z))
         (check-state #(number? y) (str "All coordinates must be numbers" z))
         (check-state #(number? z) (str "All coordinates must be numbers" z))]}
  {:x x :y y :z z})

(defn create-triangle [vectors]
  {:pre [(check-state #(= 3 (count vectors)) "Triangle must have 3 vectors!")]}
  {:vectors vectors})

(defn create-mesh [triangles]
  {:triangles triangles})

; CALCULATIONS
(def camera (vector-3d 0 0 0))

(defn multiply-3d-vector-by-matrix [vector matrix]
  (let [w (+
            (* (:x vector) (m matrix 0 3))
            (* (:y vector) (m matrix 1 3))
            (* (:z vector) (m matrix 2 3))
            (m matrix 3 3))
        multiplied {:x (+
                         (* (:x vector) (m matrix 0 0))
                         (* (:y vector) (m matrix 1 0))
                         (* (:z vector) (m matrix 2 0))
                         (m matrix 3 0))
                    :y (+
                         (* (:x vector) (m matrix 0 1))
                         (* (:y vector) (m matrix 1 1))
                         (* (:z vector) (m matrix 2 1))
                         (m matrix 3 1))
                    :z (+
                         (* (:x vector) (m matrix 0 2))
                         (* (:y vector) (m matrix 1 2))
                         (* (:z vector) (m matrix 2 2))
                         (m matrix 3 2))}]
    (if (not= 0 w)
      {:x (/ (:x multiplied) w)
       :y (/ (:y multiplied) w)
       :z (/ (:z multiplied) w)}
      multiplied)))

(defn scale-vector [vector]
  (assoc vector
    :x (* (inc (:x vector)) (* 0.5 (:width window-size)))
    :y (* (inc (:y vector)) (* 0.5 (:height window-size)))))

(defn vector-subtract [vec1 vec2]
  (vector-3d
    (- (:x vec1) (:x vec2))
    (- (:y vec1) (:y vec2))
    (- (:z vec1) (:z vec2))))

(defn calc-cross-product [vec1 vec2]
  (vector-3d
    (- (* (:y vec1) (:z vec2)) (* (:z vec1) (:y vec2)))
    (- (* (:z vec1) (:x vec2)) (* (:x vec1) (:z vec2)))
    (- (* (:x vec1) (:y vec2)) (* (:y vec1) (:x vec2)))))

(defn normalize [vector]
  (let [length (fast-inv-sqrt (+ (square (:x vector)) (square (:y vector)) (square (:z vector)) ))]
    (assoc vector
      :x (* (:x vector) length)
      :y (* (:y vector) length)
      :z (* (:z vector) length))))

(defn calculate-dot-product [vec1 vec2]
  (+
    (* (:x vec1) (:x vec2))
    (* (:y vec1) (:y vec2))
    (* (:z vec1) (:z vec2))))

(defn calculate-triangle-normal [tri]
  (let [vectors (:vectors tri)
        line1 (vector-subtract (second vectors) (first vectors))
        line2 (vector-subtract (third vectors) (first vectors))]
    (normalize (calc-cross-product line1 line2))))

(defn is-visible? [triangle]
  (neg?
    (calculate-dot-product (:normal triangle) (vector-subtract (first (:vectors triangle)) camera))))

(def light-direction (normalize (vector-3d 0 0 1)))
(defn get-lighting [tri]
  (calculate-dot-product (:normal tri) light-direction))

(defn get-z-midpoint [tri]
  (let [vectors (:vectors tri)
        coords-sum (+
                     (:z (first vectors))
                     (:z (second vectors))
                     (:z (third vectors)))]
    (/ coords-sum 3)))

(defn compare-triangles-by-z [tri1 tri2]
  (> (get-z-midpoint tri1) (get-z-midpoint tri2)))