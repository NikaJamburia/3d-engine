(ns three-d-engine.3d-projection
  (:require [three-d-engine.3d-core :refer :all]))

(def near 0.1)
(def far 1000)
(def aspect (/ (:height window-size) (:width window-size)))
(def fov 90)
(def translate-z-by (float 8))
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

(defn translate-triangle [tri]
  (create-triangle (vec (map #(translate-z % translate-z-by) (:vectors tri)))))

(defn project-triangle [tri]
  (let [translated (translate-triangle tri)
        lighting-value (get-lighting translated)
        vecs-2d (->> (:vectors translated)
                     (map #(multiply-3d-vector-by-matrix % projection-matrix))
                     (map #(scale-vector %)))]
    (if (is-visible? translated)
      (assoc tri :vectors (vec vecs-2d) :lighting lighting-value)
      (assoc tri :lighting lighting-value))))

(defn project-to-3d [mesh]
  (let [tris (:triangles mesh)
        processed (->> tris
                       (map #(project-triangle %))
                       (sort #(compare-triangles-by-z %1 %2))
                       (vec))]
    (assoc mesh :triangles processed)))
