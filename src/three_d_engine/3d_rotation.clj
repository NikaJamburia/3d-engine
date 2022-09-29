(ns three-d-engine.3d-rotation
  (:require [three-d-engine.3d-core :refer :all]))

(defn- rotation-matrix-x [theta]
  (let [cos (Math/cos theta)
        sin (Math/sin theta)
        -sin (unchecked-negate (Math/sin theta))]
    [[1 0    0   0]
     [0 cos  sin 0]
     [0 -sin cos 0]
     [0 0    0   1]]))

(defn- rotation-matrix-z [theta]
  (let [cos (Math/cos theta)
        sin (Math/sin theta)
        -sin (unchecked-negate (Math/sin theta))]
    [[cos  sin 0 0]
     [-sin cos 0 0]
     [0    0   1 0]
     [0    0   0 1]]))

(defn- rotation-matrix-y [theta]
  (let [sin (Math/sin  theta)
        -sin (unchecked-negate (Math/sin theta))
        cos (Math/cos theta)]
    [[cos 0 -sin 0]
     [0   1 0    0]
     [sin 0 cos  0]
     [0   0 0    1]]))

(def ^:private rotation-matrices
  {:x rotation-matrix-x
   :y rotation-matrix-y
   :z rotation-matrix-z})

(defn rotation-matrix [axis theta]
 ((get rotation-matrices axis) theta))

(defn- rotate-triangle-by-matrix [tri matrix]
  (assoc tri :vectors (->> (:vectors tri)
                           (map #(multiply-3d-vector-by-matrix % matrix))
                           (vec))))

(defn- rotate-mesh-by-matrix [mesh matrix]
  (assoc mesh :triangles (->> (:triangles mesh)
                              (map #(rotate-triangle-by-matrix % matrix))
                              (vec))))

(defn rotate [mesh matrices]
  (reduce #(rotate-mesh-by-matrix %1 %2) mesh matrices))

