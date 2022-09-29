(ns three-d-engine.3d-rotation
  (:require [three-d-engine.3d-core :refer :all]))

(defn rotation-matrix-x [theta]
  (let [m-1-1 (Math/cos (* 0.5 theta))
        m-1-2 (Math/sin (* 0.5 theta))
        m-2-1 (unchecked-negate (Math/sin (* 0.5 theta)))
        m-2-2 (Math/cos (* 0.5 theta))]
    [[1 0     0     0]
     [0 m-1-1 m-1-2 0]
     [0 m-2-1 m-2-2 0]
     [0 0     0     1]]))

(defn rotation-matrix-z [theta]
  (let [m-0-0 (Math/cos theta)
        m-0-1 (Math/sin theta)
        m-1-0 (unchecked-negate (Math/sin theta))
        m-1-1 (Math/cos theta)]
    [[m-0-0 m-0-1 0 0]
     [m-1-0 m-1-1 0 0]
     [0     0     1 0]
     [0     0     0 1]]))

(defn rotate-triangle [tri theta]
  (assoc tri :vectors (->> (:vectors tri)
                           (map #(multiply-3d-vector-by-matrix % (rotation-matrix-z theta)))
                           (map #(multiply-3d-vector-by-matrix % (rotation-matrix-x theta)))
                           (vec))))

(defn rotate-mesh [mesh theta]
  (let [rotated (->> (:triangles mesh)
                     (map #(rotate-triangle % theta))
                     (vec))]
    (assoc mesh :triangles rotated)))
