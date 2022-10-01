(ns three-d-engine.3d-projection
  (:require [three-d-engine.3d-core :refer :all]
            [three-d-engine.3d-util :refer :all]))

(def default-projection-params {:window-size {:width 1024 :height 768}
                                :fov 90
                                :near 0.1
                                :far 1000
                                :z-translation 8})

(defn generate-projection-matrix [params]
  (let [window-size (:window-size params)
        aspect (/ (:height window-size) (:width window-size))
        fov-rad (/ 1 (Math/tan (to-radians (* 0.5 (:fov params)))))
        view-space-scale (/ (:far params) (- (:far params) (:near params)))]
    (let [m-0-0 (* aspect fov-rad)
          m-1-1 fov-rad
          m-2-2 view-space-scale
          m-3-2 (unchecked-negate (* view-space-scale (:near params)))]
      [[m-0-0 0 0 0]
       [0 m-1-1 0 0]
       [0 0 m-2-2 1]
       [0 0 m-3-2 0]])))

(defn- translate-z [vec-3d amount]
  (assoc vec-3d :z (+ amount (:z vec-3d))))

(defn- translate-triangle [tri value]
  (assoc tri :vectors (vec (map #(translate-z % value) (:vectors tri)))))

(defn- assign-normal [tri]
  (assoc tri :normal (calculate-triangle-normal tri)))

(defn- project-triangle [tri matrix window-size]
  (assoc tri :vectors (->> (:vectors tri)
                           (map #(multiply-3d-vector-by-matrix % matrix))
                           (map #(scale-vector % window-size))
                           (vec))))

(defn- illuminate [tri]
  (assoc tri :lighting (get-lighting tri)))

(defn project-to-3d
  ([mesh params]
    (let [projection-matrix (generate-projection-matrix params)]
      (assoc mesh :triangles (->> (:triangles mesh)
                                  (map #(translate-triangle % (:z-translation params)))
                                  (map assign-normal)
                                  (filter is-visible?)
                                  (map illuminate)
                                  (map #(project-triangle % projection-matrix (:window-size params)))
                                  (sort compare-triangles-by-z)
                                  (vec)))))
  ([mesh]
    (project-to-3d mesh default-projection-params)))