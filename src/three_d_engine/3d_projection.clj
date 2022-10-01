(ns three-d-engine.3d-projection
  (:require [three-d-engine.3d-core :refer :all]))

(def near 0.1)
(def far 1000)
(def aspect (/ (:height window-size) (:width window-size)))
(def fov 90)
(def view-space-scale (/ far (- far near)))
(def fov-rad (/ 1 (Math/tan (* fov (* (float 3.14159) (/ 0.5 180))))))

(def projection-matrix
  (let [m-0-0 (* aspect fov-rad)
        m-1-1 fov-rad
        m-2-2 view-space-scale
        m-3-2 (unchecked-negate (* view-space-scale near))]
    [[m-0-0 0 0 0]
     [0 m-1-1 0 0]
     [0 0 m-2-2 1]
     [0 0 m-3-2 0]]))

(defn translate-z [vec-3d amount]
  (assoc vec-3d :z (+ amount (:z vec-3d))))

(defn translate-triangle [tri value]
  (assoc tri :vectors (vec (map #(translate-z % value) (:vectors tri)))))

(defn assign-normal [tri]
  (assoc tri :normal (calculate-triangle-normal tri)))

(defn project-triangle [tri]
  (assoc tri :vectors (->> (:vectors tri)
                           (map #(multiply-3d-vector-by-matrix % projection-matrix))
                           (map scale-vector)
                           (vec))))

(defn illuminate [tri]
  (assoc tri :lighting (get-lighting tri)))

(defn project-to-3d [mesh z-translation]
  (assoc mesh :triangles (->> (:triangles mesh)
                              (map #(translate-triangle % z-translation))
                              (map assign-normal)
                              (filter is-visible?)
                              (map illuminate)
                              (map project-triangle)
                              (sort compare-triangles-by-z)
                              (vec))))
